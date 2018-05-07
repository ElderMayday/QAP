import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;

/**
 * Created by Aldar on 08-May-18.
 */
public class TestProblem
{
    @Test
    public void testProblemLoad()
    {
        try
        {
            Problem problem = new Problem(new File("QAP_instances/bur26g.dat"));

            assertEquals(26, problem.size);
            assertEquals(53, problem.distance[0][0]);
            assertEquals(53, problem.distance[25][25]);
            assertEquals(3164, problem.flow[0][0]);
            assertEquals(0, problem.flow[25][25]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
