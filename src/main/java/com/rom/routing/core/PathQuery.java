package com.rom.routing.core;

/**
 * @author Roman Katerinenko
 */
public class PathQuery extends Query {
    private final String toStation;

    public PathQuery(String fromStation, String toStation) {
        super(fromStation);
        this.toStation = toStation;
    }

    @Override
    public void accept(QueryVisitor visitor) {
        visitor.visit(this);
    }

    public String getToStation() {
        return toStation;
    }
}