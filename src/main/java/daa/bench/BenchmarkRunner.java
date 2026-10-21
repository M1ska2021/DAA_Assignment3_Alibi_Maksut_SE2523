package daa.bench;

import daa.algo.DagShortestPath;
import daa.algo.Kahn;
import daa.algo.Kosaraju;
import daa.algo.Kruskal;
import daa.algo.Mst;
import daa.algo.Prim;
import daa.graph.Graph;
import daa.graph.GraphGen;
import daa.metrics.Metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BenchmarkRunner {

    public static class Row {
        public final String algorithm;
        public final String graph;
        public final int n;
        public final int m;
        public final double timeMs;
        public final long dsuOps;
        public final long edgeScans;
        public final long comparisons;
        public final int maxDepth;
        public final long result;

        public Row(String algorithm, String graph, int n, int m, double timeMs,
                   long dsuOps, long edgeScans, long comparisons, int maxDepth, long result) {
            this.algorithm = algorithm;
            this.graph = graph;
            this.n = n;
            this.m = m;
            this.timeMs = timeMs;
            this.dsuOps = dsuOps;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
            this.maxDepth = maxDepth;
            this.result = result;
        }

        public String toCsvLine() {
            return String.format(java.util.Locale.US, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d",
                    algorithm, graph, n, m, timeMs, dsuOps, edgeScans, comparisons, maxDepth, result);
        }
    }

    private static final int[][] SIZES = {
            {1000, 4000, 20000},
            {3162, 12648, 63240},
            {10000, 40000, 200000},
            {31623, 126492, 632460},
            {100000, 400000, 2000000}
    };

    private static final long[][] REF_VALUES = {
            {147970L, 27L, 396274L},
            {30363L, 1L, 252411L},
            {468911L, 137L, 1271911L},
            {97589L, 1L, 723157L},
            {1486139L, 373L, 3941828L},
            {309550L, 1L, 2381342L},
            {4715515L, 1188L, 12551891L},
            {968517L, 1L, 7376612L},
            {15012094L, 3923L, 40096539L},
            {3056681L, 1L, 23512087L}
    };

    public static List<Row> runAll() {
        List<Row> rows = new ArrayList<>();
        int refIdx = 0;

        for (int[] size : SIZES) {
            int n = size[0];
            int[] ms = {size[1], size[2]};
            String[] densities = {"sparse", "dense"};

            for (int d = 0; d < 2; d++) {
                int m = ms[d];
                String density = densities[d];

                long expectedMst = REF_VALUES[refIdx][0];
                long expectedScc = REF_VALUES[refIdx][1];
                long expectedDag = REF_VALUES[refIdx][2];
                refIdx++;

                Graph gUndir = GraphGen.generateUndirected(n, m, 42);
                Row rowKruskal = benchmarkKruskal(gUndir, "undirected_" + density, n, m);
                Row rowPrim = benchmarkPrim(gUndir, "undirected_" + density, n, m);

                if (rowKruskal.result != rowPrim.result) {
                    throw new IllegalStateException("MST mismatch: Kruskal=" + rowKruskal.result + ", Prim=" + rowPrim.result);
                }
                if (rowKruskal.result != expectedMst) {
                    throw new IllegalStateException("Kruskal MST does not match ref: got " + rowKruskal.result + ", expected " + expectedMst);
                }
                rows.add(rowKruskal);
                rows.add(rowPrim);

                Graph gDir = GraphGen.generateDirected(n, m, 42);
                Row rowKosaraju = benchmarkKosaraju(gDir, "directed_" + density, n, m);
                if (rowKosaraju.result != expectedScc) {
                    throw new IllegalStateException("Kosaraju SCC count does not match ref: got " + rowKosaraju.result + ", expected " + expectedScc);
                }
                if (rowKosaraju.edgeScans != 2L * m) {
                    throw new IllegalStateException("Kosaraju edgeScans mismatch: got " + rowKosaraju.edgeScans + ", expected " + (2L * m));
                }
                rows.add(rowKosaraju);

                Graph gDag = GraphGen.generateDag(n, m, 42);
                Row rowKahn = benchmarkKahn(gDag, "dag_" + density, n, m);
                if (rowKahn.result != n) {
                    throw new IllegalStateException("Kahn order size mismatch: got " + rowKahn.result + ", expected " + n);
                }
                if (rowKahn.edgeScans != m) {
                    throw new IllegalStateException("Kahn edgeScans mismatch: got " + rowKahn.edgeScans + ", expected " + m);
                }
                rows.add(rowKahn);

                Row rowDagSp = benchmarkDagShortestPath(gDag, "dag_" + density, n, m);
                if (rowDagSp.result != expectedDag) {
                    throw new IllegalStateException("DAG distance sum does not match ref: got " + rowDagSp.result + ", expected " + expectedDag);
                }
                if (rowDagSp.edgeScans != m) {
                    throw new IllegalStateException("DAG SP edgeScans mismatch: got " + rowDagSp.edgeScans + ", expected " + m);
                }
                rows.add(rowDagSp);

                System.out.printf("Checked n=%d m=%d (%s): MST=%d, SCC=%d, DAG_DIST=%d (OK)%n",
                        n, m, density, expectedMst, expectedScc, expectedDag);
            }
        }

        return rows;
    }

    private static double median(double[] values) {
        Arrays.sort(values);
        return values[values.length / 2];
    }

    private static Row benchmarkKruskal(Graph g, String graphName, int n, int m) {
        Metrics mObj = new Metrics();
        Mst res = Kruskal.run(g, mObj);
        double[] times = new double[5];
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            Kruskal.run(g, null);
            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000.0;
        }
        return new Row("Kruskal", graphName, n, m, median(times),
                mObj.dsuOps, mObj.edgeScans, mObj.comparisons, mObj.maxDepth, res.totalWeight);
    }

    private static Row benchmarkPrim(Graph g, String graphName, int n, int m) {
        Metrics mObj = new Metrics();
        Mst res = Prim.run(g, mObj);
        double[] times = new double[5];
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            Prim.run(g, null);
            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000.0;
        }
        return new Row("Prim", graphName, n, m, median(times),
                mObj.dsuOps, mObj.edgeScans, mObj.comparisons, mObj.maxDepth, res.totalWeight);
    }

    private static Row benchmarkKosaraju(Graph g, String graphName, int n, int m) {
        Metrics mObj = new Metrics();
        Kosaraju.SccResult res = Kosaraju.run(g, mObj);
        double[] times = new double[5];
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            Kosaraju.run(g, null);
            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000.0;
        }
        return new Row("Kosaraju", graphName, n, m, median(times),
                mObj.dsuOps, mObj.edgeScans, mObj.comparisons, mObj.maxDepth, res.count);
    }

    private static Row benchmarkKahn(Graph g, String graphName, int n, int m) {
        Metrics mObj = new Metrics();
        Kahn.TopoResult res = Kahn.run(g, mObj);
        if (res.hasCycle || !Kahn.isValidOrder(g, res.order)) {
            throw new IllegalStateException("Kahn produced invalid topological order");
        }
        double[] times = new double[5];
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            Kahn.run(g, null);
            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000.0;
        }
        return new Row("Kahn", graphName, n, m, median(times),
                mObj.dsuOps, mObj.edgeScans, mObj.comparisons, mObj.maxDepth, res.order.size());
    }

    private static Row benchmarkDagShortestPath(Graph g, String graphName, int n, int m) {
        Metrics mObj = new Metrics();
        DagShortestPath.ShortestPathResult res = DagShortestPath.run(g, mObj);
        double[] times = new double[5];
        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();
            DagShortestPath.run(g, null);
            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000.0;
        }
        return new Row("DagShortestPath", graphName, n, m, median(times),
                mObj.dsuOps, mObj.edgeScans, mObj.comparisons, mObj.maxDepth, res.sumOfDistances);
    }

    public static void saveCsv(List<Row> rows, String path) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            for (Row r : rows) {
                writer.println(r.toCsvLine());
            }
        }
    }
}
