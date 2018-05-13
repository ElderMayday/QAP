import org.junit.*;
import program.Problem;
import program.Solution;
import program.localSearch.LocalSearch;
import program.localSearch.LocalSearchIdsia;

import java.io.File;

import static org.junit.Assert.*;


/**
 * Created by Aldar on 12-May-18.
 */
public class TestLocalSearch
{
    @Test
    public void testLocalSearchIdsia()
    {
        Problem problem = null;

        try
        {
            problem = new Problem(new File("QAP_instances/chr15b.dat"));

            Solution solution = new Solution(problem);

            solution.assign(0, 8);
            solution.assign(1, 12);
            solution.assign(2, 3);
            solution.assign(3, 4);
            solution.assign(4, 0);
            solution.assign(5, 11);
            solution.assign(6, 9);
            solution.assign(7, 14);
            solution.assign(8, 7);
            solution.assign(9, 2);
            solution.assign(10, 1);
            solution.assign(11, 13);
            solution.assign(12, 6);
            solution.assign(13, 5);
            solution.assign(14, 10);

            LocalSearch localSearch = new LocalSearchIdsia();

            localSearch.search(solution);

            // test that all cities must be visited

            boolean[] visited = new boolean[solution.location.length];

            for (int i = 0; i < solution.location.length; i++)
                visited[solution.location[i]] = true;

            for (int i = 0; i < visited.length; i++)
                assertEquals(true, visited[i]);

            // test that optimized objective recomputation is correct

            double objective = solution.objective;
            solution.updateObjective();
            assertEquals(solution.objective, objective, 0.0001);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
