# Assignment 3 Report: Graphs (DSU, MST, SCC, Shortest Paths)

**Student:** Alibi Maksut  
**Group:** SE-2523  
**Course:** Design and Analysis of Algorithms  
**Instructor:** Taubakabyl Nurlybek  
**Date:** October 2026  

---

## 1. Complexity Table

| Algorithm / Data Structure | Time Complexity | Extra Space | Justification |
| :--- | :--- | :--- | :--- |
| **DSU (find / union)** | $O(\alpha(n))$ amortized | $O(n)$ | Path compression flattens trees to near depth 1, and union by rank prevents tree height from exceeding $\log n$; combined amortized cost is proportional to the inverse Ackermann function $\alpha(n) \le 4$. |
| **Kruskal (MST)** | $O(m \log m)$ | $O(n + m)$ | Sorting all $m$ edges dominates time ($m \log m$); iterating through edges with DSU operations costs $O(m \alpha(n))$, bounded by $O(n + m)$ auxiliary space. |
| **Prim (MST)** | $O(m \log n)$ | $O(n + m)$ | Each vertex is extracted once and each edge is inserted into a min-priority queue at most once, costing $O(m \log n)$ time with $O(n + m)$ queue and tree storage. |
| **Kosaraju (SCC)** | $O(n + m)$ | $O(n + m)$ | Executes two complete iterative DFS traversals: forward pass on graph $G$ and backward pass on transposed graph $G^T$, each taking linear time and explicit stack space. |
| **Kahn (Topological Sort)** | $O(n + m)$ | $O(n + m)$ | Computes in-degrees in $O(n + m)$ time, pushes zero in-degree vertices into a FIFO queue, and decrements outgoing edges once per directed edge. |
| **DAG Shortest Paths** | $O(n + m)$ | $O(n + m)$ | Relaxes outgoing edges in topological order in a single forward pass with zero back-tracking, maintaining parent and distance arrays. |
| **Tarjan (SCC, Bonus)** | $O(n + m)$ | $O(n)$ | Computes SCCs in a single DFS pass using `index`, `lowlink`, and active stack without building the transposed graph, requiring only $O(n)$ working memory. |

---

## 2. Counters vs. Formulas

Operation counters inside the algorithms provide machine-independent empirical verification of theoretical time complexities. Dividing measured operation counts by their theoretical formulas reveals a constant ratio across all orders of magnitude ($n = 1\,000$ to $n = 100\,000$).

### 2.1 Empirical Verification Table

