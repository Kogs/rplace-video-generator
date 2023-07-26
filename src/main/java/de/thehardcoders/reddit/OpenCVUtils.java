package de.thehardcoders.reddit;

import org.opencv.core.Point;
import org.opencv.core.Size;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenCVUtils {

    public static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }
    public static Point add(Point a, Size b) {
        return new Point(a.x + b.width, a.y + b.height);
    }

}
