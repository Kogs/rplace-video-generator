package de.thehardcoders.reddit.api;

import java.io.OutputStream;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thehardcoders.reddit.api.dto.Range;
import de.thehardcoders.reddit.api.dto.VideoRequestDto;
import de.thehardcoders.reddit.data.PixelService;
import de.thehardcoders.reddit.video.VideoService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("place")
@AllArgsConstructor
public class PlaceController {
    
    private final PixelService service;
    private final VideoService videoService;
    
    @GetMapping("pixel")
    public Object getPixel() {
        return service.getPixels(Instant.ofEpochSecond(0), Instant.now(), Range.MAX, Range.MAX);
    }

    @PostMapping("video")
    public String getVideo(@RequestBody VideoRequestDto dto) {
        return videoService.generateVideo(dto);
    }
    
}
