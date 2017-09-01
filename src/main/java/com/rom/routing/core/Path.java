package com.rom.routing.core;

/**
 * @author Roman Katerinenko
 */
public class Path {
    private final String fromStation;
    private final String toStation;
    private final int travelTime;

    public Path(String fromStation, String toStation, int travelTime) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.travelTime = travelTime;
    }

    public String getFromStation() {
        return fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public int getTravelTime() {
        return travelTime;
    }
}