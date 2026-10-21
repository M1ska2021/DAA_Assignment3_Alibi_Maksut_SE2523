package daa;

import daa.algo.Kahn;
import daa.graph.Graph;
import daa.graph.GraphGen;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TopoOrderTest {

    @Test
    public void testValidOrderOnDags() {
        for (int i = 0; i < 20; i++) {
            int n = 50 + i * 10;
            int m = n * 4;
            Graph dag = GraphGen.generateDag(n, m, 9999L + i);
            Kahn.TopoResult result = Kahn.run(dag, null);

            assertFalse(result.hasCycle);
            assertNotNull(result.order);
            assertTrue(Kahn.isValidOrder(dag, result.order));
        }
    }

    @Test
    public void testCycleDetection() throws IOException {
        Graph cyclic = Graph.fromFile("data/cyclic.txt");
        Kahn.TopoResult result = Kahn.run(cyclic, null);
        assertTrue(result.hasCycle);
        assertNull(result.order);

        Graph dag = Graph.fromFile("data/dag.txt");
        Kahn.TopoResult dagResult = Kahn.run(dag, null);
        assertFalse(dagResult.hasCycle);
        assertNotNull(dagResult.order);
        assertTrue(Kahn.isValidOrder(dag, dagResult.order));
    }
}
