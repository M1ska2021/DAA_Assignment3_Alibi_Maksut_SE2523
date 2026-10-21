package daa.algo;

import daa.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CriticalPath {

    public static class CondensationResult {
        public final Graph condensationDag;
        public final Kosaraju.SccResult scc;
        public final long longestPathLength;
        public final List<Integer> longestPathNodes;

        public CondensationResult(Graph condensationDag, Kosaraju.SccResult scc,
                                  long longestPathLength, List<Integer> longestPathNodes) {
            this.condensationDag = condensationDag;
            this.scc = scc;
            this.longestPathLength = longestPathLength;
            this.longestPathNodes = longestPathNodes;
        }
    }

    public static CondensationResult buildAndFindCriticalPath(Graph original) {
        Kosaraju.SccResult scc = Kosaraju.run(original, null);
        int numScc = scc.count;
        Graph condDag = new Graph(numScc, true);

        Set<Long> addedEdges = new HashSet<>();
        for (Graph.Edge e : original.edges) {
            int compU = scc.componentId[e.u];
            int compV = scc.componentId[e.v];
            if (compU != compV) {
                long edgeKey = (((long) compU) << 32) | (compV & 0xFFFFFFFFL);
                if (!addedEdges.contains(edgeKey)) {
                    addedEdges.add(edgeKey);
                    condDag.addEdge(compU, compV, e.w);
                }
            }
        }

        Kahn.TopoResult topo = Kahn.run(condDag, null);
        long[] dist = new long[numScc];
        int[] parent = new int[numScc];
        Arrays.fill(dist, 0);
        Arrays.fill(parent, -1);

        for (int u : topo.order) {
            for (Graph.Edge e : condDag.adj.get(u)) {
                if (dist[u] + e.w > dist[e.v]) {
                    dist[e.v] = dist[u] + e.w;
                    parent[e.v] = u;
                }
            }
        }

        int maxEnd = 0;
        long maxLen = 0;
        for (int i = 0; i < numScc; i++) {
            if (dist[i] > maxLen) {
                maxLen = dist[i];
                maxEnd = i;
            }
        }

        List<Integer> path = new ArrayList<>();
        if (maxLen > 0) {
            for (int at = maxEnd; at != -1; at = parent[at]) {
                path.add(at);
            }
            Collections.reverse(path);
        }

        return new CondensationResult(condDag, scc, maxLen, path);
    }
}
