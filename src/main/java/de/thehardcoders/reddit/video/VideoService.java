package de.thehardcoders.reddit.video;

import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

import de.thehardcoders.reddit.OpenCVUtils;
import de.thehardcoders.reddit.api.dto.VideoRequestDto;
import de.thehardcoders.reddit.pixel.PixelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final PixelService pixelService;

    public String generateVideo(VideoRequestDto request) {
        log.info("Generate Video: {}", request);
        try (VideoCreator creator = new VideoCreator(pixelService, request)) {
            creator.generate();
            return creator.getOutputfile();
        }
    }

    public byte[] generatePreview(VideoRequestDto request) {
         try (VideoCreator creator = new VideoCreator(pixelService, request)) {
            Mat generatePreviewFrame = creator.generatePreviewFrame(0.5);
            // OpenCV mat to png byte array
            return OpenCVUtils.matToPng(generatePreviewFrame);
        }
    }

}
