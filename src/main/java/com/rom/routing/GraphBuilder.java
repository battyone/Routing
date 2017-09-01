package com.rom.routing;

import com.rom.routing.core.PathTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Katerinenko
 */
class GraphBuilder {
    private final Map<String, Graph.Vertex> vertexCache = new HashMap<>();
    private final PathTask pathTask;
    private final Graph graph = new Graph();

    GraphBuilder(PathTask pathTask) {
        this.pathTask = pathTask;
    }

    Graph build() {
        pathTask.getPaths().forEach(route -> {
            Graph.Vertex fromVertex = getCachedFor(route.getFromStation());
            Graph.Vertex toVertex = getCachedFor(route.getToStation());
            graph.addEdge(fromVertex, new Graph.Edge(toVertex, route.getTravelTime()));
        });
        return graph;
    }

    private Graph.Vertex getCachedFor(String id) {
        Graph.Vertex vertex = vertexCache.get(id);
        if (vertex == null) {
            vertex = new Graph.Vertex(id);
            graph.addVertex(vertex);
            vertexCache.put(id, vertex);
        }
        return vertex;
    }
}