package program;

/**
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
}
