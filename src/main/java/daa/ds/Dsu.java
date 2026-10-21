package daa.ds;

public class Dsu {

    private final int[] parent;
    private final int[] rank;
    private int components;
    private long findCount;
    private long unionCount;

    public Dsu(int n) {
        this.parent = new int[n];
        this.rank = new int[n];
        makeSet(n);
    }

    public final void makeSet(int n) {
        this.components = n;
        this.findCount = 0;
        this.unionCount = 0;
        for (int i = 0; i < n; i++) {
            this.parent[i] = i;
            this.rank[i] = 0;
        }
    }

    public int find(int x) {
        findCount++;
        int root = x;
        while (root != parent[root]) {
            root = parent[root];
        }
        int curr = x;
        while (curr != root) {
            int next = parent[curr];
            parent[curr] = root;
            curr = next;
        }
        return root;
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }

        unionCount++;
        components--;

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }

    public int components() {
        return components;
    }

    public long getFindCount() {
        return findCount;
    }

    public long getUnionCount() {
        return unionCount;
    }

    public long getOps() {
        return findCount + unionCount;
    }
}
