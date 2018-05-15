package program.solver;

import program.Main;
import program.Problem;
import program.Solution;
import program.SolutionComparator;
import program.localSearch.LocalSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldar on 08-May-18.
 */
public class SolverRAS extends Solver
{
    protected final int numberOfDepositing;               // a.k.a. w


    public SolverRAS(Problem problem, int antNum, double evaporationRemains, LocalSearch localSearch,
                     double probabilityBestInModification, double selectionPower, int numberOfDepositing, int roundsToReinitialize, double factorQ)
    {
        super(problem, antNum, evaporationRemains, localSearch, roundsToReinitialize, probabilityBestInModification, selectionPower, factorQ);

        if ((numberOfDepositing < 0) || (numberOfDepositing > antNum))
            throw new IllegalArgumentException("Wrong number of depositing ants");

        this.numberOfDepositing = numberOfDepositing;
    }


    protected void updateMatrix(List<Solution> solutions, Solution best)
    {
        // evaporate

        pheromoneMap.evaporate(evaporationRemains);

        // deposit the global best solution

        pheromoneMap.deposit(best, (double) numberOfDepositing * factorQ / best.objective);

        // deposit the top [w - 1] best solutions

        solutions.sort(new SolutionComparator());

        int factor = numberOfDepositing - 1;

        for (int i = 0; i < numberOfDepositing - 1; i++)
        {
            Solution solution = solutions.get(i);
            pheromoneMap.deposit(solution, (double) factor * factorQ / solution.objective);
            factor--;
        }
    }
}
