package de.thehardcoders.reddit.pixel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import de.thehardcoders.reddit.api.dto.Range;
import de.thehardcoders.reddit.pixel.entity.PlacePixel;
import de.thehardcoders.reddit.pixel.repository.PlacePixelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PixelService {

    private final PlacePixelRepository repository;

    public Collection<PlacePixel> getChangedPixels(Instant start, Instant end, Range x, Range y) {
        return this.repository.findChangedInRange(start, end, x.getMin(), x.getMax(), y.getMin(), y.getMax());
    }

    public Mat getFrameAt(Instant time, Range xRange, Range yRange) {
        Collection<PlacePixel> frame = this.repository.findFullFrameAt(
            time,
            xRange.getMin(), xRange.getMax(),
            yRange.getMin(), yRange.getMax()
        );
        //Collection<PlacePixel> frame = new ArrayList<>();
        return toRgb(frame, xRange.size(), yRange.size(), xRange.getMin(), yRange.getMin());
    }

    public Mat toRgb(Collection<PlacePixel> pixels, int width, int height, int xOffset, int yOffset) {
        log.info("toRgb: {}x{} {}x{}", width, height, xOffset, yOffset);

        Mat frame = new Mat(new Size(width, height), CvType.CV_8UC3);
        Imgproc.rectangle(frame, new Point(0,0), new Point(width, height), Scalar.all(256), -1);
        
        for (PlacePixel pixel : pixels) {
            frame.put(pixel.getY() - yOffset, pixel.getX() - xOffset, toRgb(pixel.getColor()));
        }

        return frame;
    }

    public byte[] toRgb(int color) {
        return new byte[]{
            (byte)((color >> 0) & 0xFF),
            (byte)((color >> 8) & 0xFF),
            (byte)((color >> 16) & 0xFF),
        };
    }

    private void putPixel(byte[] pixels, int index, byte[] pixel) {
        pixels[index] = pixel[0];
        pixels[index + 1] = pixel[1];
        pixels[index + 2] = pixel[2];
    }

}
