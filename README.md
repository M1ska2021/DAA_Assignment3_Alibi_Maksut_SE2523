# DAA Assignment 3: Graphs (DSU, MST, SCC, Shortest Paths)

**Student:** Alibi Maksut  
**Group:** SE-2523  
**Course:** Design and Analysis of Algorithms  
**Instructor:** Taubakabyl Nurlybek  

---

## Overview

This project implements core graph algorithms and data structures:
- **Disjoint Set Union (DSU):** Union by rank and path compression with operation tracking.
- **Minimum Spanning Tree (MST):** Kruskal's algorithm with DSU and Prim's algorithm with PriorityQueue, supporting spanning forests on disconnected graphs.
- **Strongly Connected Components (SCC):** Kosaraju's two-pass algorithm and Tarjan's single-pass algorithm with iterative explicit stack traversals.
- **Topological Sorting:** Kahn's in-degree queue algorithm with cycle detection.
- **DAG Shortest Paths:** Linear-time relaxation in topological order with virtual super-source, negative weight handling, and parent restoration.
- **Condensation & Critical Path:** Condensation graph construction and longest path scheduling analysis.

---

## Requirements

- Java JDK 17 or 21
- Apache Maven 3.8+

---

## How to Build

To build the project without running tests:
```bash
mvn clean package -DskipTests
```

---

## How to Run Tests

To execute the full JUnit 5 test suite:
```bash
mvn test
```

The test suite validates:
- DSU operations against a naive reference implementation after random unions.
- Agreement between Kruskal and Prim on 100 random connected graphs.
- Disconnected graph forest handling on `data/disconnected.txt`.
- Kosaraju and Tarjan SCC equivalence against brute-force BFS reachability.
- Kahn topological order validity and cycle detection on `data/cyclic.txt` and `data/dag.txt`.
- DAG shortest paths equivalence against Bellman-Ford on DAGs with negative weights.
- Stack safety on a linear path of 100,000 vertices without `StackOverflowError`.

---

## How to Run Benchmark

To execute the benchmark across all 10 graph configurations and generate `results/results.csv` and plots in `results/plots/`:
```bash
mvn compile exec:java
```
Or directly with the Java binary:
```bash
java -Xmx2g -cp target/classes daa.Main
```

---

## Project Structure

```
├── pom.xml
├── README.md
├── REPORT.md
├── data/
│   ├── disconnected.txt
│   ├── cyclic.txt
│   └── dag.txt
├── results/
│   ├── results.csv
│   └── plots/
│       ├── ops_vs_n.png
│       ├── scans_per_edge.png
│       └── kruskal_edge_share.png
├── src/
│   ├── main/java/daa/
│   │   ├── Main.java
│   │   ├── algo/
│   │   │   ├── Bfs.java
│   │   │   ├── CriticalPath.java
│   │   │   ├── DagShortestPath.java
│   │   │   ├── Kahn.java
│   │   │   ├── Kosaraju.java
│   │   │   ├── Kruskal.java
│   │   │   ├── Mst.java
│   │   │   ├── Prim.java
│   │   │   └── Tarjan.java
│   │   ├── bench/
│   │   │   ├── BenchmarkRunner.java
│   │   │   └── ChartGenerator.java
│   │   ├── ds/
│   │   │   └── Dsu.java
│   │   ├── graph/
│   │   │   ├── Graph.java
│   │   │   └── GraphGen.java
│   │   └── metrics/
│   │       └── Metrics.java
│   └── test/java/daa/
│       ├── DagShortestPathTest.java
│       ├── DsuTest.java
│       ├── MstTest.java
│       ├── SccTest.java
│       ├── StackSafetyTest.java
│       └── TopoOrderTest.java
```

---

## AI-Use Acknowledgement

AI tools (Antigravity assistant) were consulted during development for assistance with:
- Reviewing theoretical complexity bounds and mathematical cut property formulations.
- Formulating the layout of Markdown and PDF report structures.
- Debugging iterative explicit stack pointer handling.

All algorithmic logic, graph data representations, unit test suites, and empirical benchmark executions were independently verified, executed, and cross-checked against reference values.