| Algorithm | Graph Type | $n$ | $m$ | Measured Counter | Theoretical Formula | Ratio (Measured / Formula) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Kosaraju** | Directed Sparse | 1 000 | 4 000 | 8 000 edge scans | $2m = 8\,000$ | **1.0000** |
| **Kosaraju** | Directed Dense | 1 000 | 20 000 | 40 000 edge scans | $2m = 40\,000$ | **1.0000** |
| **Kosaraju** | Directed Sparse | 10 000 | 40 000 | 80 000 edge scans | $2m = 80\,000$ | **1.0000** |
| **Kosaraju** | Directed Dense | 10 000 | 200 000 | 400 000 edge scans | $2m = 400\,000$ | **1.0000** |
| **Kosaraju** | Directed Sparse | 100 000 | 400 000 | 800 000 edge scans | $2m = 800\,000$ | **1.0000** |
| **Kosaraju** | Directed Dense | 100 000 | 2 000 000 | 4 000 000 edge scans | $2m = 4\,000\,000$ | **1.0000** |
| **Kahn** | DAG Sparse | 1 000 | 4 000 | 4 000 edge scans | $m = 4\,000$ | **1.0000** |
| **Kahn** | DAG Dense | 1 000 | 20 000 | 20 000 edge scans | $m = 20\,000$ | **1.0000** |
| **Kahn** | DAG Sparse | 100 000 | 400 000 | 400 000 edge scans | $m = 400\,000$ | **1.0000** |
| **Kahn** | DAG Dense | 100 000 | 2 000 000 | 2 000 000 edge scans | $m = 2\,000\,000$ | **1.0000** |
| **DAG SP** | DAG Sparse | 1 000 | 4 000 | 4 000 edge scans | $m = 4\,000$ | **1.0000** |
| **DAG SP** | DAG Dense | 1 000 | 20 000 | 20 000 edge scans | $m = 20\,000$ | **1.0000** |
| **DAG SP** | DAG Sparse | 100 000 | 400 000 | 400 000 edge scans | $m = 400\,000$ | **1.0000** |
| **DAG SP** | DAG Dense | 100 000 | 2 000 000 | 2 000 000 edge scans | $m = 2\,000\,000$ | **1.0000** |
| **Kruskal** | Undir. Sparse | 1 000 | 4 000 | 9 161 DSU ops | $2.97 \times \text{scans}$ | **2.9724** |
| **Kruskal** | Undir. Sparse | 10 000 | 40 000 | 107 271 DSU ops | $2.78 \times \text{scans}$ | **2.7764** |
| **Kruskal** | Undir. Sparse | 100 000 | 400 000 | 1 099 333 DSU ops | $2.75 \times \text{scans}$ | **2.7506** |
| **Prim** | Undir. Sparse | 100 000 | 400 000 | 800 000 edge scans | $2m = 800\,000$ | **1.0000** |
| **Prim** | Undir. Dense | 100 000 | 2 000 000 | 4 000 000 edge scans | $2m = 4\,000\,000$ | **1.0000** |

### 2.2 Analysis of Invariants
- **Kosaraju:** Exactly scans $2m$ edges in all graphs. Pass 1 examines all $m$ directed edges in $G$ to determine post-order finish numbers. Pass 2 examines all $m$ edges in the transposed graph $G^T$. The ratio $\text{edgeScans} / (2m)$ is precisely $1.0000$.
- **Kahn and DAG Shortest Paths:** Each directed edge is traversed exactly once during queue-based degree decrement (Kahn) and topological relaxation (DAG SP). The ratio $\text{edgeScans} / m$ is strictly $1.0000$.
- **DSU Operations:** For Kruskal, each scanned edge performs two `find` calls to look up the components of endpoints $u$ and $v$, plus at most one `union` call if $u$ and $v$ belong to separate components. Because path compression keeps tree depths nearly 1, the ratio $\text{dsuOps} / \text{edgeScans}$ remains tightly bounded between $2.40$ and $2.97$.

---

## 3. Correctness Arguments

### 3.1 Cut Property (Kruskal & Prim)
Let $G = (V, E)$ be a connected, undirected weighted graph, and let $(S, V \setminus S)$ be any non-empty cut partitioning vertices into two disjoint sets. An edge $e = (u, v)$ crossing the cut is called the lightest crossing edge if $w(e) \le w(e')$ for all edges $e'$ with one endpoint in $S$ and the other in $V \setminus S$. Suppose an MST $T$ does not contain $e$. Adding $e$ to $T$ creates a unique simple cycle $C$ containing $e$. Because $u \in S$ and $v \in V \setminus S$, this cycle must cross the cut back from $V \setminus S$ to $S$ through at least one other crossing edge $e'$. Replacing $e'$ with $e$ yields a new spanning tree $T' = (T \setminus \{e'\}) \cup \{e\}$. Since $w(e) \le w(e')$, the total weight satisfies $w(T') = w(T) - w(e') + w(e) \le w(T)$. Since $T$ was minimal, $w(T') = w(T)$, proving that $e$ belongs to an MST and is always safe to add. Prim's algorithm maintains a growing tree $S$ and repeatedly selects the minimum-weight edge crossing $(S, V \setminus S)$, satisfying the cut property directly at every step. Kruskal's algorithm maintains a forest of components; whenever it picks the lightest global edge $(u, v)$ connecting distinct components $C_u$ and $C_v$, this edge is the lightest crossing edge for the cut $(C_u, V \setminus C_u)$, safely joining two components.

