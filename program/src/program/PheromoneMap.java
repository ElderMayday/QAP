package program;

/**
 * Pheromone map of squared shape n x n
 * Created by Aldar on 08-May-18.
 */
public class PheromoneMap
{
    public double[][] pheromone;   // pheromone trail value of assigning i-th facility to j-th location


    public void initialize(int size, double pheromoneValue)
    {
        pheromone = new double[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                pheromone[i][j] = pheromoneValue;
    }


    /**
     * Perform linear pheromone trail evaporation
     * @param evaporationRemains
     */
    public void evaporate(double evaporationRemains)
    {
        for (int i = 0; i < pheromone.length; i++)
            for (int j = 0; j < pheromone.length; j++)
                pheromone[i][j] *= evaporationRemains;
    }


    /**
     * Perform deposit of solution components of a given solution by a given constant value
     * @param solution
     * @param addPheromone
     */
    public void deposit(Solution solution, double addPheromone)
    {
        for (int i = 0; i < solution.location.length; i++)
            pheromone[i][solution.location[i]] += addPheromone;
    }
}
