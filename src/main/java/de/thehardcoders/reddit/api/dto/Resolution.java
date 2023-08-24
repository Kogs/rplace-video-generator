package de.thehardcoders.reddit.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Resolution {

    FULL_HD(1920, 1080),
    ULTRA_HD(4096, 3072);

    private int width;
    private int height;

}
