package de.thehardcoders.reddit.video;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import nu.pattern.OpenCV;

public class RedditVideoGenerator {

    public static void main(String[] args) {
        OpenCV.loadShared();
        createVideo(1000, 1000, 5, 100);
    }

    private static void createVideo(int width, int height, double fps, int frames) {
        Size frameSize = new Size(width, height);
        boolean isRGB = true;
        final VideoWriter writer = new VideoWriter("output.mp4", VideoWriter.fourcc('h', '2', '6', '2'), fps, frameSize, isRGB);
        for (int f = 0; f < frames; f++) {
            Mat frame = fillFrame(new Mat(width, height, CvType.CV_8UC3));
            writer.write(frame);
        }
        writer.release();
    }

    private static Mat fillFrame(Mat frame) {
        int rows = frame.rows();
        int cols = frame.cols();
        int ch = 3; // frame.channels(); 

        byte random = (byte) (Math.random() * 256);
        byte random2 = (byte) (Math.random() * 256);
        byte random3 = (byte) (Math.random() * 256);

        byte buff[] = new byte[rows * cols * ch];
        frame.get(0, 0, buff);
        for (int i = 0; i < buff.length; i += ch) {
            buff[i] = random;
            buff[i + 1] = random2;
            buff[i + 2] = random3;
        }
        frame.put(0, 0, buff);

        return frame;
    }

}
