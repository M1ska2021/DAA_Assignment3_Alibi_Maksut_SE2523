package daa.algo;

import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Kahn {

    public static class TopoResult {
        public final boolean hasCycle;
        public final List<Integer> order;

        public TopoResult(boolean hasCycle, List<Integer> order) {
            this.hasCycle = hasCycle;
            this.order = order;
        }
    }

    public static TopoResult run(Graph graph, Metrics metrics) {
        int n = graph.n;
        int[] inDegree = new int[n];
        for (Graph.Edge e : graph.edges) {
            inDegree[e.v]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>(n);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Graph.Edge e : graph.adj.get(u)) {
                if (metrics != null) {
                    metrics.edgeScans++;
                }
                inDegree[e.v]--;
                if (inDegree[e.v] == 0) {
                    queue.add(e.v);
                }
            }
        }

        boolean hasCycle = order.size() < n;
        return new TopoResult(hasCycle, hasCycle ? null : order);
    }

    public static boolean isValidOrder(Graph graph, List<Integer> order) {
        if (order == null || order.size() != graph.n) {
            return false;
        }
        int[] pos = new int[graph.n];
        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }
        for (Graph.Edge e : graph.edges) {
            if (pos[e.u] >= pos[e.v]) {
                return false;
            }
        }
        return true;
    }
}
