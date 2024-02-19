package lab6;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.Migration;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class IslandsAlg implements Algorithm {
    double fitness;
    int island_count;
    int epoch_length;

    IslandsAlg(int island_count) {
        fitness = 0;
        this.island_count = island_count;
        epoch_length = 50;
    }

    IslandsAlg() {
        this(5);
    }

    public IslandsAlg(boolean b) {
        this();
    }

    public static void main(String[] args) {
        int dimension = 50; // dimension of problem
        int complexity = 1; // fitness estimation time multiplicator
        int populationSize = 50; // size of population
        int generations = 10; // number of generations

        IslandsAlg islandAlg = new IslandsAlg();
        double f = islandAlg.run(
            dimension,
            complexity,
            populationSize,
            generations
        );
        System.out.println(f);
    }

    public double run(int dimension,
                            int complexity,
                            int populationSize,
                            int generations) {
        Random random = new Random(); // random
        populationSize /= island_count;
        generations /= epoch_length;

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        Migration migration = new RingMigration();

        IslandEvolution<double[]> island_model = new IslandEvolution<>(
            island_count,
            migration,
            factory,
            pipeline,
            evaluator,
            selection,
            random
        );


        island_model.addEvolutionObserver(new IslandEvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                System.out.println("Epoch " + populationData.getGenerationNumber() + ": " + bestFit);
                if (bestFit > fitness) {
                    fitness = bestFit;
                }
                // System.out.println("\tEpoch best solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }

            public void islandPopulationUpdate(int i, PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                if (bestFit > fitness) {
                    fitness = bestFit;
                }
                // System.out.println("Island " + i);
                // System.out.println("\tGeneration " + populationData.getGenerationNumber() + ": " + bestFit);
                // System.out.println("\tBest solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);
        island_model.evolve(populationSize, 1, epoch_length, 2, terminate);

        return fitness;
    }
}