### 3.2 Induction on the Topological Order (DAG Shortest Paths)
Let $G = (V, E)$ be a directed acyclic graph (DAG) with vertices topologically ordered as $\langle v_1, v_2, \dots, v_n \rangle$. By definition of topological ordering, every directed edge $(v_i, v_j) \in E$ satisfies $i < j$, which implies that no edges can point backwards from higher indices to lower indices. We prove by induction on $k \in \{1, 2, \dots, n\}$ that when vertex $v_k$ is processed, its distance $dist[v_k]$ is strictly equal to the true shortest path distance from the virtual super-source to $v_k$. For the base case $k = 1$, vertex $v_1$ has in-degree zero; no edges enter $v_1$, so its distance is correctly initialized to $0$ and cannot be decreased further. For the induction hypothesis, assume that for all $i < k$, $dist[v_i]$ has already achieved its final shortest distance. Now consider vertex $v_k$: any incoming path to $v_k$ must arrive through an incoming edge $(v_i, v_k)$. Because $G$ is topologically ordered, all predecessors must satisfy $i < k$. By our inductive hypothesis, every such predecessor $v_i$ has already been completely finalized and its outgoing edges relaxed, updating $dist[v_k] \le dist[v_i] + w(v_i, v_k)$. Because there are no cycles and no forward-to-backward edges, no subsequent relaxation can ever discover a shorter path to $v_k$. Hence, a single pass over the topological order is sufficient to compute exact shortest paths, even in the presence of negative edge weights.

---

## 4. Which Algorithm When (Defense Decision Table)

| Problem | Recommended Algorithm | Preferred Graph Representation | Cycles Allowed? | Density Suitability | Engineering Rationale |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **MST** | **Kruskal** | Edge List / Adjacency List | Yes (undirected) | **Sparse graphs ($m \approx O(n)$)** | Kruskal sorts $m$ edges in $O(m \log m)$. On sparse graphs where $m \ll n^2$, sorting is fast, and DSU operations are extremely cache-friendly. Can terminate as soon as $n - 1$ edges are added. |
| **MST** | **Prim (Binary Heap)** | Adjacency List | Yes (undirected) | **Dense graphs ($m \approx O(n^2)$)** | On dense graphs, Kruskal suffers from sorting all $O(n^2)$ edges. Prim with an array or Fibonacci heap operates in $O(n^2)$ without sorting edges, winning by avoiding sorting bottlenecks. |
| **SCC** | **Kosaraju** | Adjacency List (Forward + Transpose) | Yes (directed) | Both sparse and dense | Straightforward to implement, clean two-pass structure. Ideal when the reversed graph can be easily obtained or when parallel finish-order processing is utilized. |
| **SCC** | **Tarjan** | Adjacency List (Single pass) | Yes (directed) | Both sparse and dense | Wins on edge scans ($m$ vs. $2m$), requiring only a single pass and no graph transposition. Best for large memory-constrained streaming graphs. |
| **Topological Sort**| **Kahn** | Adjacency List | **No (reports cycle)** | Both sparse and dense | In-degree queue approach naturally detects cycles when the output order length is less than $n$. Extremely practical for build schedulers and package managers. |
| **Shortest Paths** | **DAG Shortest Path** | Adjacency List | **No (requires DAG)** | Both sparse and dense | Optimal linear $O(n + m)$ runtime. Allows negative edge weights without risk of negative cycles (which are impossible in DAGs). Outperforms Dijkstra ($O(m \log n)$) and Bellman-Ford ($O(n \cdot m)$). |

---

## 5. Slope Check (Log-Log Work Scaling)

In a log-log plot of operations versus graph size $n$ ($\log \text{Work} = \alpha \log n + \beta$), a straight line of slope $\alpha = 1.00$ mathematically confirms linear asymptotic scaling $O(n + m)$.

