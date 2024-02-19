package lab6;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lab6.IslandsAlg;
import lab6.Algorithm;

import java.time.Duration;
import java.time.Instant;

public class ExperimentRunner {

    public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        int[] complexities = {1, 2, 3, 4, 5};
        int n = 10;
        int generations = 1000;
        int dimension = 50;
        int populationSize = 100;
        
        String outputFile = "./exps.txt";
        Path filePath = Paths.get(outputFile);
        try {
            Files.createFile(filePath);
        } catch (IOException ex) {
            System.err.println("Error creating file: " + ex.getMessage());
        }
        appendToFile(outputFile, "Experiment Results\n");

        for (int complexity: complexities) {
            Setup setup = new Setup(complexity, dimension, n, generations, populationSize);
            executeExperiment(setup);
            String result = generateReport(setup);
            appendToFile(outputFile, result);
        }
    }

    private static void appendToFile(String filePath, String content) {
        try {
            Files.writeString(Paths.get(filePath), content, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println("Error writing to file: " + ex.getMessage());
        }
    }

    public static void executeExperiment(Setup setup) {
        for (AlgoSetup as: setup.algos) {
            Instant start = Instant.now();
            for (int i = 0; i < setup.n; i++) {
                Algorithm alg = as.get();
                double best_fit = alg.run(setup.dimension, setup.complexity, setup.populationSize, setup.generations);
                as.best_fit += best_fit;
            }
            Instant end = Instant.now();
            Duration timeElapsed = Duration.between(start, end);
            System.out.println(timeElapsed.toMillis());
            as.time = timeElapsed.toMillis();
        }
        setup.average();
    }

    public static String generateReport(Setup setup) {
        StringBuilder result = new StringBuilder("Complexity " + setup.complexity);
        for (int i = 0; i < setup.algos.size(); i++) {
            String name = setup.names[i].contains("Master") ? (setup.args[i] ? "Single" : "Multi") : "Islands";

            String time = String.format("%.2f", setup.algos.get(i).time).replace('.', ',');
            String bestFit = String.format("%.2f", setup.algos.get(i).best_fit).replace('.', ',');

            result.append(" | ").append(name).append(" | ").append(time).append(" | ").append(bestFit);
        }
        result.append("\n");
        return result.toString();
    }

}

class Setup {
    String[] names = {
        "lab6.MasterSlaveAlg",
        "lab6.MasterSlaveAlg",
        "lab6.IslandsAlg"
    };
    boolean[] args = {
        true,
        false,
        false,
    };
    ArrayList<AlgoSetup> algos;
    int complexity;
    int n;
    int generations;
    int populationSize;
    int dimension;

    Setup(int complexity,
          int dimension,
          int n,
          int generations,
          int populationSize) {

        this.complexity = complexity;
        this.dimension = dimension;
        this.n = n;
        this.generations = generations;
        this.populationSize = populationSize;

        algos = new ArrayList<AlgoSetup>();
        for (int i = 0; i < names.length; i++) {
            AlgoSetup a = new AlgoSetup(names[i], args[i]);
            algos.add(a);
        }
    }

    void average() {
        for (AlgoSetup a: algos) {
            a.time /= n;
            a.best_fit /= n;
        }
    }

}

class AlgoSetup {
    Constructor<?> c;
    double best_fit;
    double time;
    boolean arg;

    AlgoSetup(String name, boolean arg) {
        Class<?> cls;
        try {
            cls = Class.forName(name);
            c = cls.getConstructor(boolean.class);
        } catch (IllegalArgumentException | ClassNotFoundException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        best_fit = 0;
        time = 0;
        this.arg = arg;
    }

    Algorithm get() {
        Algorithm a;
        try {
            a = (Algorithm) c.newInstance(arg);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("FAILURE");
            a = new IslandsAlg();
        }
        return a;
    }
}