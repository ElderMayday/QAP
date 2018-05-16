package program;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Solution for QAP problem instance
 * Some fields are public, so external classes must also care about its consistency
 * Created by Aldar on 08-May-18.
 */
public class Solution
{
    public final Problem problem;     // the problem for which this solution is
    public final int[] location;      // a.k.a. psi, array of indices of locations assigned to the facilities
    protected final boolean[] locationIsTaken;   // array of flags whether the corresponding location was taken
    protected final boolean[] facilityIsTaken;   // array of flags whether the corresponding facility was taken
    public int numUnassigned;               // number of unassigned locations/facilities
    public int objective;                      // is not updated until construction is finished or updateObjective() is called

    public Solution(Problem problem)
    {
        this.problem = problem;

        location = new int[problem.size];

        numUnassigned = problem.size;                  // none are assigned

        facilityIsTaken = new boolean[problem.size];     // none facilities are taken in the beginning
        for (int i = 0; i < problem.size; i++)
            facilityIsTaken[i] = false;

        locationIsTaken = new boolean[problem.size];     // none locations are taken in the beginning
        for (int i = 0; i < problem.size; i++)
            locationIsTaken[i] = false;

        objective = -1;
    }

    public Solution(Solution solution)
    {
        this.problem = solution.problem;

        int size = problem.size;

        this.location = Arrays.copyOf(solution.location, size);
        this.locationIsTaken = Arrays.copyOf(solution.locationIsTaken, size);
        this.facilityIsTaken = Arrays.copyOf(solution.facilityIsTaken, size);
        this.numUnassigned = solution.numUnassigned;
        this.objective = solution.objective;
    }


    public static List<Solution> deepCopyList(List<Solution> solutions)
    {
        List<Solution> result = new ArrayList<Solution>();

        for (int i = 0; i < solutions.size(); i++)
            result.add(new Solution(solutions.get(i)));

        return result;
    }

    public static Solution chooseBest(Solution one, Solution two)
    {
        return one.objective < two.objective ? one : two;
    }


    /**
     * Try to assign a facility to a location
     * @param facility
     * @param toLocation
     * @return flag whether the assignment was done
     */
    public boolean assign(int facility, int toLocation)
    {
        if (!facilityIsTaken[facility])
        {
            if (!locationIsTaken[toLocation])
            {
                location[facility] = toLocation;
                facilityIsTaken[facility] = true;
                locationIsTaken[toLocation] = true;
                numUnassigned--;

                if (isComplete())
                    updateObjective();

                return true;
            }
            else
                return false;
        }
        else
            return false;
    }


    /**
     * Try to unassign a facility from a location
     * May be useful in case of local search
     * @param facility
     * @return flag whether the unassignment was done
     */
    public boolean unassign(int facility)
    {
        if (facilityIsTaken[facility])
        {
            if (isComplete())
                objective = -1;

            int toLocation = location[facility];

            facilityIsTaken[facility] = false;
            locationIsTaken[toLocation] = false;
            numUnassigned++;

            return true;
        }
        else
            return false;
    }


    /**
     * Updates objective value
     * Called manually or when construction is finished
     */
    public void updateObjective()
    {
        if (!isComplete())
        {
            objective = -1;
            return;
        }

        int result = 0;

        for (int facility1 = 0; facility1 < problem.size; facility1++)
            for (int facility2 = 0; facility2 < problem.size; facility2++)
                if (facility1 != facility2)
                {
                    int location1 = location[facility1];
                    int location2 = location[facility2];

                    result += problem.distance[location1][location2] * problem.flow[facility1][facility2];
                }

        objective = result;
    }


    /**
     * Predicate whether the solution construction is complete
     * @return
     */
    public boolean isComplete()
    {
        return numUnassigned == 0;
    }


    /**
     * Finds the best solution among the given list of solutions
     * @param solutions
     * @return
     */
    public static Solution chooseBestInList(List<Solution> solutions)
    {
        Solution best = solutions.get(0);

        for (int i = 1; i < solutions.size(); i++)
        {
            Solution next = solutions.get(i);

            if (next.objective < best.objective)
                best = next;
        }

        return best;
    }



    /**
     * Determines the difference of the objective values in case if i-th and j-th facilities are swapped.
     * Is done in O(n) time
     * Use this for pair swapping, because of lack of necessity to recompute the objective in O(n^2)
     * @param i
     * @param j
     * @return
     */
    public long delta(int i, int j)
    {
        int[][] a = problem.distance;
        int[][] b = problem.flow;

        int pi_i = location[i];  // location #1
        int pi_j = location[j];  // location #2

        long delta = (b[i][j] - b[j][i]) * (a[pi_i][pi_j] - a[pi_j][pi_i]);  // minus delta actually

        for (int k = 0; k < problem.size; k++)
            if ((k != i) && (k != j))
            {
                int pi_k = location[k];

                delta += b[i][k] * (a[pi_i][pi_k] - a[pi_j][pi_k]) + b[k][i] * (a[pi_k][pi_i] - a[pi_k][pi_j])
                        + b[j][k] * (a[pi_j][pi_k] - a[pi_i][pi_k]) + b[k][j] * (a[pi_k][pi_j] - a[pi_k][pi_i]);
            }

        return delta;
    }


    /**
     * Performs an exchange of pi_i and pi_j in case if it improves the objective
     * @param i facility #1
     * @param j facility #2
     */
    public void tryExchange(int i, int j)
    {
        long delta = delta(i, j);

        if (delta > 0)  // if such local move is profitable then perform it
        {
            int buffer = location[i];
            location[i] = location[j];
            location[j] = buffer;

            objective -= delta;
        }
    }

    /**
     * Performs an exchange of pi_i and pi_j in any case
     * @param i facility #1
     * @param j facility #2
     */
    public void doExchange(int i, int j)
    {
        long delta = delta(i, j);

        int buffer = location[i];
        location[i] = location[j];
        location[j] = buffer;

        objective -= delta;
    }



    @Override
    public String toString()
    {
        String result = "obj=" + objective + " ";

        for (int i = 0; i < location.length; i++)
            result += i + "-" + location[i] + " ";

        return result;
    }
}
