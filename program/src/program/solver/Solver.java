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

    protected final double probabilityBest;


    public Solver(Problem problem, double pheromoneValue, double probabilityBest)
    {
        this.problem = problem;

        this.pheromoneMap = new PheromoneMap(problem, pheromoneValue);

        if ((probabilityBest < 0.0) || (probabilityBest > 1.0))
            throw new IllegalArgumentException("Wrong probability best value");

        this.probabilityBest = probabilityBest;
    }



    public abstract List<Solution> solve();


    /**
     * Generates an initial solution for all Solver implementations
     * @return
     */
    protected Solution generateInitialSolution()
    {
        Solution solution = generateRandomSolution();

        return solution;
    }


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


    protected void modifySolution(Solution solution)
    {
        for (int round = 0; round < problem.size; round++)   // number or rounds is equal to the problem size
        {
            if (Main.random.nextDouble() < probabilityBest)  // if chooses exploiting policy
                swapExploiting(solution);
            else                                             // if chooses exploring policy
                swapExploring(solution);
        }
    }

    protected void swapExploiting(Solution solution)
    {
        int index1 = Main.random.nextInt(problem.size);
    }


    protected void swapExploring(Solution solution)
    {
        throw new NotImplementedException();
    }
}
