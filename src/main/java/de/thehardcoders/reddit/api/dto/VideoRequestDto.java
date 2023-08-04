package de.thehardcoders.reddit.api.dto;

import java.time.Instant;


import lombok.Data;

@Data
public class VideoRequestDto {

    private Instant from;

    private Instant to;

    private int x;

    private int y;

    private Resolution resolution;

    private boolean vertical;

    private double scale;

    private int videoLenghtSeconds;

    private String filename;

    private int fps = 30;

}
