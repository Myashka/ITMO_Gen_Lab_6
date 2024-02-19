package lab6;

import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyCrossover extends AbstractCrossover<double[]> {
    protected MyCrossover() {
        super(1);
    }

    protected List<double[]> mate(double[] p1, double[] p2, int i, Random random) {
        ArrayList children = new ArrayList();

        // your implementation:
        double[] c1 = p1.clone();
        double[] c2 = p2.clone();
        double l, alpha=0.2;
        l = random.nextDouble();

        for (int j = 0; j < c1.length; j++) {
            double min = Math.min(p1[i], p2[i]);
            double max = Math.max(p1[i], p2[i]);
            double range = max - min;

            double lowerBound = min - range * alpha;
            double upperBound = max + range * alpha;

            lowerBound = Math.max(lowerBound, -5);
            upperBound = Math.min(upperBound, 5);

            c1[i] = lowerBound + random.nextDouble() * (upperBound - lowerBound);
            c2[i] = lowerBound + random.nextDouble() * (upperBound - lowerBound);
            
        }
        
        children.add(c1);
        children.add(c2);    

        return children;
    }
}
