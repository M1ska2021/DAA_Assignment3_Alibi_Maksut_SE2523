package daa.algo;

import daa.graph.Graph;
import daa.metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tarjan {

    public static Kosaraju.SccResult run(Graph graph, Metrics metrics) {
        int n = graph.n;
        int[] index = new int[n];
        int[] lowlink = new int[n];
        boolean[] onStack = new boolean[n];
        Arrays.fill(index, -1);
        Arrays.fill(lowlink, -1);

        int[] callStack = new int[n + 5];
        int[] nextEdge = new int[n];
        int[] sccStack = new int[n + 5];
        int sccTop = -1;

        int[] compId = new int[n];
        Arrays.fill(compId, -1);
        List<List<Integer>> compList = new ArrayList<>();
        int time = 0;
        int sccCount = 0;

        for (int i = 0; i < n; i++) {
            if (index[i] != -1) {
                continue;
            }

            int top = 0;
            callStack[top] = i;
            index[i] = time;
            lowlink[i] = time;
            time++;
            sccStack[++sccTop] = i;
            onStack[i] = true;
            nextEdge[i] = 0;

            if (metrics != null) {
                metrics.updateDepth(top + 1);
            }

            while (top >= 0) {
                int u = callStack[top];
                List<Graph.Edge> edges = graph.adj.get(u);
                if (nextEdge[u] < edges.size()) {
                    Graph.Edge e = edges.get(nextEdge[u]++);
                    if (metrics != null) {
                        metrics.edgeScans++;
                    }
                    int v = e.v;
                    if (index[v] == -1) {
                        index[v] = time;
                        lowlink[v] = time;
                        time++;
                        sccStack[++sccTop] = v;
                        onStack[v] = true;
                        nextEdge[v] = 0;
                        top++;
                        callStack[top] = v;
                        if (metrics != null) {
                            metrics.updateDepth(top + 1);
                        }
                    } else if (onStack[v]) {
                        lowlink[u] = Math.min(lowlink[u], index[v]);
                    }
                } else {
                    if (top > 0) {
                        int parent = callStack[top - 1];
                        lowlink[parent] = Math.min(lowlink[parent], lowlink[u]);
                    }

                    if (lowlink[u] == index[u]) {
                        List<Integer> comp = new ArrayList<>();
                        int currentComp = sccCount++;
                        while (true) {
                            int v = sccStack[sccTop--];
                            onStack[v] = false;
                            compId[v] = currentComp;
                            comp.add(v);
                            if (v == u) break;
                        }
                        compList.add(comp);
                    }
                    top--;
                }
            }
        }

        return new Kosaraju.SccResult(sccCount, compId, compList);
    }
}
