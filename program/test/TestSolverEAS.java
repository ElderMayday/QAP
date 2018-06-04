import org.junit.Before;
import org.junit.Test;
import program.Problem;
import program.Solution;
import program.localSearch.LocalSearchIdsia;
import program.solver.Solver;
import program.solver.SolverEAS;
import program.solver.SolverRAS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Aldar on 15-May-18.
 */
public class TestSolverEAS
{
    private Problem problem;

    @Before
    public void before() throws FileNotFoundException
    {
        problem = new Problem(new File("QAP_instances/chr15b.dat"));
    }

    @Test
    public void testSolver()
    {
        Solver solver = new SolverEAS(problem, 5, 0.8, new LocalSearchIdsia(),
                0.0, 2.0, 3, 1000, 1.0, false);

        Solution solution = null;
        try
        {
            solution = solver.solve();
        } catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }

        // test that all cities must be visited

        boolean[] visited = new boolean[solution.location.length];

        for (int i = 0; i < solution.location.length; i++)
            visited[solution.location[i]] = true;

        for (int i = 0; i < visited.length; i++)
            assertEquals(true, visited[i]);

        // test that optimized objective recomputation is correct

        long objective = solution.objective;
        solution.updateObjective();
        assertEquals(solution.objective, objective);
    }
}
