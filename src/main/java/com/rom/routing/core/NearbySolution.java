package com.rom.routing.core;

import java.util.Set;
import java.util.TreeSet;

/**
 * Corresponds to successful {@link NearbyQuery}
 *
 * @author Roman Katerinenko
 */
public class NearbySolution implements Solution {
    // sorted by travel time asc then by station name asc
    private final Set<StationAndTime> nearbyStations = new TreeSet<>((s1, s2) -> {
        if (s1.travelTime == s2.travelTime) {
            return s1.stationName.compareTo(s2.stationName);
        } else {
            return s1.travelTime - s2.travelTime;
        }
    });

    public void add(int travelTime, String stationName) {
        nearbyStations.add(new StationAndTime(stationName, travelTime));
    }

    @Override
    public void accept(Solutions.SolutionVisitor visitor) {
        visitor.visit(this);
    }

    public Set<StationAndTime> getNearbyStations() {
        return nearbyStations;
    }

    public static class StationAndTime {
        private final String stationName;
        private final int travelTime;

        private StationAndTime(String stationName, int travelTime) {
            this.stationName = stationName;
            this.travelTime = travelTime;
        }

        public String getStationName() {
            return stationName;
        }

        public int getTravelTime() {
            return travelTime;
        }
    }
}