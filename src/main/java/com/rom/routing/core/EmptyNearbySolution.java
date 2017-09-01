package com.rom.routing.core;

/**
 * Corresponds to unsuccessful {@link NearbyQuery}
 *
 * @author Roman Katerinenko
 */
public class EmptyNearbySolution implements Solution {
    private final String station;
    private final int travelTime;

    public EmptyNearbySolution(NearbyQuery nearbyQuery) {
        this.station = nearbyQuery.getFromStation();
        this.travelTime = nearbyQuery.getTravelTime();
    }

    @Override
    public void accept(Solutions.SolutionVisitor visitor) {
        visitor.visit(this);
    }

    public String getStation() {
        return station;
    }

    public int getTravelTime() {
        return travelTime;
    }
}