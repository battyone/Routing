package com.rom.routing.core;

/**
 * @author Roman Katerinenko
 */
public class NearbyQuery extends Query {
    private final int travelTime;

    public NearbyQuery(String fromStation, int travelTime) {
        super(fromStation);
        this.travelTime = travelTime;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

    public int getTravelTime() {
        return travelTime;
    }
}