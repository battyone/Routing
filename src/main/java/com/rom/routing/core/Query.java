package com.rom.routing.core;

/**
 * @author Roman Katerinenko
 */
public abstract class Query {
    private final String fromStation;

    public Query(String fromStation) {
        this.fromStation = fromStation;
    }

    public abstract void accept(QueryVisitor visitor);

    public String getFromStation() {
        return fromStation;
    }
}