package daa.algo;

import daa.graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class Bfs {

    public static boolean[] reachableFrom(Graph graph, int start) {
        boolean[] visited = new boolean[graph.n];
        Queue<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Graph.Edge e : graph.adj.get(u)) {
                if (!visited[e.v]) {
                    visited[e.v] = true;
                    queue.add(e.v);
                }
            }
        }
        return visited;
    }

    public static Kosaraju.SccResult bruteForceScc(Graph graph) {
        int n = graph.n;
        boolean[][] reach = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            reach[i] = reachableFrom(graph, i);
        }

        int[] compId = new int[n];
        Arrays.fill(compId, -1);
        List<List<Integer>> compList = new ArrayList<>();
        int sccCount = 0;

        for (int i = 0; i < n; i++) {
            if (compId[i] != -1) {
                continue;
            }
            int current = sccCount++;
            compId[i] = current;
            List<Integer> comp = new ArrayList<>();
            comp.add(i);

            for (int j = i + 1; j < n; j++) {
                if (compId[j] == -1 && reach[i][j] && reach[j][i]) {
                    compId[j] = current;
                    comp.add(j);
                }
            }
            compList.add(comp);
        }

        return new Kosaraju.SccResult(sccCount, compId, compList);
    }
}
