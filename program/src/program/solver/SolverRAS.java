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
    protected final LocalSearch localSearch;
    protected final double probabilityBestInModification; // a.k.a. q
    protected final double selectionPower;                // a.k.a. k
    protected final int numberOfDepositing;               // a.k.a. w
    protected final int roundsToReinitialize;

    public SolverRAS(Problem problem, int antNum, double evaporationRemains, LocalSearch localSearch,
                     double probabilityBestInModification, double selectionPower, int numberOfDepositing, int roundsToReinitialize)
    {
        super(problem, antNum, evaporationRemains);

        if (localSearch == null)
            throw new IllegalArgumentException("Local search must be specified");

        this.localSearch = localSearch;

        if ((probabilityBestInModification < 0.0) || (probabilityBestInModification > 1.0))
            throw new IllegalArgumentException("Wrong probability best value");

        this.probabilityBestInModification = probabilityBestInModification;

        if (selectionPower <= 0.0)
            throw new IllegalArgumentException("K must be positive");

        this.selectionPower = selectionPower;

        if ((numberOfDepositing < 0) || (numberOfDepositing > antNum))
            throw new IllegalArgumentException("Wrong number of depositing number");

        this.numberOfDepositing = numberOfDepositing;

        if (roundsToReinitialize < 0)
            throw new IllegalArgumentException("Wrong roundsToReinitialize value");

        this.roundsToReinitialize = roundsToReinitialize;
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

        Solution best = Solution.chooseBestInList(solutions);   // global best (among all iterations)

        pheromoneMap.initialize(problem.size, 1.0 / best.objective);

        boolean intensificationIsActivated = true;

        int count = roundsToReinitialize;  // how many rounds are left before the next reinitialization

        while (System.currentTimeMillis() - startTime < runtime)  // until runtime is not finished
        {
            // solution manipulation

            List<Solution> newSolutions = Solution.deepCopyList(solutions);

            for (Solution solution : newSolutions)
            {
                modifySolution(solution);              // pi^
                localSearch.search(solution);          // pi~
            }

            if (intensificationIsActivated)  // intensify
            {
                boolean improved = false;  // at least one was improved

                for (int i = 0; i < solutions.size(); i++)
                {
                    Solution betterSolution = Solution.chooseBest(solutions.get(i), newSolutions.get(i));

                    if (solutions.get(i) != betterSolution)
                        improved = true;

                    solutions.set(i, betterSolution);
                }

                if (improved = false)    // if did not improve any solution then deactivate intensification
                    intensificationIsActivated = false;
            }
            else  // explore
            {
                solutions.clear();

                for (int i = 0; i < solutions.size(); i++)
                    solutions.add(new Solution(newSolutions.get(i)));
            }

            Solution newBest = Solution.chooseBestInList(newSolutions);

            if (newBest.objective < best.objective)
            {
                best = newBest;
                intensificationIsActivated = true;
                count = roundsToReinitialize;        // postpone the diversification
                //System.out.println(best.objective);
            }

            updateMatrix(solutions, best);

            count--;
            if (count <= 0)  // apply diversification if got stuck for a long time
            {
                pheromoneMap.initialize(problem.size, 1.0 / best.objective);
                count = roundsToReinitialize;
                //System.out.println("diversify");
            }
        }

        return solutions;
    }


    /**
     * Perform RAS pheromone update
     * @param solutions
     */
    protected void updateMatrix(List<Solution> solutions, Solution best)
    {
        // evaporate

        pheromoneMap.evaporate(evaporationRemains);

        // deposit the global best solution

        pheromoneMap.deposit(best, (double) numberOfDepositing / best.objective);

        // deposit the top [w - 1] best solutions

        solutions.sort(new SolutionComparator());

        int factor = numberOfDepositing - 1;

        for (int i = 0; i < numberOfDepositing - 1; i++)
        {
            Solution solution = solutions.get(i);
            pheromoneMap.deposit(solution, (double) factor / solution.objective);
            factor--;
        }
    }


    /**
     * Modify the solution according to the article's algorithm
     * @param solution
     */
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

    /**
     * Perform one swapping based on the best pair potential
     * @param solution
     */
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


    /**
     * Perform one probabilistic swapping
     * @param solution
     */
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
