package lab6;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.Random;

public class MyFactory extends AbstractCandidateFactory<double[]> {

    private int dimension;

    public MyFactory(int dimension) {
        this.dimension = dimension;
    }

    public double[] generateRandomCandidate(Random random) {
        double[] solution = new double[dimension];
        // x from -5.0 to 5.0
        // your implementation:
        for (int i = 0; i < dimension; i++) {
            double randomValue = -5.0 + (10.0 * random.nextDouble());
            solution[i] = randomValue;
        }

        return solution;
    }
}

