package daa;

import daa.bench.BenchmarkRunner;
import daa.bench.ChartGenerator;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Benchmark for Assignment 3...");
        try {
            List<BenchmarkRunner.Row> rows = BenchmarkRunner.runAll();
            BenchmarkRunner.saveCsv(rows, "results/results.csv");
            System.out.println("Saved results to results/results.csv (" + rows.size() + " rows)");

            ChartGenerator.generateAll(rows, "results/plots");
            System.out.println("Generated charts in results/plots/");
            System.out.println("Benchmark successfully finished and verified.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
