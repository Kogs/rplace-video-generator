package de.thehardcoders.reddit.api.dto;

import java.time.Instant;


import lombok.Data;

@Data
public class VideoRequestDto {

    private Instant from;

    private Instant to;

    private Range x;

    private Range y;

    private int scale;

    private int videoLenghtSeconds;

    private String filename;

    private int fps = 30;

}
