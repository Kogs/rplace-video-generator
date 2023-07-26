package de.thehardcoders.reddit.pixel;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import de.thehardcoders.reddit.api.dto.Range;
import de.thehardcoders.reddit.pixel.entity.PlacePixel;
import de.thehardcoders.reddit.pixel.repository.PlacePixelRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PixelService {

    private final PlacePixelRepository repository;

    public Collection<PlacePixel> getChangedPixels(Instant start, Instant end, Range x, Range y) {
        return this.repository.findChangedInRange(start, end, x.getMin(), x.getMax(), y.getMin(), y.getMax());
    }

    public byte[] getFrameAt(Instant time, Range xRange, Range yRange) {
        Collection<PlacePixel> frame = this.repository.findFullFrameAt(
            time,
            xRange.getMin(), xRange.getMax(),
            yRange.getMin(), yRange.getMax()
        );
        return toRgb(frame, xRange.size(), yRange.size(), xRange.getMin(), yRange.getMin());
    }

    public byte[] toRgb(Collection<PlacePixel> pixels, int width, int height, int xOffset, int yOffset) {
        Map<Pair<Integer, Integer>, PlacePixel> map = pixels.stream().collect(Collectors.toMap(
            (p) -> Pair.of(
                p.getX() - xOffset,
                p.getY() - yOffset
            ),
            Function.identity())
        );
      
        byte[] data = new byte[width * height * 3];

        for (int i = 0; i < data.length; i += 3) {
            int index = i / 3;

            int x = index % width;
            int y = index / width;
            PlacePixel placePixel = map.get(Pair.of(x, y));

            if (placePixel != null) {
                putPixel(data, i, toRgb(placePixel.getColor()));
            } else {
                putPixel(data, i, new byte[]{ (byte)255 , (byte)255, (byte)255});
            }
        }

        return data;
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
