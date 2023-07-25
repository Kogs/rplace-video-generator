package de.thehardcoders.reddit.data;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import de.thehardcoders.reddit.api.dto.Range;
import de.thehardcoders.reddit.data.entity.PlacePixel;
import de.thehardcoders.reddit.data.repository.PlacePixelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PixelService {

    private final PlacePixelRepository repository;

    public Collection<PlacePixel> getPixels(Instant start, Instant end, Range x, Range y) {
        return this.repository.findByTimestampAndPixel(start, end, x.getMin(), x.getMax(), y.getMin(), y.getMax());
    }

    public Collection<PlacePixel> getChangedPixels(Instant start, Instant end, Range x, Range y) {
        return this.repository.findChangedInRange(start, end, x.getMin(), x.getMax(), y.getMin(), y.getMax());
    }

    public byte[] getFrameAt(Instant time, Range xRange, Range yRange) {
        Collection<PlacePixel> frame = this.repository.findFullFrameAt(
            time,
            xRange.getMin(), xRange.getMax(),
            yRange.getMin(), yRange.getMax()
        );
        int xOffset = xRange.getMin();
        int yOffset = yRange.getMin();
        Map<Pair<Integer, Integer>, PlacePixel> map = frame.stream().collect(Collectors.toMap(
            (p) -> Pair.of(
                p.getX() - xOffset,
                p.getY() - yOffset
            ),
            Function.identity())
        );
        int width = xRange.size();
        int height = yRange.size();
        byte[] pixels = new byte[width * height * 3];

        for (int i = 0; i < pixels.length; i += 3) {
            int index = i / 3;

            int x = index % width;
            int y = index / width;
            PlacePixel placePixel = map.get(Pair.of(x, y));

            byte r = (byte)255;
            byte g = (byte)255;
            byte b = (byte)255;

            if (placePixel != null) {
                int color = placePixel.getColor();
                r = (byte)((color >> 0) & 0xFF);
                g = (byte)((color >> 8) & 0xFF);
                b = (byte)((color >> 16) & 0xFF);
            }

            pixels[i] = r;
            pixels[i + 1] = g;
            pixels[i + 2] = b;
        }

        return pixels;
    }

    public byte[] toRgb(int color) {
        return new byte[]{
            (byte)((color >> 0) & 0xFF),
            (byte)((color >> 8) & 0xFF),
            (byte)((color >> 16) & 0xFF),
        };
    }

}
