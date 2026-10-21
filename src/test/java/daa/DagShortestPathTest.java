package daa;

import daa.algo.CriticalPath;
import daa.algo.DagShortestPath;
import daa.graph.Graph;
import daa.graph.GraphGen;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DagShortestPathTest {

    static long[] bellmanFordVirtualSource(Graph graph) {
        int n = graph.n;
        long[] dist = new long[n];
        Arrays.fill(dist, DagShortestPath.INF);

        int[] inDegree = new int[n];
        for (Graph.Edge e : graph.edges) {
            inDegree[e.v]++;
        }
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int iter = 0; iter < n - 1; iter++) {
            boolean changed = false;
            for (Graph.Edge e : graph.edges) {
                if (dist[e.u] != DagShortestPath.INF && dist[e.u] + e.w < dist[e.v]) {
                    dist[e.v] = dist[e.u] + e.w;
                    changed = true;
                }
            }
            if (!changed) break;
        }

        return dist;
    }

    @Test
    public void testCompareWithBellmanFordIncludingNegativeWeights() {
        for (int test = 0; test < 20; test++) {
            int n = 40 + test * 5;
            int m = n * 3;
            Graph dag = GraphGen.generateDag(n, m, 777L + test);

            Random rnd = new Random(555L + test);
            Graph dagWithNegatives = new Graph(n, true);
            for (Graph.Edge e : dag.edges) {
                int w = e.w - (rnd.nextBoolean() ? 300 : 0);
                dagWithNegatives.addEdge(e.u, e.v, w);
            }

            DagShortestPath.ShortestPathResult result = DagShortestPath.run(dagWithNegatives, null);
            long[] bfDist = bellmanFordVirtualSource(dagWithNegatives);

            for (int i = 0; i < n; i++) {
                assertEquals(bfDist[i], result.dist[i]);
            }
        }
    }

    @Test
    public void testPathRestoration() throws IOException {
        Graph dag = Graph.fromFile("data/dag.txt");
        DagShortestPath.ShortestPathResult result = DagShortestPath.run(dag, null);

        List<Integer> path = result.getPath(5);
        assertFalse(path.isEmpty());
        assertEquals(0, path.get(0));
        assertEquals(5, path.get(path.size() - 1));

        long pathCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            int w = Integer.MAX_VALUE;
            for (Graph.Edge e : dag.adj.get(u)) {
                if (e.v == v) {
                    w = Math.min(w, e.w);
                }
            }
            pathCost += w;
        }
        assertEquals(result.dist[5], pathCost);
    }

    @Test
    public void testCondensationAndCriticalPath() throws IOException {
        Graph cyclic = Graph.fromFile("data/cyclic.txt");
        CriticalPath.CondensationResult res = CriticalPath.buildAndFindCriticalPath(cyclic);
        assertTrue(res.condensationDag.n <= cyclic.n);
        assertTrue(res.longestPathLength >= 0);

        Graph multiScc = new Graph(4, true);
        multiScc.addEdge(0, 1, 10);
        multiScc.addEdge(1, 0, 5);
        multiScc.addEdge(1, 2, 8);
        multiScc.addEdge(2, 3, 6);
        multiScc.addEdge(3, 2, 3);
        CriticalPath.CondensationResult multiRes = CriticalPath.buildAndFindCriticalPath(multiScc);
        assertEquals(2, multiRes.condensationDag.n);
        assertEquals(8, multiRes.longestPathLength);
    }
}
