package de.thehardcoders.reddit.data.repository;

import java.time.Instant;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.thehardcoders.reddit.data.entity.PlacePixel;
import de.thehardcoders.reddit.data.entity.PlacePixel.PlacePixelId;

public interface PlacePixelRepository extends JpaRepository<PlacePixel, PlacePixelId> {

    @Query(
        "select p from PlacePixel p " +
        "where p.id.timestamp between :start and :end " +
        "and p.id.x between :minX and :maxX " +
        "and p.id.y between :minY and :maxY "
    )
    public Collection<PlacePixel> findByTimestampAndPixel(Instant start, Instant end, int minX, int maxX, int minY, int maxY);

    @Query(value =
        "select distinct on (x, y) * from place_pixel " +
        "where timestamp between :start and :end " +
        "and x between :minX and :maxX " +
        "and y between :minY and :maxY " +
        "order by x, y, timestamp",
        nativeQuery = true
    )
    public Collection<PlacePixel> findChangedInRange(Instant start, Instant end, int minX, int maxX, int minY, int maxY);


    @Query(value =
        "select distinct on (x, y) * from place_pixel " +
        "where timestamp <= :time " +
        "and x between :minX and :maxX " +
        "and y between :minY and :maxY " +
        "order by x, y, timestamp",
        nativeQuery = true
    )
    public Collection<PlacePixel> findFullFrameAt(Instant time, int minX, int maxX, int minY, int maxY);

}
