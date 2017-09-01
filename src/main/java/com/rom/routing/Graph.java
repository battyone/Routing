package com.rom.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Package-local adjacency-list implementation of a graph to efficiently represent sparse graphs.
 * Insertion and {@link #getEdgesAdjacentTo(Vertex)} can be done in O(1) as it 'guarantees' by {@link HashMap}.
 * <p>
 * {@link #findVertexByStringId(String)} requires linear search over all vertices.
 *
 * @author Roman Katerinenko
 * @see Vertex
 * @see Edge
 */
class Graph {
    private final Map<Vertex, ArrayList<Edge>> vertexToEdges = new HashMap<>();

    void addEdge(Vertex fromVertex, Edge edge) {
        addVertex(fromVertex).add(edge);
    }

    Collection<Edge> addVertex(Vertex vertex) {
        ArrayList<Edge> edges = vertexToEdges.get(vertex);
        if (edges == null) {
            edges = new ArrayList<>();
            vertexToEdges.put(vertex, edges);
        }
        return edges;
    }

    Collection<Vertex> getVertices() {
        return vertexToEdges.keySet();
    }

    Collection<Edge> getEdgesAdjacentTo(Vertex target) {
        return vertexToEdges.get(target);
    }

    Vertex findVertexByStringId(String targetId) {
        for (Vertex vertex : getVertices()) {
            if (vertex.id.equals(targetId)) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * Note! Vertices are if and only if they have the same string id.
     */
    static class Vertex {
        final String id;

        Vertex(String id) {
            this.id = id;
        }

        @Override
        public final int hashCode() {
            return id.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Vertex thatVertex = (Vertex) obj;
            return thatVertex.id.equals(id);
        }

        @Override
        public String toString() {
            return id;
        }
    }

    /**
     * Note! Simplified directed edge.
     * E.g., for weightened edge 'A -10-> B' (10 is weight), we keep only 'B' and 10.
     */
    static class Edge {
        final Vertex destinationVertex;
        final int weight;

        Edge(Vertex destinationVertex, int weight) {
            this.destinationVertex = destinationVertex;
            this.weight = weight;
        }
    }
}