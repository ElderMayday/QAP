package program;


/**
 * Created by Aldar on 08-May-18.
 */
public class Solution
{
    public final Problem problem;     // the problem for which this solution is
    public final int[] location;      // a.k.a. psi, array of indices of locations assigned to the facilities
    protected final boolean[] locationIsTaken;   // array of flag whether the corresponding location was taken
    protected final boolean[] facilityIsTaken;   // array of flag whether the corresponding facility was taken
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
}
