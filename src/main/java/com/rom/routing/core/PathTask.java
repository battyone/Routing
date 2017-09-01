package com.rom.routing.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Object representation of input. Contains paths ({@link Path}) and queries ({@link Query} and it's descendants).
 * It keeps them in insertion order.
 * To iterate over queries one need to pass {@link QueryVisitor} to {@link #runQueryVisitor(QueryVisitor)}
 *
 * @author Roman Katerinenko
 */
public class PathTask {
    private final Collection<Path> paths = new ArrayList<>();
    private final Collection<Query> queries = new ArrayList<>();

    public void addRoute(Path path) {
        paths.add(path);
    }

    public void addQuery(Query query) {
        queries.add(query);
    }

    public Collection<Path> getPaths() {
        return paths;
    }

    public void runQueryVisitor(QueryVisitor queryVisitor) {
        queries.forEach(q -> q.accept(queryVisitor));
    }
}