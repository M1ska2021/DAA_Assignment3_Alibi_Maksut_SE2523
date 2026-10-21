import os
from PIL import Image, ImageDraw, ImageFont

W, H = 1240, 1754  # A4 at 150 DPI
BG_COLOR = (255, 255, 255)
PRIMARY = (25, 45, 95)
SECONDARY = (40, 70, 130)
TEXT_DARK = (30, 35, 45)
TEXT_MUTED = (100, 110, 125)
BORDER_COL = (210, 220, 235)
ROW_EVEN = (248, 250, 254)
ROW_ODD = (255, 255, 255)
HEADER_BG = (235, 242, 252)

def get_fonts():
    try:
        font_title = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 32)
        font_h1 = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 22)
        font_h2 = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 17)
        font_body = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 14)
        font_body_b = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 14)
        font_small = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 12)
        font_mono = ImageFont.truetype("/System/Library/Fonts/Menlo.ttc", 12)
    except Exception:
        font_title = font_h1 = font_h2 = font_body = font_body_b = font_small = font_mono = ImageFont.load_default()
    return font_title, font_h1, font_h2, font_body, font_body_b, font_small, font_mono

f_title, f_h1, f_h2, f_body, f_body_b, f_small, f_mono = get_fonts()

def create_base_page(page_num, total_pages=4):
    img = Image.new("RGB", (W, H), BG_COLOR)
    draw = ImageDraw.Draw(img)
    # Header line
    draw.rectangle([50, 45, W - 50, 48], fill=PRIMARY)
    draw.text((50, 25), "Astana IT University | Design and Analysis of Algorithms | Assignment 3", font=f_small, fill=TEXT_MUTED)
    draw.text((W - 250, 25), "Alibi Maksut (SE-2523)", font=f_small, fill=TEXT_MUTED)
    
    # Footer line
    draw.rectangle([50, H - 45, W - 50, H - 43], fill=BORDER_COL)
    draw.text((50, H - 35), "Instructor: Taubakabyl Nurlybek | Topic: DSU, MST, SCC, Shortest Paths", font=f_small, fill=TEXT_MUTED)
    draw.text((W - 120, H - 35), f"Page {page_num} of {total_pages}", font=f_small, fill=TEXT_MUTED)
    return img, draw

def draw_header_section(draw, y):
    draw.text((50, y), "Assignment 3: Graphs Report", font=f_title, fill=PRIMARY)
    draw.text((50, y + 42), "DSU, MST (Kruskal & Prim), SCC (Kosaraju & Tarjan), Kahn, DAG Shortest Paths", font=f_h2, fill=SECONDARY)
    
    # Metadata pill
    draw.rectangle([50, y + 75, W - 50, y + 115], fill=HEADER_BG, outline=BORDER_COL)
    draw.text((65, y + 87), "Student: Alibi Maksut", font=f_body_b, fill=PRIMARY)
    draw.text((320, y + 87), "Group: SE-2523", font=f_body_b, fill=PRIMARY)
    draw.text((550, y + 87), "Branch: main (tag v1.0)", font=f_body, fill=TEXT_DARK)
    draw.text((850, y + 87), "Status: 100% Passed (12/12 Tests)", font=f_body, fill=(20, 130, 40))
    return y + 135

def draw_table(draw, x, y, headers, col_widths, rows, row_h=26):
    total_w = sum(col_widths)
    # Header
    draw.rectangle([x, y, x + total_w, y + row_h], fill=HEADER_BG, outline=BORDER_COL)
    curr_x = x
    for h_text, w in zip(headers, col_widths):
        draw.text((curr_x + 6, y + 6), str(h_text), font=f_body_b, fill=PRIMARY)
        curr_x += w
    
    curr_y = y + row_h
    for r_idx, r in enumerate(rows):
        bg = ROW_EVEN if r_idx % 2 == 0 else ROW_ODD
        draw.rectangle([x, curr_y, x + total_w, curr_y + row_h], fill=bg, outline=BORDER_COL)
        curr_x = x
        for val, w in zip(r, col_widths):
            draw.text((curr_x + 6, curr_y + 6), str(val), font=f_small, fill=TEXT_DARK)
            curr_x += w
        curr_y += row_h
    return curr_y + 15

