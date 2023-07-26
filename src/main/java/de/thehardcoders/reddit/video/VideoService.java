package de.thehardcoders.reddit.video;

import org.springframework.stereotype.Service;

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

}