Linear regression on our benchmark measurements yields:
- **Kosaraju (Sparse & Dense):** Fitted slope = **$1.0000$** ($R^2 = 1.000$).
- **Kahn (Sparse & Dense):** Fitted slope = **$1.0000$** ($R^2 = 1.000$).
- **DAG Shortest Paths (Sparse & Dense):** Fitted slope = **$1.0000$** ($R^2 = 1.000$).
- **Prim (Sparse & Dense):** Fitted slope = **$1.0000$** ($R^2 = 1.000$).
- **Kruskal (Sparse):** Fitted slope = **$1.0550$**.
- **Kruskal (Dense):** Fitted slope = **$1.1194$**.

### Why Kruskal's Slope is Steeper
While all traversal algorithms (Kosaraju, Kahn, DAG SP, and Prim's edge scans) scale with strict linearity ($\alpha = 1.0000$), Kruskal's edge examinations and comparisons scale slightly steeper than 1.0 ($\alpha \approx 1.06 - 1.12$). This empirical deviation is explained by the logarithmic factor in $O(m \log m)$ for edge sorting, combined with the inverse Ackermann factor $\alpha(n)$ across DSU queries.

---

## 6. Plots and Analysis

All three plots were programmatically generated from `results/results.csv` and are saved in `results/plots/`.

### 6.1 Plot 1: Operations vs. $n$ (Log-Log Scale)
![Operations vs n](results/plots/ops_vs_n.png)

- **Observations:** On log-log axes, Kosaraju, Kahn, DAG SP, and Prim appear as parallel straight lines with constant slope 1.00 across five orders of magnitude. Dense graphs lie exactly vertically shifted by a factor of 5 ($\log_{10} 5 \approx 0.70$) above sparse graphs, precisely mirroring the density ratio $m = 20n$ versus $m = 4n$.
- Kruskal's curve shows a slight upward curvature relative to the linear traversals due to edge sorting comparisons.

### 6.2 Plot 2: Scans Per Edge ($\text{edgeScans} / m$ vs. $n$)
![Scans Per Edge](results/plots/scans_per_edge.png)

- **Observations:** Horizontal, perfectly flat lines at constant values confirm linear time complexity:
  - Kosaraju: Constant horizontal line at exactly $2.00$ ($\text{edgeScans} = 2m$).
  - Kahn: Constant horizontal line at exactly $1.00$ ($\text{edgeScans} = m$).
  - DAG Shortest Paths: Constant horizontal line at exactly $1.00$ ($\text{edgeScans} = m$).
  - Prim: Constant horizontal line at exactly $2.00$ ($\text{edgeScans} = 2m$ on undirected adjacency lists).

### 6.3 Plot 3: Share of Edges Kruskal Examines ($\text{edgeScans} / m$)
![Share of Edges Kruskal Examines](results/plots/kruskal_edge_share.png)

- **Observations:** This plot highlights where each MST algorithm excels:
  - On **dense graphs ($m = 20n$)**, Kruskal examines only **$20\% - 37\%$** of total edges before collecting $n - 1$ tree edges and breaking early.
  - On **sparse graphs ($m = 4n$)**, Kruskal must examine **$77\% - 99.9\%$** of edges to find $n - 1$ tree edges.
  - While Kruskal benefits from early termination on dense graphs, it still pays the upfront cost of sorting all $m$ edges ($2\,000\,000 \log_2(2\,000\,000) \approx 4.18 \times 10^7$ comparisons), which explains why Prim becomes more attractive on dense graphs.

---

## 7. One Paragraph on Wall-Clock Time

Wall-clock execution times were measured as the median of 5 consecutive runs. On smaller graphs ($n = 1\,000$ and $n = 3\,162$), wall-clock times fluctuate between $0.3\text{ ms}$ and $1.5\text{ ms}$, where CPU frequency scaling, JIT compilation warm-up, and thread scheduling dominate, rendering the clock essentially noise. On the largest instance ($n = 100\,000, m = 2\,000\,000$), timings scale predictably: Kruskal took $178.6\text{ ms}$, Prim took $309.2\text{ ms}$, Kosaraju took $85.7\text{ ms}$, Kahn took $60.2\text{ ms}$, and DAG Shortest Paths took $74.5\text{ ms}$. However, wall-clock time varies across laptops, JVM implementations, and background system load. Therefore, the internal operation counters ($\text{dsuOps}, \text{edgeScans}, \text{comparisons}, \text{maxDepth}$) provide the only reproducible, hardware-agnostic proof of algorithm efficiency.

---

## 8. Bonus Tasks (+15%)

### 8.1 Task A: Tarjan's SCC Algorithm (+10%)
We implemented Tarjan's strongly connected components algorithm (`Tarjan.java`) using iterative explicit stacks to avoid stack overflow at $n = 100\,000$. Tarjan assigns discovery timestamps (`index[]`) and low-link values (`lowlink[]`), tracking active recursion vertices on an auxiliary stack. 

**Empirical Comparison:** Across all test graphs, Tarjan returned identical SCC partitions and component counts as Kosaraju (e.g., exactly 27 components on $1\,000 / 4\,000$, 1 component on $100\,000 / 2\,000\,000$, etc.). However, while Kosaraju required $2m$ edge scans (one forward pass, one transpose pass), Tarjan completed the partitioning in a single pass of $m$ edge scans. This delivers a direct **$50\%$ reduction in edge examinations**, saving substantial traversal time on large graph topologies.

### 8.2 Task B: Condensation Graph & Critical Path (+5%)
We implemented condensation DAG construction and critical path analysis (`CriticalPath.java`):
1. **Condensation DAG:** Every SCC found by Kosaraju/Tarjan is contracted into a single super-vertex. Cross-component directed edges form a directed acyclic graph (DAG) guaranteed to have no cycles.
2. **Critical Path (Longest Path):** In general graphs, finding the longest simple path is NP-hard (equivalent to the Hamiltonian Path problem). In a DAG, however, topological ordering permits solving the longest path in linear $O(V + E)$ time by maximizing path relaxation:
$$dist[v] = \max(dist[v], dist[u] + w(u, v))$$
3. **Application in Job Scheduling:** The critical path represents the minimum total duration required to complete all interdependent tasks in a workflow pipeline. Any delay in a task belonging to the critical path directly postpones project completion, pinpointing the exact scheduling bottlenecks.

---

## 9. Verification Summary & Reference Check

All 10 benchmark graph instances reproduced the exact reference values specified in the course assignment:

| Graph ($n$ / $m$) | Kruskal MST Weight | Prim MST Weight | Kosaraju SCC Count | DAG Distance Sum | Reference Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1 000 / 4 000** | 147 970 | 147 970 | 27 | 396 274 | **MATCH** |
| **1 000 / 20 000** | 30 363 | 30 363 | 1 | 252 411 | **MATCH** |
| **3 162 / 12 648** | 468 911 | 468 911 | 137 | 1 271 911 | **MATCH** |
| **3 162 / 63 240** | 97 589 | 97 589 | 1 | 723 157 | **MATCH** |
| **10 000 / 40 000** | 1 486 139 | 1 486 139 | 373 | 3 941 828 | **MATCH** |
| **10 000 / 200 000** | 309 550 | 309 550 | 1 | 2 381 342 | **MATCH** |
| **31 623 / 126 492** | 4 715 515 | 4 715 515 | 1 188 | 12 551 891 | **MATCH** |
| **31 623 / 632 460** | 968 517 | 968 517 | 1 | 7 376 612 | **MATCH** |
| **100 000 / 400 000** | 15 012 094 | 15 012 094 | 3 923 | 40 096 539 | **MATCH** |
| **100 000 / 2 000 000** | 3 056 681 | 3 056 681 | 1 | 23 512 087 | **MATCH** |

In addition, iterative DFS safely completed on a linear path of $100\,000$ vertices without stack overflow, recording `maxDepth = 100000`, and JUnit 5 tests confirmed equivalence with brute-force BFS on random graphs.
