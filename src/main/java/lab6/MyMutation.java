package lab6;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

import java.util.List;
import java.util.Random;

public class MyMutation implements EvolutionaryOperator<double[]> {
    public List<double[]> apply(List<double[]> population, Random random) {
        
        for (int j = 0; j < population.size(); j++) {
            double[] individual = population.get(j);
            int mutationCount = (int) Math.ceil(individual.length * 0.01); // 1% координат для мутации
            for (int m = 0; m < mutationCount; m++) {
                int index = random.nextInt(individual.length);
                double mutationCoefficient = random.nextDouble(); // sigma из U[0, 1]
                individual[index] += random.nextGaussian() * mutationCoefficient;
                individual[index] = Math.min(Math.max(individual[index], -5), 5);
            }
            population.set(j, individual);
        }

        return population;
    }
}
