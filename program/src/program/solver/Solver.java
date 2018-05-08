package program.solver;


import program.Main;
import program.PheromoneMap;
import program.Problem;
import program.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldar on 08-May-18.
 */
public abstract class Solver
{
    protected final PheromoneMap pheromoneMap;
    protected final Problem problem;


    public Solver(Problem problem)
    {
        this.problem = problem;

        this.pheromoneMap = new PheromoneMap(problem);
    }

    public abstract List<Solution> solve();


    protected Solution generateInitialSolution()
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
