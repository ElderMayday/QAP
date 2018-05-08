package program;

/**
 * Created by Aldar on 08-May-18.
 */
public class PheromoneMap
{
    public double[][] pheromone;

    public PheromoneMap(Problem problem)
    {
        pheromone = new double[problem.size][problem.size];
    }
}
