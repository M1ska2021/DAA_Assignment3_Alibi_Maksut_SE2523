package daa;

import daa.algo.Kosaraju;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackSafetyTest {

    @Test
    public void testSccSurvivesHundredThousandVertices() {
        int n = 100_000;
        Graph path = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            path.addEdge(i, i + 1, 1);
        }

        Metrics metrics = new Metrics();
        Kosaraju.SccResult result = Kosaraju.run(path, metrics);

        assertEquals(n, result.count);
        assertEquals(n, metrics.maxDepth);
    }
}
