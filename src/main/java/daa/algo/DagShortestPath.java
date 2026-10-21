package daa.algo;

import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DagShortestPath {

    public static final long INF = Long.MAX_VALUE / 4;

    public static class ShortestPathResult {
        public final long[] dist;
        public final int[] parent;
        public final long sumOfDistances;

        public ShortestPathResult(long[] dist, int[] parent, long sumOfDistances) {
            this.dist = dist;
            this.parent = parent;
            this.sumOfDistances = sumOfDistances;
        }

        public List<Integer> getPath(int target) {
            if (dist[target] >= INF) {
                return Collections.emptyList();
            }
            List<Integer> path = new ArrayList<>();
            for (int at = target; at != -1; at = parent[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static ShortestPathResult run(Graph graph, Metrics metrics) {
        return runWithOrder(graph, Kahn.run(graph, null).order, metrics);
    }

    public static ShortestPathResult runWithOrder(Graph graph, List<Integer> topoOrder, Metrics metrics) {
        int n = graph.n;
        int[] inDegree = new int[n];
        for (Graph.Edge e : graph.edges) {
            inDegree[e.v]++;
        }

        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int u : topoOrder) {
            for (Graph.Edge e : graph.adj.get(u)) {
                if (metrics != null) {
                    metrics.edgeScans++;
                }
                if (dist[u] != INF) {
                    if (metrics != null) {
                        metrics.comparisons++;
                    }
                    if (dist[u] + e.w < dist[e.v]) {
                        dist[e.v] = dist[u] + e.w;
                        parent[e.v] = u;
                    }
                }
            }
        }

        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += dist[i];
        }

        return new ShortestPathResult(dist, parent, sum);
    }
}
