package de.thehardcoders.reddit.video;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

import org.apache.commons.lang3.time.StopWatch;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;
import org.springframework.stereotype.Service;

import de.thehardcoders.reddit.api.dto.VideoRequestDto;
import de.thehardcoders.reddit.data.PixelService;
import de.thehardcoders.reddit.data.entity.PlacePixel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final PixelService pixelService;

    public String generateVideo(VideoRequestDto request) {
        log.info("Generate Video: {}", request);
        String outputFile =  "output.mp4";
        int fps = 30;
        Size size = new Size(request.getX().size(), request.getY().size());
        Size scaledSize = new Size(size.width * request.getScale(), size.height *  request.getScale());
        final VideoWriter writer = new VideoWriter(
            outputFile, 
            VideoWriter.fourcc('h', '2', '6', '2'),
            fps,
            scaledSize,
            true
        );
        log.info("Read first frame data");
        byte[] firstFrameData = pixelService.getFrameAt(request.getFrom(), request.getX(), request.getY());
        Mat frame = toFrame(size, firstFrameData);

        long seconds = ChronoUnit.SECONDS.between(request.getFrom(), request.getTo());
        long frames = request.getVideoLenghtSeconds() * fps;
        long realSecondsPerFrame = seconds / frames;

        log.info("Frames to write: {}. RealTimeSecondsPerFrame: {}", frames, realSecondsPerFrame);



        Instant currentTime = request.getFrom();
        Instant lastFrame = currentTime;
        // write first frame
        writeFrame(scaledSize, writer, frame, request.getFrom());

        StopWatch writeWatch = StopWatch.createStarted(); writeWatch.suspend();
        StopWatch readWatch = StopWatch.createStarted(); readWatch.suspend();

        for (int i = 0; i < frames; i++) {
            currentTime = currentTime.plusSeconds(realSecondsPerFrame);
            // will be faster if multiple frames are geather by once.
            readWatch.resume();
            Collection<PlacePixel> pixels = pixelService.getChangedPixels(lastFrame, currentTime, request.getX(), request.getY());
            readWatch.suspend();

            writeWatch.resume();
            // TODO: maybe its faster to fetch the full color data first
            for (PlacePixel pixel : pixels) {
                int x = pixel.getX() - request.getX().getMin();
                int y = pixel.getY() - request.getY().getMin();
                frame.put(y, x, pixelService.toRgb(pixel.getColor()));
            }
            writeFrame(scaledSize, writer, frame, currentTime);
            writeWatch.suspend();

            lastFrame = currentTime;
        }
        log.info("Video created. Read: {}ms. Write {}ms", readWatch.getTime(), writeWatch.getTime());

        writer.release();
        return outputFile;
    }


    private void writeFrame(Size scaledSize, final VideoWriter writer, Mat frame, Instant currentTime) {
        Mat frameToWrite = resize(frame, scaledSize);
        addText(frameToWrite, currentTime);
        writer.write(frameToWrite);
    }

    private Mat toFrame(Size size, byte[] bytes) {
        Mat frame = new Mat(size, CvType.CV_8UC3);
        frame.put(0, 0, bytes);
        return frame;
    }

    private Mat resize(Mat frame, Size targetSize) {
        Mat scaledFrame = new Mat(targetSize, CvType.CV_8UC3);
        Imgproc.resize(frame, scaledFrame, targetSize, 0,0 , Imgproc.INTER_AREA);
        return scaledFrame;
    }

    private void addText(Mat frame, Instant currentTime) {
        Imgproc.putText(frame, currentTime.toString(),
                new Point(0, 22), Imgproc.FONT_HERSHEY_SIMPLEX, 1,
                Scalar.all(256), 2);
    }



}
