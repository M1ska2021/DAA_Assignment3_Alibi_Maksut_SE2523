package daa.algo;

import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kosaraju {

    public static class SccResult {
        public final int count;
        public final int[] componentId;
        public final List<List<Integer>> components;

        public SccResult(int count, int[] componentId, List<List<Integer>> components) {
            this.count = count;
            this.componentId = componentId;
            this.components = components;
        }
    }

    public static SccResult run(Graph graph, Metrics metrics) {
        int n = graph.n;
        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderIdx = 0;

        int[] stack = new int[n + 5];
        int[] nextEdge = new int[n];

        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                continue;
            }

            int top = 0;
            stack[top] = i;
            visited[i] = true;
            nextEdge[i] = 0;
            if (metrics != null) {
                metrics.updateDepth(1);
            }

            while (top >= 0) {
                int u = stack[top];
                List<Graph.Edge> edges = graph.adj.get(u);
                if (nextEdge[u] < edges.size()) {
                    Graph.Edge e = edges.get(nextEdge[u]++);
                    if (metrics != null) {
                        metrics.edgeScans++;
                    }
                    int v = e.v;
                    if (!visited[v]) {
                        visited[v] = true;
                        nextEdge[v] = 0;
                        top++;
                        stack[top] = v;
                        if (metrics != null) {
                            metrics.updateDepth(top + 1);
                        }
                    }
                } else {
                    order[orderIdx++] = u;
                    top--;
                }
            }
        }

        Graph reversed = graph.transpose();
        boolean[] visited2 = new boolean[n];
        int[] compId = new int[n];
        Arrays.fill(compId, -1);
        List<List<Integer>> compList = new ArrayList<>();
        int sccCount = 0;

        int[] revNext = new int[n];

        for (int i = n - 1; i >= 0; i--) {
            int root = order[i];
            if (visited2[root]) {
                continue;
            }

            List<Integer> comp = new ArrayList<>();
            int currentComponent = sccCount;
            sccCount++;

            int top = 0;
            stack[top] = root;
            visited2[root] = true;
            compId[root] = currentComponent;
            comp.add(root);
            revNext[root] = 0;
            if (metrics != null) {
                metrics.updateDepth(1);
            }

            while (top >= 0) {
                int u = stack[top];
                List<Graph.Edge> edges = reversed.adj.get(u);
                if (revNext[u] < edges.size()) {
                    Graph.Edge e = edges.get(revNext[u]++);
                    if (metrics != null) {
                        metrics.edgeScans++;
                    }
                    int v = e.v;
                    if (!visited2[v]) {
                        visited2[v] = true;
                        compId[v] = currentComponent;
                        comp.add(v);
                        revNext[v] = 0;
                        top++;
                        stack[top] = v;
                        if (metrics != null) {
                            metrics.updateDepth(top + 1);
                        }
                    }
                } else {
                    top--;
                }
            }

            compList.add(comp);
        }

        return new SccResult(sccCount, compId, compList);
    }
}
