package program.solver;

import program.Problem;
import program.Solution;
import program.localSearch.LocalSearch;

import java.util.List;

/**
 * Created by Aldar on 15-May-18.
 */
public class SolverEAS extends Solver
{
    protected int numberOfElitist;   // a.k.a. sigma

    public SolverEAS(Problem problem, int antNum, double evaporationRemains, LocalSearch localSearch,
                     double probabilityBestInModification, double selectionPower, int numberOfElitist, int roundsToReinitialize, double factorQ)
    {
        super(problem, antNum, evaporationRemains, localSearch, roundsToReinitialize, probabilityBestInModification, selectionPower, factorQ);

        if (numberOfElitist < 1)
            throw new IllegalArgumentException("Wrong number of elitist ants");

        this.numberOfElitist = numberOfElitist;
    }


    @Override
    protected void updateMatrix(List<Solution> solutions, Solution best)
    {
        // evaporate

        pheromoneMap.evaporate(evaporationRemains);

        // deposit to every iteration solution

        for (int i = 0; i < solutions.size(); i++)
        {
            Solution solution = solutions.get(i);
            pheromoneMap.deposit(solution, factorQ / solution.objective);
        }

        // deposit to the best solution (elitist ants)

        pheromoneMap.deposit(best, (double) numberOfElitist * factorQ / best.objective);
    }
}
