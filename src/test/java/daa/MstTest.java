package daa;

import daa.algo.Kruskal;
import daa.algo.Mst;
import daa.algo.Prim;
import daa.graph.Graph;
import daa.graph.GraphGen;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MstTest {

    @Test
    public void testHundredRandomGraphsAgreement() {
        for (int test = 0; test < 100; test++) {
            int n = 30 + test;
            int m = n * 4;
            Graph graph = GraphGen.generateUndirected(n, m, 1000L + test);

            Mst kruskal = Kruskal.run(graph, null);
            Mst prim = Prim.run(graph, null);

            assertEquals(kruskal.totalWeight, prim.totalWeight);
            assertEquals(1, kruskal.components);
            assertEquals(1, prim.components);
            assertEquals(n - 1, kruskal.edges.size());
            assertEquals(n - 1, prim.edges.size());
        }
    }

    @Test
    public void testDisconnectedFile() throws IOException {
        Graph graph = Graph.fromFile("data/disconnected.txt");
        Mst kruskal = Kruskal.run(graph, null);
        Mst prim = Prim.run(graph, null);

        assertEquals(kruskal.totalWeight, prim.totalWeight);
        assertEquals(kruskal.components, prim.components);
        assertEquals(3, kruskal.components);
    }
}
