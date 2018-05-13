package program.solver;

import program.Problem;
import program.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldar on 08-May-18.
 */
public class SolverRAS extends Solver
{
    public SolverRAS(Problem problem, double pheromoneValue, double probabilityBest)
    {
        super(problem, pheromoneValue, probabilityBest);
    }

    @Override
    public List<Solution> solve()
    {
        List<Solution> solutions = new ArrayList<Solution>();

        solutions.add(generateInitialSolution());

        return solutions;
    }
}
