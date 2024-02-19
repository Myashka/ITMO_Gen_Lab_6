package lab6;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MasterSlaveAlg implements Algorithm {
    double fitness;
    boolean single_thread;

    public MasterSlaveAlg(boolean st) {
        fitness = 0;
        single_thread = st;
        System.out.println(single_thread);
    }

    MasterSlaveAlg() {
        this(false);
    }

    public static void main(String[] args) {
        int dimension = 10; // dimension of problem
        int complexity = 1; // fitness estimation time multiplicator
        int populationSize = 50; // size of population
        int generations = 100; // number of generations

        MasterSlaveAlg ma = new MasterSlaveAlg();
        ma.run(
            dimension,
            complexity,
            populationSize,
            generations
        );
    }

    public double run(int dimension,
                           int complexity,
                           int populationSize,
                           int generations) {

        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        AbstractEvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<double[]>(
                factory, pipeline, evaluator, selection, populationSize, false, random);

        algorithm.setSingleThreaded(single_thread);

        algorithm.addEvolutionObserver(new EvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
                if (bestFit > fitness) {
                    fitness = bestFit;
                }
                // System.out.println("\tBest solution = " + Arrays.toString((double[])populationData.getBestCandidate()));
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);
        algorithm.evolve(populationSize, 1, terminate);

        return fitness;
    }
}