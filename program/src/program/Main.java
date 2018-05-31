package program;

import program.localSearch.LocalSearch;
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


    /**
     * Performs one run on an instance with the configuration provided in the arguments
     * @param args
     */
    public static void main(String[] args)
    {
        run(args, true);
    }


    public static int run(String[] args, boolean mustOutputConsole)
    {
        Solution solution = null;

        try
        {
            Configuration conf = new Configuration(args);

            Solver solver = null;

            long startTime = System.currentTimeMillis();  // start clock

            Problem problem = new Problem(new File(conf.path));  // read the instance

            if (conf.seed != -1)
                random = new Random(conf.seed);    // set seed if specified

            LocalSearch localSearch = null;

            if (conf.localSearch == 1)
                localSearch = new LocalSearchIdsia();

            solver = defineSolver(problem, conf, localSearch);  // define the algorithm according to the given configuration

            solution = solver.solve();

            outputSolution(solution, conf.outputPath);    // output solution into a file if specified

            long stopTime = System.currentTimeMillis();   // end clock
            long elapsedTime = stopTime - startTime;

            if (mustOutputConsole)
            {
                if (conf.mode == 0)  // for irace: output objective value with total runtime
                {
                    Locale.setDefault(Locale.US);
                    System.out.println(solution.objective + " " + String.format("%1$.2f", elapsedTime / 1000.0));
                }

                if (conf.mode == 1)  // for custom: output the solution om screen
                    System.out.println(solution.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return solution.objective;
    }


    private static Solver defineSolver(Problem problem, Configuration conf, LocalSearch localSearch)
    {
        Solver solver = null;

        if (conf.algorithm == 0)   // define the algorithm
            solver = new SolverEAS(problem, conf.antNum, conf.evaporationRemains, localSearch,
                    conf.probabilityBestInModification, conf.selectionPower, conf.numberOfElitist,
                    conf.roundsToReinitialize, conf.factorQ);
        else
            solver = new SolverRAS(problem, conf.antNum, conf.evaporationRemains, localSearch,
                    conf.probabilityBestInModification, conf.selectionPower, conf.numberOfDepositing,
                    conf.roundsToReinitialize, conf.factorQ);

        return solver;
    }


    private static void outputSolution(Solution solution, String outputPath) throws FileNotFoundException, UnsupportedEncodingException
    {
        if (outputPath != null)
        {
            PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
            writer.println(solution.toString());
            writer.close();
        }
    }
}
