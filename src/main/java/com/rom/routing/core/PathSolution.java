package com.rom.routing.core;

import java.util.Arrays;
import java.util.Collection;

/**
 * Corresponds to successful {@link PathQuery}
 *
 * @author Roman Katerinenko
 */
public class PathSolution implements Solution {
    private final Collection<String> stationsPath;
    private final int routeTime;

    public PathSolution(int routeTime, String... stations) {
        this.stationsPath = Arrays.asList(stations);
        this.routeTime = routeTime;
    }

    public PathSolution(int routeTime, Collection<String> stationsPath) {
        this.stationsPath = stationsPath;
        this.routeTime = routeTime;
    }

    @Override
    public void accept(Solutions.SolutionVisitor visitor) {
        visitor.visit(this);
    }

    public Collection<String> getStationsPath() {
        return stationsPath;
    }

    public int getRouteTime() {
        return routeTime;
    }
}