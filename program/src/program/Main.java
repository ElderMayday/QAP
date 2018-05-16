package program;

import program.localSearch.LocalSearchIdsia;
import program.solver.Configuration;
import program.solver.Solver;
import program.solver.SolverEAS;
import program.solver.SolverRAS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Aldar on 07-May-18.
 */
public class Main
{
    public static Random random = new Random(10);

    public static void main(String[] args)
    {
        try
        {
            Configuration conf = new Configuration(args);

            Solver solver = null;

            if ((conf.mode == 0) || (conf.mode == 1))  // irace or custom mode
            {
                long startTime = System.currentTimeMillis();  // start clock

                Problem problem = new Problem(new File(conf.path));

                if (conf.seed != -1)
                    random = new Random(conf.seed);   // set seed if specified

                if (conf.algorithm == 0)   // define the algorithm
                    solver = new SolverEAS(problem, conf.antNum, conf.evaporationRemains, new LocalSearchIdsia(),
                            conf.probabilityBestInModification, conf.selectionPower, conf.numberOfElitist,
                            conf.roundsToReinitialize, conf.factorQ);
                else
                    solver = new SolverRAS(problem, conf.antNum, conf.evaporationRemains, new LocalSearchIdsia(),
                            conf.probabilityBestInModification, conf.selectionPower, conf.numberOfDepositing,
                            conf.roundsToReinitialize, conf.factorQ);

                Solution solution = solver.solve();

                outputSolution(solution, conf.outputPath);

                long stopTime = System.currentTimeMillis();   // end clock
                long elapsedTime = stopTime - startTime;

                if (conf.mode == 0)  // for irace output with time
                {
                    Locale.setDefault(Locale.US);
                    System.out.println(solution.objective + " " + String.format("%1$.2f", elapsedTime / 1000.0));
                }

                if (conf.mode == 1)  // for custom output the solution
                    System.out.println(solution.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void outputSolution(Solution solution, String outputPath) throws FileNotFoundException, UnsupportedEncodingException
    {
        if (outputPath != null)
        {
            PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
            writer.println(solution.toString());
            writer.close();
        }
    }
}
