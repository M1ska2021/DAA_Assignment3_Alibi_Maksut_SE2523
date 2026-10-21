package daa.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {

    public static class Edge {
        public final int u;
        public final int v;
        public final int w;

        public Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }

        @Override
        public String toString() {
            return "(" + u + " -> " + v + ", w=" + w + ")";
        }
    }

    public final int n;
    public final boolean directed;
    public final List<List<Edge>> adj;
    public final List<Edge> edges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            this.adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n) {
            throw new IndexOutOfBoundsException("Vertex out of bounds: " + u + " or " + v);
        }
        Edge edge = new Edge(u, v, w);
        adj.get(u).add(edge);
        edges.add(edge);
        if (!directed && u != v) {
            adj.get(v).add(new Edge(v, u, w));
        }
    }

    public Graph transpose() {
        Graph reversed = new Graph(n, true);
        for (Edge e : edges) {
            reversed.addEdge(e.v, e.u, e.w);
        }
        return reversed;
    }

    public static Graph fromFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while (line != null && line.trim().isEmpty()) {
                line = reader.readLine();
            }
            if (line == null) {
                throw new IllegalArgumentException("Empty graph file");
            }

            String[] header = line.trim().split("\\s+");
            int n = Integer.parseInt(header[0]);
            int m = Integer.parseInt(header[1]);
            boolean directed = header[2].equalsIgnoreCase("directed");

            Graph g = new Graph(n, directed);
            int readEdges = 0;
            while (readEdges < m && (line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                int w = Integer.parseInt(parts[2]);
                g.addEdge(u, v, w);
                readEdges++;
            }
            return g;
        }
    }
}
