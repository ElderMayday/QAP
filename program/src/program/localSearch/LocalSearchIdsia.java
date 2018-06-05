package program.localSearch;

import program.Main;
import program.Problem;
import program.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aldar on 12-May-18.
 */
public class LocalSearchIdsia extends LocalSearch
{

    /**
     * Performs local search according to the paper "Ant Colonies for the Quadratic Assignment Problem (Gambardella)"
     * @param solution
     * @throws Exception
     */
    @Override
    public void search(Solution solution) throws Exception
    {
        if (!solution.isComplete())
            throw new Exception("Cannot perform local search on an non-complete solution");

        Problem problem = solution.problem;

        // initialise listFrom (list of FROM which facility positions an exchange still must be tried)

        List<Integer> listFrom = new ArrayList<Integer>();

        for (int i = 0; i < problem.size; i++)
            listFrom.add(i);

        while (!listFrom.isEmpty())
        {
            // determine i

            int indexFrom = Main.random.nextInt(listFrom.size());
            int fromFacility = listFrom.get(indexFrom);  // a.k.a. i
            listFrom.remove(indexFrom);

            // initialise listTo (list of TO which facility positions an exchange still must be tried)

            List<Integer> listTo = new ArrayList<Integer>();

            for (int i = 0; i < problem.size; i++)
                if (i != fromFacility)
                    listTo.add(i);

            while (!listTo.isEmpty())
            {
                int indexTo = Main.random.nextInt(listTo.size());
                int toFacility = listTo.get(indexTo);   // a.k.a j
                listTo.remove(indexTo);

                solution.tryExchange(indexFrom, indexTo);
            }
        }
    }
}
