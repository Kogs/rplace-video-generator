package de.thehardcoders.reddit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import nu.pattern.OpenCV;

@SpringBootApplication
public class PlaceApplication {

    public static void main(String[] args) {
        OpenCV.loadShared();
        SpringApplication.run(PlaceApplication.class, args);
    }

}
