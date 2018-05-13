package program.solver;

import program.Main;
import program.Problem;
import program.Solution;
import program.localSearch.LocalSearch;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldar on 08-May-18.
 */
public class SolverRAS extends Solver
{
    protected final LocalSearch localSearch;
    protected final double probabilityBestInModification; // a.k.a. q
    protected final double selectionPower;                // a.k.a. k


    public SolverRAS(Problem problem, int antNum, LocalSearch localSearch, double probabilityBestInModification, double selectionPower)
    {
        super(problem, antNum);

        if (localSearch == null)
            throw new IllegalArgumentException("Local search must be specified");

        this.localSearch = localSearch;

        if ((probabilityBestInModification < 0.0) || (probabilityBestInModification > 1.0))
            throw new IllegalArgumentException("Wrong probability best value");

        this.probabilityBestInModification = probabilityBestInModification;

        if (selectionPower <= 0.0)
            throw new IllegalArgumentException("K must be positive");

        this.selectionPower = selectionPower;
    }

    @Override
    public List<Solution> solve() throws Exception
    {
        long startTime = System.currentTimeMillis();

        // generate m random solutions

        List<Solution> solutions = new ArrayList<Solution>();

        for (int i = 0; i < antNum; i++)
            solutions.add(generateRandomSolution());

        // apply local search

        for (Solution solution : solutions)
            localSearch.search(solution);

        // initialize pheromone matrix according to the best found solution so far

        long bestQuality = Solution.findBestSolution(solutions).objective;

        pheromoneMap.initialize(problem.size, 1.0 / bestQuality);

        boolean intensificationIsActivated = true;

        while (System.currentTimeMillis() - startTime < runtime)  // until runtime is not finished
        {
            // solution manipulation

            List<Solution> newSolutions = Solution.deepCopyList(solutions);

            for (Solution solution : newSolutions)
            {
                modifySolution(solution);
                localSearch.search(solution);
            }

            if (intensificationIsActivated)
                for (int i = 0; i < solutions.size(); i++)
                    solutions.set(i, Solution.chooseBest(solutions.get(i), newSolutions.get(i)));
            else
                solutions = newSolutions;
        }

        return solutions;
    }






    protected void modifySolution(Solution solution)
    {
        for (int round = 0; round < problem.size; round++)   // number or rounds is equal to the problem size
        {
            if (Main.random.nextDouble() < probabilityBestInModification)  // if chooses exploiting policy
                swapExploiting(solution);
            else                                             // if chooses exploring policy
                swapExploring(solution);
        }
    }

    protected void swapExploiting(Solution solution)
    {
        int facility1 = Main.random.nextInt(problem.size);  // a.k.a r
        int location1 = solution.location[facility1];       // pi_r

        int bestFacility2 = -1;
        double bestValue = -1.0;

        double[][] pheromone = pheromoneMap.pheromone;

        for (int facility2 = 0; facility2 < problem.size; facility2++)
            if (facility2 != facility1)
            {
                int location2 = solution.location[facility2];   // pi_s

                double newValue = Math.pow(pheromone[facility1][location2], selectionPower) + Math.pow(pheromone[facility2][location1], selectionPower);

                if (newValue > bestValue)
                {
                    bestValue = newValue;
                    bestFacility2 = facility2;
                }
            }

        solution.doExchange(facility1, bestFacility2);
    }


    protected void swapExploring(Solution solution)
    {
        int facility1 = Main.random.nextInt(problem.size);  // a.k.a r
        int location1 = solution.location[facility1];       // pi_r

        double[] value = new double[problem.size];      // selection values tau[r,pi_s] + tau[s,pi_r]
        double valueSum = 0.0;                          // sum of all tau[r,pi_s] + tau[s,pi_r]

        double[][] pheromone = pheromoneMap.pheromone;

        // compute the tau[r,pi_s] + tau[s,pi_r] values for each facility

        for (int facility2 = 0; facility2 < problem.size; facility2++)
            if (facility1 != facility2)
            {
                int location2 = solution.location[facility2];   // pi_s

                double newValue = Math.pow(pheromone[facility1][location2], selectionPower) + Math.pow(pheromone[facility2][location1], selectionPower);

                value[facility2] = newValue;
                valueSum += newValue;
            }

        // compute the selection probabilities

        for (int i = 0; i < problem.size; i++)
            value[i] /= valueSum;


        // compute the interval border values

        double total = 0.0;

        for (int i = 1; i < problem.size; i++)
            value[i] += value[i - 1];

        // determine the "s"

        double dice = Main.random.nextDouble();

        int facility2 = problem.size - 1; // if none of the first n-1 elements is selected, then it is the last

        for (int i = 0; i < problem.size - 1; i++)
            if (dice < value[i])
            {
                facility2 = i;
                break;
            }

        solution.doExchange(facility1, facility2);
    }
}
