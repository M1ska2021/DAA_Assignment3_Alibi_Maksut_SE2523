package daa;

import daa.algo.Bfs;
import daa.algo.Kosaraju;
import daa.algo.Tarjan;
import daa.graph.Graph;
import daa.graph.GraphGen;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SccTest {

    @Test
    public void testSccAgreesWithBfsBruteForce() {
        for (int test = 0; test < 15; test++) {
            int n = 50 + test * 10;
            int m = n * 3;
            Graph graph = GraphGen.generateDirected(n, m, 4242L + test);

            Kosaraju.SccResult kosa = Kosaraju.run(graph, null);
            Kosaraju.SccResult tarjan = Tarjan.run(graph, null);
            Kosaraju.SccResult brute = Bfs.bruteForceScc(graph);

            assertEquals(brute.count, kosa.count);
            assertEquals(brute.count, tarjan.count);

            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    boolean sameKosa = (kosa.componentId[u] == kosa.componentId[v]);
                    boolean sameTarjan = (tarjan.componentId[u] == tarjan.componentId[v]);
                    boolean sameBrute = (brute.componentId[u] == brute.componentId[v]);
                    assertEquals(sameBrute, sameKosa);
                    assertEquals(sameBrute, sameTarjan);
                }
            }
        }
    }

    @Test
    public void testCyclicFile() throws IOException {
        Graph graph = Graph.fromFile("data/cyclic.txt");
        Kosaraju.SccResult kosa = Kosaraju.run(graph, null);
        Kosaraju.SccResult tarjan = Tarjan.run(graph, null);
        assertEquals(kosa.count, tarjan.count);
    }
}
