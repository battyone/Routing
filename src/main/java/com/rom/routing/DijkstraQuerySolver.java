package com.rom.routing;

import com.rom.routing.core.EmptyNearbySolution;
import com.rom.routing.core.EmptyRouteSolution;
import com.rom.routing.core.NearbyQuery;
import com.rom.routing.core.NearbySolution;
import com.rom.routing.core.QueryVisitor;
import com.rom.routing.core.PathQuery;
import com.rom.routing.core.PathSolution;
import com.rom.routing.core.Solution;
import com.rom.routing.core.Solutions;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implementation of Dijkstra single-source shortest-path algorithm.
 * It uses {@link PriorityQueue} as MIN-HEAP implementation.
 * <p>
 * Note! It keeps a metadata for a vertex separately from a vertex.
 * Since the algorithm modifies the metadata while solving a query, separate metadata allows to keep graph unmodified
 * from query to query and avoid the cost of graph copying.
 *
 * @author Roman Katerinenko
 */
class DijkstraQuerySolver implements QueryVisitor {
    private static final int SAME_VERTEX_PATH_COST = 0;
    private static final int MIN_PATH_COST = 0;
    private static final int MAX_PATH_COST = Integer.MAX_VALUE;

    private final Solutions solutions = new Solutions();
    private final Graph graph;

    private Map<Graph.Vertex, VertexMetadata> vertexMetadata;
    private PriorityQueue<VertexMetadata> priorityQueue;

    DijkstraQuerySolver(Graph graph) {
        this.graph = graph;
    }

    public void visit(PathQuery pathQuery) {
        Graph.Vertex fromVertex = graph.findVertexByStringId(pathQuery.getFromStation());
        Graph.Vertex toVertex = graph.findVertexByStringId(pathQuery.getToStation());
        if (fromVertex == null || toVertex == null) {
            solutions.addSolution(new EmptyRouteSolution(pathQuery));
        } else {
            if (fromVertex.equals(toVertex)) {
                solutions.addSolution(new PathSolution(SAME_VERTEX_PATH_COST, fromVertex.id, fromVertex.id));
            } else {
                runDijkstraForStartVertex(fromVertex);
                Solution solution = findPath(toVertex);
                if (solution != null) {
                    solutions.addSolution(solution);
                } else {
                    solutions.addSolution(new EmptyRouteSolution(pathQuery));
                }
            }
        }
    }

    private Solution findPath(Graph.Vertex toVertex) {
        Graph.Vertex v = toVertex;
        LinkedList<String> path = new LinkedList<>();
        do {
            path.addFirst(v.id);
        } while ((v = getMetadataFor(v).getPredecessor()) != null);
        if (path.size() > 1) {
            int routeTime = getMetadataFor(toVertex).estimatedTime;
            return new PathSolution(routeTime, path);
        } else {
            return null;
        }
    }

    public void visit(NearbyQuery nearbyQuery) {
        Graph.Vertex startVertex = graph.findVertexByStringId(nearbyQuery.getFromStation());
        if (startVertex == null) {
            solutions.addSolution(new EmptyNearbySolution(nearbyQuery));
        } else {
            runDijkstraForStartVertex(startVertex);
            Solution nearbySolution = findNearbyStationsFor(nearbyQuery);
            if (nearbySolution != null) {
                solutions.addSolution(nearbySolution);
            } else {
                solutions.addSolution(new EmptyNearbySolution(nearbyQuery));
            }
        }
    }

    private Solution findNearbyStationsFor(NearbyQuery nearbyQuery) {
        Graph.Vertex startVertex = graph.findVertexByStringId(nearbyQuery.getFromStation());
        NearbySolution nearbySolution = new NearbySolution();
        boolean anyNearbyStations = false;
        for (Graph.Vertex vertex : graph.getVertices()) {
            int travelTime = getMetadataFor(vertex).getEstimatedTime();
            if (!vertex.equals(startVertex) && travelTime <= nearbyQuery.getTravelTime()) {
                anyNearbyStations = true;
                nearbySolution.add(travelTime, vertex.id);
            }
        }
        return anyNearbyStations ? nearbySolution : null;
    }

    private void runDijkstraForStartVertex(Graph.Vertex startVertex) {
        initMetadataWith(startVertex);
        initPriorityQueue();
        while (!priorityQueue.isEmpty()) {
            VertexMetadata vertexMetadata = priorityQueue.poll();
            Graph.Vertex vertex = vertexMetadata.getVertex();
            graph.getEdgesAdjacentTo(vertex).forEach(edge -> {
                Graph.Vertex adjacentVertex = edge.destinationVertex;
                /* Since we use {@link #MAX_PATH_COST} we need to avoid overflow */
                long newEstimation = (long) edge.weight + (long) vertexMetadata.getEstimatedTime();
                VertexMetadata adjacentMetadata = getMetadataFor(adjacentVertex);
                if (adjacentMetadata.getEstimatedTime() > newEstimation) {
                    priorityQueue.remove(adjacentMetadata);
                    newEstimation = newEstimation > MAX_PATH_COST ? MAX_PATH_COST : newEstimation;
                    adjacentMetadata.setEstimatedTime((int) newEstimation);
                    adjacentMetadata.setPredecessor(vertex);
                    priorityQueue.add(adjacentMetadata);
                }
            });
        }
    }

    private void initMetadataWith(Graph.Vertex startVertex) {
        Collection<Graph.Vertex> vertices = graph.getVertices();
        vertexMetadata = new HashMap<>(vertices.size());
        vertices.forEach(v -> vertexMetadata.put(v, new VertexMetadata(v)));
        vertexMetadata.get(startVertex).setEstimatedTime(MIN_PATH_COST); // assign the smallest possible value to make it staring vertex
    }

    private void initPriorityQueue() {
        priorityQueue = new PriorityQueue<>((vm1, vm2) -> vm1.getEstimatedTime() - vm2.getEstimatedTime());
        priorityQueue.addAll(vertexMetadata.values());
    }

    private VertexMetadata getMetadataFor(Graph.Vertex vertex) {
        return vertexMetadata.get(vertex);
    }


    Solutions getSolutions() {
        return solutions;
    }

    /**
     * Two metadatas are equal when corresponding {@link #vertex} are equal (see {@link Graph.Vertex#equals(Object)}).
     */
    private static class VertexMetadata {
        private final Graph.Vertex vertex;

        private int estimatedTime;
        private Graph.Vertex predecessor;

        private VertexMetadata(Graph.Vertex vertex) {
            this.vertex = vertex;
            estimatedTime = MAX_PATH_COST;
            predecessor = null;
        }

        private int getEstimatedTime() {
            return estimatedTime;
        }

        private void setEstimatedTime(int estimatedTime) {
            this.estimatedTime = estimatedTime;
        }

        private Graph.Vertex getPredecessor() {
            return predecessor;
        }

        private void setPredecessor(Graph.Vertex predecessor) {
            this.predecessor = predecessor;
        }

        private Graph.Vertex getVertex() {
            return vertex;
        }

        @Override
        public final int hashCode() {
            return vertex.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            VertexMetadata metadata = (VertexMetadata) obj;
            return metadata.getVertex().equals(vertex);
        }
    }
}