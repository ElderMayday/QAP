package program.solver;


import program.Main;
import program.PheromoneMap;
import program.Problem;
import program.Solution;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldar on 08-May-18.
 */
public abstract class Solver
{
    protected final PheromoneMap pheromoneMap;
    protected final Problem problem;
    protected final int antNum;                     // a.k.a. m
    protected final long runtime;                   // = number of facilities in seconds


    public Solver(Problem problem, int antNum)
    {
        if (problem == null)
            throw new IllegalArgumentException("Problem must be specified");

        this.problem = problem;
        this.pheromoneMap = new PheromoneMap();

        if (antNum < 1)
            throw new IllegalArgumentException("Ant number must be positive");

        this.antNum = antNum;

        this.runtime = problem.size * 1000;
    }



    public abstract List<Solution> solve() throws Exception;



    /**
     * Generates a solution by random assignment of all facilities to locations
     * @return
     */
    protected Solution generateRandomSolution()
    {
        Solution solution = new Solution(problem);

        List<Integer> freeLocations = new ArrayList<Integer>();

        for (int i = 0; i < problem.size; i++)
            freeLocations.add(i);

        for (int i = 0; i < problem.size; i++)
        {
            int index = Main.random.nextInt(freeLocations.size());
            solution.assign(i, freeLocations.get(index));
            freeLocations.remove(index);
        }

        return solution;
    }
}
