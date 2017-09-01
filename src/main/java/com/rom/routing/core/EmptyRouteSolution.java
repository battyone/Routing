package com.rom.routing.core;

/**
 * Corresponds to unsuccessful {@link PathQuery}
 *
 * @author Roman Katerinenko
 */
public class EmptyRouteSolution implements Solution {
    private final String fromStation;
    private final String toStation;

    public EmptyRouteSolution(PathQuery pathQuery) {
        this.fromStation = pathQuery.getFromStation();
        this.toStation = pathQuery.getToStation();
    }

    @Override
    public void accept(Solutions.SolutionVisitor visitor) {
        visitor.visit(this);
    }

    public String getFromStation() {
        return fromStation;
    }

    public String getToStation() {
        return toStation;
    }
}