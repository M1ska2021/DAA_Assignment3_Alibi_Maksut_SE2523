package daa.algo;

import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {

    public static Mst run(Graph graph, Metrics metrics) {
        boolean[] inTree = new boolean[graph.n];
        List<Graph.Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        int components = 0;

        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>((e1, e2) -> {
            if (metrics != null) {
                metrics.comparisons++;
            }
            return Integer.compare(e1.w, e2.w);
        });

        for (int i = 0; i < graph.n; i++) {
            if (inTree[i]) {
                continue;
            }

            components++;
            inTree[i] = true;

            for (Graph.Edge e : graph.adj.get(i)) {
                if (metrics != null) {
                    metrics.edgeScans++;
                }
                pq.add(e);
            }

            while (!pq.isEmpty()) {
                Graph.Edge edge = pq.poll();
                int nextVertex = edge.v;

                if (inTree[nextVertex]) {
                    continue;
                }

                inTree[nextVertex] = true;
                totalWeight += edge.w;
                mstEdges.add(edge);

                for (Graph.Edge nextEdge : graph.adj.get(nextVertex)) {
                    if (metrics != null) {
                        metrics.edgeScans++;
                    }
                    if (!inTree[nextEdge.v]) {
                        pq.add(nextEdge);
                    }
                }
            }
        }

        return new Mst(totalWeight, mstEdges, components);
    }
}
