package com.rom.routing.core;

/**
 * @author Roman Katerinenko
 */
public interface QueryVisitor {
    void visit(PathQuery pathQuery);

    void visit(NearbyQuery routeQuery);
}