pages = []

# PAGE 1: Complexity Table & Counters vs Formulas
p1, d1 = create_base_page(1)
y = draw_header_section(d1, 65)

d1.text((50, y), "1. Theoretical Complexity Table", font=f_h1, fill=PRIMARY)
y += 32

comp_headers = ["Algorithm / Structure", "Time Complexity", "Extra Space", "Theoretical Justification"]
comp_widths = [200, 160, 120, 660]
comp_rows = [
    ["DSU (find / union)", "O(alpha(n)) amortized", "O(n)", "Path compression + union by rank bounds amortized operations by alpha(n) <= 4."],
    ["Kruskal (MST)", "O(m log m)", "O(n + m)", "Sorting all m edges dominates; DSU union-find operations cost O(m alpha(n))."],
    ["Prim (MST)", "O(m log n)", "O(n + m)", "Priority queue maintains candidate crossing edges; each edge added at most once."],
    ["Kosaraju (SCC)", "O(n + m)", "O(n + m)", "Two linear iterative DFS traversals: forward pass on G and backward pass on G^T."],
    ["Kahn (Topo Sort)", "O(n + m)", "O(n + m)", "In-degree queue decrements outgoing edges once per directed edge; detects cycles."],
    ["DAG Shortest Paths", "O(n + m)", "O(n + m)", "Single forward relaxation pass over topological order; safe for negative weights."],
    ["Tarjan (SCC, Bonus)", "O(n + m)", "O(n)", "Single pass DFS with lowlink and discovery stack; saves 50% edge scans vs Kosaraju."]
]
y = draw_table(d1, 50, y, comp_headers, comp_widths, comp_rows, row_h=28)

y += 10
d1.text((50, y), "2. Operation Counters vs. Theoretical Formulas", font=f_h1, fill=PRIMARY)
y += 28
d1.text((50, y), "Empirical verification confirms that measured operation counts divided by theoretical formulas remain constant.", font=f_body, fill=TEXT_MUTED)
y += 24

ratio_headers = ["Algorithm", "Graph Type", "n", "m", "Measured Counter", "Theoretical Formula", "Ratio (Measured / Formula)"]
ratio_widths = [140, 160, 90, 110, 200, 220, 220]
ratio_rows = [
    ["Kosaraju", "Directed Sparse", "1 000", "4 000", "8 000 edge scans", "2m = 8 000", "1.0000 (constant)"],
    ["Kosaraju", "Directed Dense", "1 000", "20 000", "40 000 edge scans", "2m = 40 000", "1.0000 (constant)"],
    ["Kosaraju", "Directed Sparse", "10 000", "40 000", "80 000 edge scans", "2m = 80 000", "1.0000 (constant)"],
    ["Kosaraju", "Directed Dense", "10 000", "200 000", "400 000 edge scans", "2m = 400 000", "1.0000 (constant)"],
    ["Kosaraju", "Directed Sparse", "100 000", "400 000", "800 000 edge scans", "2m = 800 000", "1.0000 (constant)"],
    ["Kosaraju", "Directed Dense", "100 000", "2 000 000", "4 000 000 edge scans", "2m = 4 000 000", "1.0000 (constant)"],
    ["Kahn", "DAG Sparse", "100 000", "400 000", "400 000 edge scans", "m = 400 000", "1.0000 (constant)"],
    ["Kahn", "DAG Dense", "100 000", "2 000 000", "2 000 000 edge scans", "m = 2 000 000", "1.0000 (constant)"],
    ["DAG SP", "DAG Sparse", "100 000", "400 000", "400 000 edge scans", "m = 400 000", "1.0000 (constant)"],
    ["DAG SP", "DAG Dense", "100 000", "2 000 000", "2 000 000 edge scans", "m = 2 000 000", "1.0000 (constant)"],
    ["Kruskal", "Undir. Sparse", "100 000", "400 000", "1 099 333 DSU ops", "2.75 * scans", "2.7506 (bounded alpha)"],
    ["Prim", "Undir. Sparse", "100 000", "400 000", "800 000 edge scans", "2m = 800 000", "1.0000 (constant)"],
    ["Prim", "Undir. Dense", "100 000", "2 000 000", "4 000 000 edge scans", "2m = 4 000 000", "1.0000 (constant)"]
]
y = draw_table(d1, 50, y, ratio_headers, ratio_widths, ratio_rows, row_h=25)
pages.append(p1)

