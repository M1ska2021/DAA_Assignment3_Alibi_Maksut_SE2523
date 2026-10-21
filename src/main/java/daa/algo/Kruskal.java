package daa.algo;

import daa.ds.Dsu;
import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static Mst run(Graph graph, Metrics metrics) {
        List<Graph.Edge> sortedEdges = new ArrayList<>(graph.edges);
        sortedEdges.sort((e1, e2) -> {
            if (metrics != null) {
                metrics.comparisons++;
            }
            return Integer.compare(e1.w, e2.w);
        });

        Dsu dsu = new Dsu(graph.n);
        List<Graph.Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;

        for (Graph.Edge e : sortedEdges) {
            if (metrics != null) {
                metrics.edgeScans++;
            }
            if (dsu.find(e.u) != dsu.find(e.v)) {
                dsu.union(e.u, e.v);
                totalWeight += e.w;
                mstEdges.add(e);
                if (mstEdges.size() == graph.n - 1) {
                    break;
                }
            }
        }

        if (metrics != null) {
            metrics.dsuOps += dsu.getOps();
        }

        return new Mst(totalWeight, mstEdges, dsu.components());
    }
}
