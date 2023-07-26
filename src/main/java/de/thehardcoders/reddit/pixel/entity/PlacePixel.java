package de.thehardcoders.reddit.pixel.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "place_pixel")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlacePixel {

    @Data
    @Embeddable
    public static class PlacePixelId implements Serializable {
        private Instant timestamp;
        private int x;
        private int y;
        private String userId;
    }

    @EmbeddedId
    @EqualsAndHashCode.Include
    private PlacePixelId id;

    private int color;

    public int getX() {
        return id.x;
    }

    public int getY() {
        return id.y;
    }

}
