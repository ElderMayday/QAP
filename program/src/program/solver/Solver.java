package program.solver;

import program.Main;
import program.PheromoneMap;
import program.Problem;
import program.Solution;
import program.localSearch.LocalSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * HAS-QAP algorithm regardless the pheromone trail update implementation
 * Created by Aldar on 08-May-18.
 */
public abstract class Solver
{
    protected final PheromoneMap pheromoneMap;
    protected final Problem problem;
    protected final LocalSearch localSearch;              // may be null
    protected final int antNum;                           // a.k.a. m
    protected final long runtime;                         // = number of facilities in seconds
    protected final double evaporationRemains;            // a.k.a. rho
    protected final int roundsToReinitialize;             // how many rounds are left before the next reinitialization
    protected final double probabilityBestInModification; // a.k.a. q
    protected final double selectionPower;                // a.k.a. k
    protected final double factorQ;                       // a.k.a. Q

    protected final boolean mustOutputBestUpdate;

    public Solver(Problem problem, int antNum, double evaporationRemains, LocalSearch localSearch, int roundsToReinitialize,
                  double probabilityBestInModification, double selectionPower, double factorQ, boolean mustOutputBestUpdate)
    {
        if (problem == null)
            throw new IllegalArgumentException("Problem must be specified");

        this.problem = problem;
        this.pheromoneMap = new PheromoneMap();

        this.localSearch = localSearch;

        if (antNum < 1)
            throw new IllegalArgumentException("Ant number must be positive");

        this.antNum = antNum;

        this.runtime = problem.size * 1000;

        if ((evaporationRemains < 0.0) || (evaporationRemains > 1.0))
            throw new IllegalArgumentException("Evaporation value is wrong");

        this.evaporationRemains = evaporationRemains;

        if (roundsToReinitialize < 0)
            throw new IllegalArgumentException("Wrong roundsToReinitialize value");

        this.roundsToReinitialize = roundsToReinitialize;

        if ((probabilityBestInModification < 0.0) || (probabilityBestInModification > 1.0))
            throw new IllegalArgumentException("Wrong probability best value");

        this.probabilityBestInModification = probabilityBestInModification;

        if (selectionPower <= 0.0)
            throw new IllegalArgumentException("K must be positive");

        this.selectionPower = selectionPower;

        if (factorQ <= 0.0)
            throw new IllegalArgumentException("Q must be positive");

        this.factorQ = factorQ;

        this.mustOutputBestUpdate = mustOutputBestUpdate;
    }


    /**
     * Solves the problem with configuration specified in the constructor
     * @return Obtained global-best solution
     * @throws Exception
     */
    public Solution solve() throws Exception
    {
        long startTime = System.currentTimeMillis();

        // generate m random solutions

        List<Solution> solutions = new ArrayList<Solution>();

        for (int i = 0; i < antNum; i++)
            solutions.add(generateRandomSolution());

        // apply local search if necessary

        if (localSearch != null)
            for (Solution solution : solutions)
                localSearch.search(solution);

        // initialize pheromone matrix according to the best found solution so far

        Solution best = Solution.chooseBestInList(solutions);   // global best (among all iterations)

        if (mustOutputBestUpdate)
            System.out.println("1:" + best.objective);

        pheromoneMap.initialize(problem.size, 1.0 / best.objective);

        boolean intensificationIsActivated = true;

        int countToDiversify = roundsToReinitialize; // how many round are left before the next diversification

        long round = 2; // number of rounds (evaluations passed)

        while (System.currentTimeMillis() - startTime < runtime)  // until runtime is not finished
        {
            // solution manipulation

            List<Solution> newSolutions = Solution.deepCopyList(solutions);

            if (localSearch != null)  // if need to apply local search
            {
                for (Solution solution : newSolutions)
                {
                    modifySolution(solution);              // pi^
                    localSearch.search(solution);          // pi~
                }
            }
            else                     // same modification, but without local search
            {
                for (Solution solution : newSolutions)
                    modifySolution(solution);              // pi^
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
                countToDiversify = roundsToReinitialize;        // postpone the diversification

                if (mustOutputBestUpdate)
                    System.out.println(round + ":" + best.objective);

                //System.out.println(best.objective);
            }

            updateMatrix(solutions, best);

            countToDiversify--;
            round++;
            if (countToDiversify <= 0)  // apply diversification if got stuck for a long time
            {
                pheromoneMap.initialize(problem.size, 1.0 / best.objective);
                countToDiversify = roundsToReinitialize;
                //System.out.println("diversify");
            }
        }

        return best;
    }


    /**
     * Perform pheromone update operation based on the given current iteration solutions and global best solution
     * @param solutions
     * @param best
     */
    protected abstract void updateMatrix(List<Solution> solutions, Solution best);



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
