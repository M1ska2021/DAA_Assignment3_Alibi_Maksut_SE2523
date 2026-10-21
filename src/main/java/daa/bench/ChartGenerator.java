package daa.bench;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartGenerator {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;
    private static final int PAD_LEFT = 90;
    private static final int PAD_RIGHT = 180;
    private static final int PAD_TOP = 60;
    private static final int PAD_BOTTOM = 70;

    public static void generateAll(List<BenchmarkRunner.Row> rows, String outDir) throws IOException {
        File dir = new File(outDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        drawOperationsVsN(rows, new File(dir, "ops_vs_n.png"));
        drawScansPerEdge(rows, new File(dir, "scans_per_edge.png"));
        drawKruskalEdgeShare(rows, new File(dir, "kruskal_edge_share.png"));
    }

    private static void drawOperationsVsN(List<BenchmarkRunner.Row> rows, File outFile) throws IOException {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = initGraphics(img);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int plotW = WIDTH - PAD_LEFT - PAD_RIGHT;
        int plotH = HEIGHT - PAD_TOP - PAD_BOTTOM;

        double minN = 1000;
        double maxN = 100000;
        double minOp = 1000;
        double maxOp = 100000000;

        drawGridLog(g2, plotW, plotH, minN, maxN, minOp, maxOp, "Vertices (n)", "Operations / Work (log scale)", "Operations vs n (Log-Log Scale)");

        Map<String, List<double[]>> seriesMap = new HashMap<>();
        for (BenchmarkRunner.Row r : rows) {
            String key = r.algorithm + " (" + (r.graph.contains("sparse") ? "sparse" : "dense") + ")";
            long work = r.edgeScans + r.dsuOps + r.comparisons;
            if (work == 0) work = r.edgeScans;
            seriesMap.computeIfAbsent(key, k -> new ArrayList<>()).add(new double[]{r.n, work});
        }

        Color[] colors = {
                new Color(31, 119, 180), new Color(174, 199, 232),
                new Color(255, 127, 14), new Color(255, 187, 120),
                new Color(44, 160, 44), new Color(152, 223, 138),
                new Color(214, 39, 40), new Color(255, 152, 150),
                new Color(148, 103, 189), new Color(197, 176, 213)
        };

        int colorIdx = 0;
        int legendY = PAD_TOP + 10;
        for (Map.Entry<String, List<double[]>> entry : seriesMap.entrySet()) {
            Color c = colors[colorIdx % colors.length];
            colorIdx++;

            List<double[]> points = entry.getValue();
            points.sort((a, b) -> Double.compare(a[0], b[0]));

            int[] px = new int[points.size()];
            int[] py = new int[points.size()];
            for (int i = 0; i < points.size(); i++) {
                double xVal = points.get(i)[0];
                double yVal = Math.max(minOp, points.get(i)[1]);
                px[i] = (int) (PAD_LEFT + (Math.log10(xVal) - Math.log10(minN)) / (Math.log10(maxN) - Math.log10(minN)) * plotW);
                py[i] = (int) (PAD_TOP + plotH - (Math.log10(yVal) - Math.log10(minOp)) / (Math.log10(maxOp) - Math.log10(minOp)) * plotH);
            }

            g2.setColor(c);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawPolyline(px, py, points.size());
            for (int i = 0; i < points.size(); i++) {
                g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
            }

            g2.drawLine(WIDTH - PAD_RIGHT + 15, legendY, WIDTH - PAD_RIGHT + 35, legendY);
            g2.fillOval(WIDTH - PAD_RIGHT + 22, legendY - 3, 6, 6);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.drawString(entry.getKey(), WIDTH - PAD_RIGHT + 42, legendY + 4);
            legendY += 17;
        }

        g2.dispose();
        ImageIO.write(img, "png", outFile);
    }

    private static void drawScansPerEdge(List<BenchmarkRunner.Row> rows, File outFile) throws IOException {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = initGraphics(img);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int plotW = WIDTH - PAD_LEFT - PAD_RIGHT;
        int plotH = HEIGHT - PAD_TOP - PAD_BOTTOM;

        double minN = 1000;
        double maxN = 100000;
        double minY = 0.0;
        double maxY = 2.5;

        drawGridLinearY(g2, plotW, plotH, minN, maxN, minY, maxY, "Vertices (n, log scale)", "Scans per edge (edgeScans / m)", "Scans Per Edge vs n (Linear Work Confirmation)");

        Map<String, List<double[]>> seriesMap = new HashMap<>();
        for (BenchmarkRunner.Row r : rows) {
            String key = r.algorithm + " (" + (r.graph.contains("sparse") ? "sparse" : "dense") + ")";
            double ratio = (double) r.edgeScans / r.m;
            seriesMap.computeIfAbsent(key, k -> new ArrayList<>()).add(new double[]{r.n, ratio});
        }

        Color[] colors = {
                new Color(31, 119, 180), new Color(174, 199, 232),
                new Color(255, 127, 14), new Color(255, 187, 120),
                new Color(44, 160, 44), new Color(152, 223, 138),
                new Color(214, 39, 40), new Color(255, 152, 150),
                new Color(148, 103, 189), new Color(197, 176, 213)
        };

        int colorIdx = 0;
        int legendY = PAD_TOP + 10;
        for (Map.Entry<String, List<double[]>> entry : seriesMap.entrySet()) {
            Color c = colors[colorIdx % colors.length];
            colorIdx++;

            List<double[]> points = entry.getValue();
            points.sort((a, b) -> Double.compare(a[0], b[0]));

            int[] px = new int[points.size()];
            int[] py = new int[points.size()];
            for (int i = 0; i < points.size(); i++) {
                double xVal = points.get(i)[0];
                double yVal = Math.max(minY, Math.min(maxY, points.get(i)[1]));
                px[i] = (int) (PAD_LEFT + (Math.log10(xVal) - Math.log10(minN)) / (Math.log10(maxN) - Math.log10(minN)) * plotW);
                py[i] = (int) (PAD_TOP + plotH - (yVal - minY) / (maxY - minY) * plotH);
            }

            g2.setColor(c);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawPolyline(px, py, points.size());
            for (int i = 0; i < points.size(); i++) {
                g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
            }

            g2.drawLine(WIDTH - PAD_RIGHT + 15, legendY, WIDTH - PAD_RIGHT + 35, legendY);
            g2.fillOval(WIDTH - PAD_RIGHT + 22, legendY - 3, 6, 6);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.drawString(entry.getKey(), WIDTH - PAD_RIGHT + 42, legendY + 4);
            legendY += 17;
        }

        g2.dispose();
        ImageIO.write(img, "png", outFile);
    }

    private static void drawKruskalEdgeShare(List<BenchmarkRunner.Row> rows, File outFile) throws IOException {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = initGraphics(img);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        int plotW = WIDTH - PAD_LEFT - PAD_RIGHT;
        int plotH = HEIGHT - PAD_TOP - PAD_BOTTOM;

        double minN = 1000;
        double maxN = 100000;
        double minY = 0.0;
        double maxY = 1.0;

        drawGridLinearY(g2, plotW, plotH, minN, maxN, minY, maxY, "Vertices (n, log scale)", "Share of edges examined (edgeScans / m)", "Share of Edges Kruskal Examines");

        List<double[]> sparsePts = new ArrayList<>();
        List<double[]> densePts = new ArrayList<>();

        for (BenchmarkRunner.Row r : rows) {
            if (r.algorithm.equals("Kruskal")) {
                double share = (double) r.edgeScans / r.m;
                if (r.graph.contains("sparse")) {
                    sparsePts.add(new double[]{r.n, share});
                } else {
                    densePts.add(new double[]{r.n, share});
                }
            }
        }

        sparsePts.sort((a, b) -> Double.compare(a[0], b[0]));
        densePts.sort((a, b) -> Double.compare(a[0], b[0]));

        drawSingleSeries(g2, sparsePts, minN, maxN, minY, maxY, plotW, plotH, new Color(31, 119, 180));
        drawSingleSeries(g2, densePts, minN, maxN, minY, maxY, plotW, plotH, new Color(214, 39, 40));

        g2.setColor(new Color(31, 119, 180));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(WIDTH - PAD_RIGHT + 15, PAD_TOP + 40, WIDTH - PAD_RIGHT + 40, PAD_TOP + 40);
        g2.fillOval(WIDTH - PAD_RIGHT + 25, PAD_TOP + 37, 6, 6);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString("Kruskal Sparse (m=4n)", WIDTH - PAD_RIGHT + 48, PAD_TOP + 45);

        g2.setColor(new Color(214, 39, 40));
        g2.drawLine(WIDTH - PAD_RIGHT + 15, PAD_TOP + 70, WIDTH - PAD_RIGHT + 40, PAD_TOP + 70);
        g2.fillOval(WIDTH - PAD_RIGHT + 25, PAD_TOP + 67, 6, 6);
        g2.drawString("Kruskal Dense (m=20n)", WIDTH - PAD_RIGHT + 48, PAD_TOP + 75);

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString("Dense: Kruskal scans ~10%", WIDTH - PAD_RIGHT + 15, PAD_TOP + 110);
        g2.drawString("of edges due to early exit", WIDTH - PAD_RIGHT + 15, PAD_TOP + 126);
        g2.drawString("Sparse: Kruskal scans ~45%", WIDTH - PAD_RIGHT + 15, PAD_TOP + 150);
        g2.drawString("of edges to build spanning tree", WIDTH - PAD_RIGHT + 15, PAD_TOP + 166);

        g2.dispose();
        ImageIO.write(img, "png", outFile);
    }

    private static void drawSingleSeries(Graphics2D g2, List<double[]> pts, double minX, double maxX,
                                         double minY, double maxY, int plotW, int plotH, Color c) {
        int[] px = new int[pts.size()];
        int[] py = new int[pts.size()];
        for (int i = 0; i < pts.size(); i++) {
            double xVal = pts.get(i)[0];
            double yVal = pts.get(i)[1];
            px[i] = (int) (PAD_LEFT + (Math.log10(xVal) - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * plotW);
            py[i] = (int) (PAD_TOP + plotH - (yVal - minY) / (maxY - minY) * plotH);
        }
        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawPolyline(px, py, pts.size());
        for (int i = 0; i < pts.size(); i++) {
            g2.fillOval(px[i] - 4, py[i] - 4, 8, 8);
        }
    }

    private static Graphics2D initGraphics(BufferedImage img) {
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static void drawGridLog(Graphics2D g2, int plotW, int plotH,
                                    double minX, double maxX, double minY, double maxY,
                                    String xLabel, String yLabel, String title) {
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(PAD_LEFT, PAD_TOP, plotW, plotH);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(PAD_LEFT, PAD_TOP, plotW, plotH);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(title, PAD_LEFT, PAD_TOP - 25);

        int[] xTicks = {1000, 3162, 10000, 31623, 100000};
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int xt : xTicks) {
            int px = (int) (PAD_LEFT + (Math.log10(xt) - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * plotW);
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(px, PAD_TOP, px, PAD_TOP + plotH);
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(px, PAD_TOP + plotH, px, PAD_TOP + plotH + 5);
            g2.drawString(String.valueOf(xt), px - 15, PAD_TOP + plotH + 20);
        }

        int[] yPowers = {3, 4, 5, 6, 7, 8};
        for (int yp : yPowers) {
            double val = Math.pow(10, yp);
            int py = (int) (PAD_TOP + plotH - (yp - Math.log10(minY)) / (Math.log10(maxY) - Math.log10(minY)) * plotH);
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(PAD_LEFT, py, PAD_LEFT + plotW, py);
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(PAD_LEFT - 5, py, PAD_LEFT, py);
            g2.drawString("10^" + yp, PAD_LEFT - 45, py + 4);
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(xLabel, PAD_LEFT + plotW / 2 - 40, PAD_TOP + plotH + 45);

        g2.rotate(-Math.PI / 2);
        g2.drawString(yLabel, -(PAD_TOP + plotH / 2 + 60), PAD_LEFT - 55);
        g2.rotate(Math.PI / 2);
    }

    private static void drawGridLinearY(Graphics2D g2, int plotW, int plotH,
                                        double minX, double maxX, double minY, double maxY,
                                        String xLabel, String yLabel, String title) {
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(PAD_LEFT, PAD_TOP, plotW, plotH);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(PAD_LEFT, PAD_TOP, plotW, plotH);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(title, PAD_LEFT, PAD_TOP - 25);

        int[] xTicks = {1000, 3162, 10000, 31623, 100000};
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int xt : xTicks) {
            int px = (int) (PAD_LEFT + (Math.log10(xt) - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * plotW);
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(px, PAD_TOP, px, PAD_TOP + plotH);
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(px, PAD_TOP + plotH, px, PAD_TOP + plotH + 5);
            g2.drawString(String.valueOf(xt), px - 15, PAD_TOP + plotH + 20);
        }

        int steps = 5;
        for (int i = 0; i <= steps; i++) {
            double val = minY + (maxY - minY) * i / steps;
            int py = (int) (PAD_TOP + plotH - (val - minY) / (maxY - minY) * plotH);
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(PAD_LEFT, py, PAD_LEFT + plotW, py);
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(PAD_LEFT - 5, py, PAD_LEFT, py);
            g2.drawString(String.format("%.1f", val), PAD_LEFT - 35, py + 4);
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(xLabel, PAD_LEFT + plotW / 2 - 40, PAD_TOP + plotH + 45);

        g2.rotate(-Math.PI / 2);
        g2.drawString(yLabel, -(PAD_TOP + plotH / 2 + 80), PAD_LEFT - 50);
        g2.rotate(Math.PI / 2);
    }
}
