import org.junit.*;
import program.Problem;
import program.Solution;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;



/**
 * Created by Aldar on 08-May-18.
 */
public class TestSolution
{
    private Problem problem;

    @Before
    public void before() throws FileNotFoundException
    {
        problem = new Problem(new File("QAP_instances/bur26g.dat"));
    }

    @Test
    public void testSimulateConstruction()
    {
        Solution solution = new Solution(problem);

        assertEquals(26, solution.numUnassigned);
        assertEquals(false, solution.isComplete());

        boolean result = solution.assign(10, 12);
        assertEquals(true, result);
        assertEquals(false, solution.isComplete());
        result = solution.assign(10, 12);
        assertEquals(false, result);
        assertEquals(false, solution.isComplete());
        result = solution.unassign(10);
        assertEquals(true, result);

        assertEquals(-1, solution.objective);

        for (int i = 0; i < problem.size; i++)
            solution.assign(i, i);

        assertEquals(true, solution.isComplete());
        assertEquals(10425188, solution.objective);
    }
}
