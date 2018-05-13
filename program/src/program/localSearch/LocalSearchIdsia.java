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

                tryExchange(solution, indexFrom, indexTo);
            }
        }
    }

    /**
     * Performs an exchange of pi_i and pi_j in case if it improves the objective
     * @param solution
     * @param i facility #1
     * @param j facility #2
     */
    public void tryExchange(Solution solution, int i, int j)
    {
        Problem problem = solution.problem;

        int[][] a = problem.distance;
        int[][] b = problem.flow;

        // optimized computation of delta (instead of full recomputation)

        int pi_i = solution.location[i];  // location #1
        int pi_j = solution.location[j];  // location #2

        long delta = (b[i][j] - b[j][i]) * (a[pi_i][pi_j] - a[pi_j][pi_i]);  // minus delta actually

        for (int k = 0; k < problem.size; k++)
            if ((k != i) && (k != j))
            {
                int pi_k = solution.location[k];

                delta += b[i][k] * (a[pi_i][pi_k] - a[pi_j][pi_k]) + b[k][i] * (a[pi_k][pi_i] - a[pi_k][pi_j])
                        + b[j][k] * (a[pi_j][pi_k] - a[pi_i][pi_k]) + b[k][j] * (a[pi_k][pi_j] - a[pi_k][pi_i]);
            }

         if (delta > 0)  // if such local move is profitable then perform it
         {
             solution.location[i] = pi_j;
             solution.location[j] = pi_i;

             solution.objective -= delta;
         }
    }
}
