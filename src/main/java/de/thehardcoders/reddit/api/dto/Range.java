package de.thehardcoders.reddit.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Range {
    public static final Range MAX = new Range(Integer.MIN_VALUE, Integer.MAX_VALUE);

    private int min;
    private int max;
    public int size() {
        return Math.abs(max - min);
    }
}
