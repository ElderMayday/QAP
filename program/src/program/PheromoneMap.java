package program;

/**
 * Created by Aldar on 08-May-18.
 */
public class PheromoneMap
{
    public double[][] pheromone;   // pheromone trail value of assigning i-th facility to j-th location


    /**
     * Initializes the pheromone map with a single value for all solution components for the specified problem instance
     * @param problem
     * @param pheromoneValue
     */
    public PheromoneMap(Problem problem, double pheromoneValue)
    {
        pheromone = new double[problem.size][problem.size];

        for (int i = 0; i < problem.size; i++)
            for (int j = 0; j < problem.size; j++)
                pheromone[i][j] = pheromoneValue;
    }
}