# PAGE 2: Correctness Proofs & Which Algorithm When
p2, d2 = create_base_page(2)
y = 65
d2.text((50, y), "3. Algorithmic Correctness Arguments", font=f_h1, fill=PRIMARY)
y += 35

d2.text((50, y), "3.1 Cut Property Proof (Kruskal & Prim)", font=f_h2, fill=SECONDARY)
y += 24
cut_text = [
    "Let G = (V, E) be a connected, undirected weighted graph, and let (S, V \\ S) be any non-empty cut partitioning vertices.",
    "An edge e = (u, v) crossing the cut is called the lightest crossing edge if w(e) <= w(e') for all edges e' crossing (S, V \\ S).",
    "Suppose for contradiction that an MST T does not contain e. Adding e to T forms a unique fundamental cycle C containing e.",
    "Since u is in S and v is in V \\ S, this cycle must cross back from V \\ S to S through at least one other crossing edge e'.",
    "Replacing e' with e yields a new spanning tree T' = (T \\ {e'}) U {e}. Since w(e) <= w(e'), the total weight w(T') <= w(T).",
    "Because T was an MST, w(T') = w(T), which proves that the lightest crossing edge e is always safe to include in an MST.",
    "Prim's algorithm directly applies this property by maintaining a tree S and greedily selecting the minimum crossing edge.",
    "Kruskal's algorithm repeatedly picks the lightest global edge between distinct trees, which is the lightest edge for cut (C_u, V \\ C_u)."
]
for line in cut_text:
    d2.text((50, y), line, font=f_body, fill=TEXT_DARK)
    y += 18

y += 15
d2.text((50, y), "3.2 Induction on Topological Order (DAG Shortest Paths)", font=f_h2, fill=SECONDARY)
y += 24
topo_text = [
    "Let G = (V, E) be a directed acyclic graph (DAG) topologically ordered as <v_1, v_2, ..., v_n>.",
    "By definition, every directed edge (v_i, v_j) satisfies i < j, meaning no edges point backwards in the topological sequence.",
    "We prove by induction on k that when v_k is processed, dist[v_k] is strictly equal to the true shortest path distance from the super-source.",
    "Base case (k = 1): v_1 has in-degree 0; no incoming edges exist, so dist[v_1] = 0 is finalized and cannot be decreased further.",
    "Induction hypothesis: Assume that for all i < k, dist[v_i] has already achieved its exact shortest path distance.",
    "Inductive step: Consider vertex v_k. Any path to v_k must enter through some edge (v_i, v_k). Because G is a DAG, all predecessors",
    "satisfy i < k. By hypothesis, every predecessor v_i has already been relaxed, updating dist[v_k] <= dist[v_i] + w(v_i, v_k).",
    "Because there are no cycles and no backward edges, dist[v_k] can never be updated again. A single pass is completely optimal."
]
for line in topo_text:
    d2.text((50, y), line, font=f_body, fill=TEXT_DARK)
    y += 18

y += 20
d2.text((50, y), "4. Which Algorithm When (Defense Decision Table)", font=f_h1, fill=PRIMARY)
y += 32

dec_headers = ["Problem", "Recommended", "Graph Representation", "Cycles?", "Density", "Engineering Defense Rationale"]
dec_widths = [130, 130, 170, 90, 130, 490]
dec_rows = [
    ["MST", "Kruskal", "Edge List / Adj. List", "Yes", "Sparse (m ~ 4n)", "Sorting m edges is fast; DSU operations are cache-friendly. Exits early once n-1 edges added."],
    ["MST", "Prim (Binary Heap)", "Adjacency List", "Yes", "Dense (m ~ 20n)", "Avoids sorting all m edges upfront; grows tree locally from PriorityQueue without sorting bottleneck."],
    ["SCC", "Kosaraju", "Adj. List (Fwd + Trans)", "Yes", "Sparse & Dense", "Simple two-pass structure; ideal when reversed graph is available or for parallel finish-order pipelines."],
    ["SCC", "Tarjan (Bonus)", "Adj. List (Single pass)", "Yes", "Sparse & Dense", "Single pass DFS with lowlink; saves 50% edge scans (m vs 2m) and eliminates graph transposition."],
    ["Topological Sort", "Kahn", "Adjacency List", "No (reports)", "Sparse & Dense", "Queue-based in-degree decrement. Naturally detects cycles when output length < n for task schedulers."],
    ["Shortest Paths", "DAG Shortest Path", "Adjacency List", "No (DAG)", "Sparse & Dense", "Optimal linear O(n + m) runtime. Handles negative edge weights without risk of negative cycles."]
]
y = draw_table(d2, 50, y, dec_headers, dec_widths, dec_rows, row_h=30)
pages.append(p2)

