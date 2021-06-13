package com.metcs767.ahajj;

import java.util.HashMap;
import java.util.Map;

/**
 * Class is taken from StackOverflow
 * Useful for generating a distributed random number
 * For when we select parents
 * 
 * https://stackoverflow.com/questions/20327958/random-number-with-probabilities
 * 
 * @author trylimits (user on stackoverflow)
 *
 */

public class DistributedRandomNumberGenerator {

    private Map<Integer, Double> distribution;
    private double distSum;

    public DistributedRandomNumberGenerator() {
        distribution = new HashMap<>();
    }

    public void addNumber(int value, double distribution) {
        if (this.distribution.get(value) != null) {
            distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        distSum += distribution;
    }

    public int getDistributedRandomNumber() {
        double rand = Math.random();
        double ratio = 1.0f / distSum;
        double tempDist = 0;
        for (Integer i : distribution.keySet()) {
            tempDist += distribution.get(i);
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        return 0;
    }

}