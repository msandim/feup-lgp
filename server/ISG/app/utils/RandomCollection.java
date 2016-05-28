package utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by Miguel on 06-05-2016.
 */
// Source: http://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
public class RandomCollection<E> {
    private final NavigableMap<Float, E> map = new TreeMap<>();
    private final Random random;
    private Float total = (float) 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public void add(Float weight, E result) {
        if (weight < 0) return;
        total += weight;
        map.put(total, result);
    }

    public E next() {
        Float value = random.nextFloat() * total;
        return map.ceilingEntry(value).getValue();
    }
}