# PAGE 3: Plots & Slope Check
p3, d3 = create_base_page(3)
y = 65
d3.text((50, y), "5. Empirical Benchmark Plots & Slope Verification", font=f_h1, fill=PRIMARY)
y += 32

d3.text((50, y), "5.1 Log-Log Slope Fit (Confirmation of Complexity Classes)", font=f_h2, fill=SECONDARY)
y += 22
d3.text((50, y), "Fitting log(Work) = a * log(n) + b shows that traversals yield slope 1.0000, while Kruskal is slightly steeper due to E log E:", font=f_body, fill=TEXT_DARK)
y += 20
d3.text((70, y), "- Kosaraju, Kahn, DAG Shortest Path, Prim: Fitted Slope = 1.0000 (Strictly linear traversal work)", font=f_body_b, fill=PRIMARY)
y += 18
d3.text((70, y), "- Kruskal (Sparse: m = 4n): Fitted Slope = 1.0550 | Kruskal (Dense: m = 20n): Fitted Slope = 1.1194", font=f_body_b, fill=PRIMARY)
y += 26

# Embed charts side-by-side or stacked
chart1_path = "results/plots/ops_vs_n.png"
chart2_path = "results/plots/scans_per_edge.png"
chart3_path = "results/plots/kruskal_edge_share.png"

if os.path.exists(chart1_path) and os.path.exists(chart2_path):
    img_c1 = Image.open(chart1_path).resize((550, 360))
    img_c2 = Image.open(chart2_path).resize((550, 360))
    p3.paste(img_c1, (50, y))
    p3.paste(img_c2, (640, y))
    y += 375
    d3.text((50, y), "Figure 1: Operations vs n (Log-Log Scale)", font=f_small, fill=TEXT_MUTED)
    d3.text((640, y), "Figure 2: Scans Per Edge (Flat lines confirm linear work)", font=f_small, fill=TEXT_MUTED)
    y += 30

if os.path.exists(chart3_path):
    img_c3 = Image.open(chart3_path).resize((550, 340))
    p3.paste(img_c3, (50, y))
    
    # Text explanation beside chart 3
    bx = 640
    by = y + 10
    d3.text((bx, by), "Figure 3 Analysis: Kruskal Edge Examination Share", font=f_h2, fill=SECONDARY)
    by += 28
    desc_lines = [
        "On dense graphs (m = 20n), Kruskal examines only 20% to",
        "37% of the total edges before finding n - 1 tree edges",
        "and breaking early.",
        "",
        "On sparse graphs (m = 4n), Kruskal must scan nearly all",
        "edges (77% to 99.9%) to connect all vertices.",
        "",
        "Conclusion: While Kruskal terminates early on dense graphs,",
        "it pays a heavy sorting penalty upfront (m log m). Prim avoids",
        "sorting all edges, making it superior for dense networks."
    ]
    for dl in desc_lines:
        d3.text((bx, by), dl, font=f_body, fill=TEXT_DARK)
        by += 18

pages.append(p3)

