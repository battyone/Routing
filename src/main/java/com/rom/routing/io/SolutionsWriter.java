package com.rom.routing.io;

import com.rom.routing.core.EmptyNearbySolution;
import com.rom.routing.core.EmptyRouteSolution;
import com.rom.routing.core.NearbySolution;
import com.rom.routing.core.PathSolution;
import com.rom.routing.core.Solution;
import com.rom.routing.core.Solutions;

import java.util.Collection;

import static java.lang.String.format;

/**
 * Private implementation of {@link Solutions.SolutionVisitor} to visit each type of {@link Solution}
 * and produce output accordingly.
 *
 * @author Roman Katerinenko
 */
public class SolutionsWriter implements Solutions.SolutionVisitor {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void visit(EmptyRouteSolution solution) {
        stringBuilder.append(format("Error: No path from %s to %s\n", solution.getFromStation(), solution.getToStation()));
    }

    @Override
    public void visit(EmptyNearbySolution solution) {
        stringBuilder.append(format("Error: No near stations %s within %d sec.\n", solution.getStation(), solution.getTravelTime()));
    }

    @Override
    public void visit(PathSolution pathSolution) {
        int counter = 0;
        Collection<String> stationsPath = pathSolution.getStationsPath();
        int stationsAmount = stationsPath.size();
        for (String station : stationsPath) {
            stringBuilder.append(station);
            if (counter++ < stationsAmount - 1) {
                stringBuilder.append(" -> ");
            }
        }
        stringBuilder.append(": ");
        stringBuilder.append(pathSolution.getRouteTime());
        stringBuilder.append('\n');
    }

    @Override
    public void visit(NearbySolution nearbySolution) {
        int counter = 0;
        int stationsAmount = nearbySolution.getNearbyStations().size();
        for (NearbySolution.StationAndTime stationAndTime : nearbySolution.getNearbyStations()) {
            stringBuilder.append(stationAndTime.getStationName());
            stringBuilder.append(": ");
            stringBuilder.append(stationAndTime.getTravelTime());
            if (counter++ < stationsAmount - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append('\n');
    }

    public String getString() {
        int length = stringBuilder.length();
        if (length > 0) {
            // here we delete last '\n'
            stringBuilder.deleteCharAt(length - 1);
        }
        return stringBuilder.toString();
    }
}
