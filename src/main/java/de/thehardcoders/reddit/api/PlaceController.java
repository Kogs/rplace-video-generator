package de.thehardcoders.reddit.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thehardcoders.reddit.api.dto.VideoRequestDto;
import de.thehardcoders.reddit.video.VideoService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("place")
@AllArgsConstructor
public class PlaceController {
    
    private final VideoService videoService;

    @PostMapping("video")
    public String getVideo(@RequestBody VideoRequestDto dto) {
        return videoService.generateVideo(dto);
    }

    @PostMapping("preview")
    public byte[] getPreview(@RequestBody VideoRequestDto dto) {
        return videoService.generatePreview(dto);
    }

    @GetMapping()
    public String get() throws IOException {
        ClassPathResource resource = new ClassPathResource("ui/index.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }


}
