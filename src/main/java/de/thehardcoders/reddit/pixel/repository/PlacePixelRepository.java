package de.thehardcoders.reddit.pixel.repository;

import java.time.Instant;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.thehardcoders.reddit.pixel.entity.PlacePixel;
import de.thehardcoders.reddit.pixel.entity.PlacePixel.PlacePixelId;

public interface PlacePixelRepository extends JpaRepository<PlacePixel, PlacePixelId> {


    @Query(value =
        "select distinct on (x, y) * from place_pixel " +
        "where timestamp between :start and :end " +
        "and x between :minX and :maxX " +
        "and y between :minY and :maxY " +
        "order by x, y, timestamp desc",
        nativeQuery = true
    )
    public Collection<PlacePixel> findChangedInRange(Instant start, Instant end, int minX, int maxX, int minY, int maxY);


    @Query(value =
        "select distinct on (x, y) * from place_pixel " +
        "where timestamp <= :time " +
        "and x between :minX and :maxX " +
        "and y between :minY and :maxY " +
        "order by x, y, timestamp desc",
        nativeQuery = true
    )
    public Collection<PlacePixel> findFullFrameAt(Instant time, int minX, int maxX, int minY, int maxY);

}