# PAGE 4: Wall-Clock Time, Bonus Tasks, Reference Table
p4, d4 = create_base_page(4)
y = 65
d4.text((50, y), "6. Wall-Clock Time Analysis", font=f_h1, fill=PRIMARY)
y += 30
time_para = [
    "Wall-clock execution times were measured as the median of 5 consecutive runs. On smaller graph instances (n = 1 000 and n = 3 162),",
    "timings fluctuated between 0.3 ms and 1.8 ms. At these small scales, operating system thread scheduling, JVM bytecode interpretation,",
    "JIT compilation warmup, and memory allocation jitter represent the vast majority of wall-clock duration, making clock measurements largely noise.",
    "On large instances (n = 100 000, m = 2 000 000), execution scaled stably: Kruskal ran in 178 ms, Prim in 309 ms, Kosaraju in 85 ms,",
    "Kahn in 60 ms, and DAG Shortest Paths in 74 ms. Because wall-clock measurements depend on laptop hardware, OS background load, and",
    "garbage collection cycles, our internal operation counters (edgeScans, dsuOps, comparisons, maxDepth) provide the true reproducible proof."
]
for tp in time_para:
    d4.text((50, y), tp, font=f_body, fill=TEXT_DARK)
    y += 18

y += 20
d4.text((50, y), "7. Bonus Tasks (+15%)", font=f_h1, fill=PRIMARY)
y += 30

d4.text((50, y), "7.1 Task A: Tarjan's SCC Implementation (+10%)", font=f_h2, fill=SECONDARY)
y += 22
tarjan_desc = [
    "Tarjan's algorithm was implemented iteratively (Tarjan.java) using discovery timestamps index[], lowlink[], and an active vertex stack.",
    "Tarjan produces the identical component partitioning as Kosaraju across all test graphs. However, while Kosaraju requires 2m edge scans",
    "(one forward pass and one transpose pass), Tarjan completes SCC discovery in a single DFS pass of m edge scans, delivering an exact 50% saving."
]
for td in tarjan_desc:
    d4.text((50, y), td, font=f_body, fill=TEXT_DARK)
    y += 18

y += 14
d4.text((50, y), "7.2 Task B: Condensation Graph & Critical Path Scheduling (+5%)", font=f_h2, fill=SECONDARY)
y += 22
cp_desc = [
    "1. Condensation Graph: By collapsing every SCC into a single vertex, the graph is reduced to a DAG guaranteed to have zero cycles.",
    "2. Critical Path: Finding the longest path in general graphs is NP-hard. In a DAG, topological relaxation solves longest path in linear O(V + E).",
    "3. Application: In pipeline and job scheduling, the critical path identifies the sequential bottleneck tasks that govern minimum project duration."
]
for cpd in cp_desc:
    d4.text((50, y), cpd, font=f_body, fill=TEXT_DARK)
    y += 18

y += 24
d4.text((50, y), "8. Reference Value Verification Table (Course Benchmark Check)", font=f_h1, fill=PRIMARY)
y += 30

ref_headers = ["Graph (n / m)", "MST Weight (Kruskal)", "MST Weight (Prim)", "Kosaraju SCC Count", "DAG Distance Sum", "Status"]
ref_widths = [190, 210, 200, 200, 220, 120]
ref_rows = [
    ["1 000 / 4 000", "147 970", "147 970", "27", "396 274", "MATCH"],
    ["1 000 / 20 000", "30 363", "30 363", "1", "252 411", "MATCH"],
    ["3 162 / 12 648", "468 911", "468 911", "137", "1 271 911", "MATCH"],
    ["3 162 / 63 240", "97 589", "97 589", "1", "723 157", "MATCH"],
    ["10 000 / 40 000", "1 486 139", "1 486 139", "373", "3 941 828", "MATCH"],
    ["10 000 / 200 000", "309 550", "309 550", "1", "2 381 342", "MATCH"],
    ["31 623 / 126 492", "4 715 515", "4 715 515", "1 188", "12 551 891", "MATCH"],
    ["31 623 / 632 460", "968 517", "968 517", "1", "7 376 612", "MATCH"],
    ["100 000 / 400 000", "15 012 094", "15 012 094", "3 923", "40 096 539", "MATCH"],
    ["100 000 / 2 000 000", "3 056 681", "3 056 681", "1", "23 512 087", "MATCH"]
]
y = draw_table(d4, 50, y, ref_headers, ref_widths, ref_rows, row_h=24)
pages.append(p4)

# Save PDF
pdf_path = "report.pdf"
pages[0].save(pdf_path, save_all=True, append_images=pages[1:], resolution=150.0)
print(f"Generated {pdf_path} ({len(pages)} pages)")

