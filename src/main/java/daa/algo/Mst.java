package daa.algo;

import daa.graph.Graph;
import java.util.List;

public class Mst {
    public final long totalWeight;
    public final List<Graph.Edge> edges;
    public final int components;

    public Mst(long totalWeight, List<Graph.Edge> edges, int components) {
        this.totalWeight = totalWeight;
        this.edges = edges;
        this.components = components;
    }

    public long getTotalWeight() {
        return totalWeight;
    }

    public List<Graph.Edge> getEdges() {
        return edges;
    }

    public int getComponents() {
        return components;
    }
}
