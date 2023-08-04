package de.thehardcoders.reddit.video;

import java.io.Closeable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.apache.commons.lang3.time.StopWatch;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import de.thehardcoders.reddit.OpenCVUtils;
import de.thehardcoders.reddit.api.dto.Range;
import de.thehardcoders.reddit.api.dto.VideoRequestDto;
import de.thehardcoders.reddit.pixel.PixelService;
import de.thehardcoders.reddit.pixel.entity.PlacePixel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VideoCreator implements Closeable {

    private final PixelService pixelService;

    private final VideoWriter writer;

    private final VideoRequestDto request;

    private final Range x;

    private final Range y;

    private final Size size;

    private final Size scaledSize;

    private final String outputFile;

    private final int fps;

    public VideoCreator(
        PixelService pixelService,
        VideoRequestDto request
    ) {
        this.pixelService = pixelService;
        this.request = request;
        this.fps = this.request.getFps();
        this.outputFile = request.getFilename();
        if (!request.isVertical()) {
            this.scaledSize = new Size(request.getResolution().getWidth(), request.getResolution().getHeight());
        } else {
            this.scaledSize = new Size(request.getResolution().getHeight(), request.getResolution().getWidth());
        }
        this.size = new Size(
             this.scaledSize.width / request.getScale(),
            this.scaledSize.height / request.getScale()
        );
        int halfWidth = (int) size.width / 2;
        int halfHeight = (int) size.height / 2;
        this.x = new Range(request.getX() - halfWidth, request.getX() + halfWidth);
        this.y = new Range(request.getY() - halfHeight, request.getY() + halfHeight);

        this.writer = new VideoWriter(
            outputFile, 
            VideoWriter.fourcc('h', '2', '6', '2'),
            fps,
            scaledSize,
            true
        );
    }

    public void generate() {
        log.info("Read first frame data");
        byte[] firstFrameData = pixelService.getFrameAt(request.getFrom(), x, y);
        Mat frame = toFrame(size, firstFrameData);

        long seconds = ChronoUnit.SECONDS.between(request.getFrom(), request.getTo());
        long frames = request.getVideoLenghtSeconds() * fps;
        long realSecondsPerFrame = seconds / frames;

        log.info("Frames to write: {}. RealTimeSecondsPerFrame: {}", frames, realSecondsPerFrame);
        Instant currentTime = request.getFrom();
        Instant lastFrame = currentTime;
        // write first frame
        writeFrame(scaledSize, frame, request.getFrom());

        StopWatch writeWatch = StopWatch.createStarted(); writeWatch.suspend();
        StopWatch readWatch = StopWatch.createStarted(); readWatch.suspend();

        for (int i = 0; i < frames; i++) {
            currentTime = currentTime.plusSeconds(realSecondsPerFrame);
            // will be faster if multiple frames are geather by once.
            readWatch.resume();
            Collection<PlacePixel> pixels = pixelService.getChangedPixels(lastFrame, currentTime, this.x, this.y);
            readWatch.suspend();

            writeWatch.resume();
            // TODO: maybe its faster to fetch the full color data first
            for (PlacePixel pixel : pixels) {
                int x = pixel.getX() - this.x.getMin();
                int y = pixel.getY() - this.y.getMin();
                frame.put(y, x, pixelService.toRgb(pixel.getColor()));
            }
            writeFrame(scaledSize, frame, currentTime);
            writeWatch.suspend();

            lastFrame = currentTime;
        }
        log.info("Video created. Read: {}ms. Write {}ms", readWatch.getTime(), writeWatch.getTime());
    }


    private void writeFrame(Size scaledSize, Mat frame, Instant currentTime) {
        Mat frameToWrite = resize(frame, scaledSize);
        addTextWithBackground(frameToWrite, new Point(5, frameToWrite.size().height - 10), currentTime.toString());
        addTextWithBackground(frameToWrite, new Point(frameToWrite.size().width - 290, frameToWrite.size().height - 10), "yt/@RPlaceShorts");

        writer.write(frameToWrite);
    }

    private Mat toFrame(Size size, byte[] bytes) {
        Mat frame = new Mat(size, CvType.CV_8UC3);
        frame.put(0, 0, bytes);
        return frame;
    }

    private Mat resize(Mat frame, Size targetSize) {
        Mat scaledFrame = new Mat(targetSize, CvType.CV_8UC3);
        Imgproc.resize(frame, scaledFrame, targetSize, 0, 0, Imgproc.INTER_AREA);
        return scaledFrame;
    }

    private void addTextWithBackground(Mat frame, Point point, String text) {  
        addTextWithBackground(frame, point, text, Scalar.all(256), Scalar.all(0), Imgproc.FONT_HERSHEY_SIMPLEX, 1, 2);
    }

    private void addTextWithBackground(Mat frame,
        Point point, String text, Scalar color, Scalar backgroundColor,
        int fontFace, double fontscale, int thickness
    ) { 
        Size textSize = Imgproc.getTextSize(text, fontFace, fontscale, thickness, null);

        Imgproc.rectangle(frame, 
            new Point(point.x - 5, point.y + 10), 
            new Point(point.x + textSize.width + 5, point.y - textSize.height - 10), 
            backgroundColor, -1);
        Imgproc.putText(frame, text, point, fontFace, fontscale, color, thickness);
    }



    @Override
    public void close() {
        if (writer != null) {
            writer.release();
        }
    }

    public String getOutputfile() {
        return outputFile;
    }


}
