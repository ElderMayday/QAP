import org.junit.*;
import program.Problem;
import program.Solution;
import program.solver.Solver;
import program.solver.SolverRAS;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;



/**
 * Created by Aldar on 08-May-18.
 */
public class TestSolver
{

    private Problem problem;

    @Before
    public void before() throws FileNotFoundException
    {
        problem = new Problem(new File("QAP_instances/bur26g.dat"));
    }

    @Test
    public void testSolver()
    {
        Solver solver = new SolverRAS(problem);

        Solution solution = solver.solve().get(0);

        // test that all cities must be visited

        boolean[] visited = new boolean[solution.location.length];

        for (int i = 0; i < solution.location.length; i++)
            visited[solution.location[i]] = true;

        for (int i = 0; i < visited.length; i++)
            assertEquals(true, visited[i]);
    }
}
