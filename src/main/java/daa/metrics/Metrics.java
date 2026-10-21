package daa.metrics;

public class Metrics {
    public long dsuOps;
    public long edgeScans;
    public long comparisons;
    public int maxDepth;

    public Metrics() {
        reset();
    }

    public void reset() {
        this.dsuOps = 0;
        this.edgeScans = 0;
        this.comparisons = 0;
        this.maxDepth = 0;
    }

    public void updateDepth(int depth) {
        if (depth > this.maxDepth) {
            this.maxDepth = depth;
        }
    }
}
