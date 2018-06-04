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
import java.util.*;

/**
 * Created by Aldar on 07-May-18.
 */
public class Main
{
    public static Random random = new Random(10);

    protected static String configurationEasNols = "--local-search-none --eas --m 24 --rho 0.97 --rounds-reinitialize 8693 --q 0.61 --k 0.29 --Q 2.34 --sigma 54 --end";
    protected static String configurationRasNols = "--local-search-none --ras --m 70 --rho 0.38 --rounds-reinitialize 2128 --q 0.28 --k 0.64 --Q 3.2 --w 13 --end";
    protected static String configurationEasLsNotTuned = "--local-search-idsia --eas --m 24 --rho 0.97 --rounds-reinitialize 8693 --q 0.61 --k 0.29 --Q 2.34 --sigma 54 --end";
    protected static String configurationRasLsNotTuned = "--local-search-idsia --ras --m 70 --rho 0.38 --rounds-reinitialize 2128 --q 0.28 --k 0.64 --Q 3.2 --w 13 --end";
    protected static String configurationEasLsTuned = "--local-search-idsia --eas --m 85 --rho 0.32 --rounds-reinitialize 8672 --q 0.42 --k 2.09 --Q 3.6 --sigma 54 --end";
    protected static String configurationRasLsTuned = "--local-search-idsia --ras --m 88 --rho 0.83 --rounds-reinitialize 8474 --q 0.67 --k 1.01 --Q 1.68 --w 15 --end";

    protected static String folderPath = "QAP_instances/";

    /**
     * Performs one run on an instance with the configuration provided in the arguments or experimental series
     * @param args
     */
    public static void main(String[] args)
    {
        Locale.setDefault(Locale.US);

        if (!args[0].equals("--experiments") && !args[0].equals("--convergence"))  // irace or custom mode
            run(args, true);
        else if (args[0].equals("--convergence"))       // convergence series
            convergence();
        else                                            // experimental series
            experiments();
    }


    public static void convergence()
    {
        String instancePath = folderPath + "chr15b.dat";

        List<String> configurations = new ArrayList<String>();

        // without local search
        configurations.add(configurationEasNols);
        configurations.add(configurationRasNols);

        // with local search (not tuned)
        configurations.add(configurationEasLsNotTuned);
        configurations.add(configurationRasLsNotTuned);

        // with local search (tuned)
        configurations.add(configurationEasLsTuned);
        configurations.add(configurationRasLsTuned);

        int seed = 1;

        for (String configuration : configurations)
        {
            System.out.println(configuration);

            String argumentsString = "configuration instance " + seed + " " + instancePath + " --best " + configuration;

            String arguments[] = argumentsString.split(" ");

            Main.run(arguments, false);
        }
    }

    // performs 10 runs per each instance per each predefined configuration
    public static void experiments()
    {
        int rounds = 10;

        List<String> instanceNames = Arrays.asList("bur26g.dat",
                "chr15b.dat", "esc16d.dat", "had14.dat",
                "had20.dat", "nug28.dat", "scr12.dat",
                "tai17a.dat", "tai35a.dat", "wil50.dat");

        List<String> configurations = new ArrayList<String>();

        // without local search
        configurations.add(configurationEasNols);
        configurations.add(configurationRasNols);

        // with local search (not tuned)
        configurations.add(configurationEasLsNotTuned);
        configurations.add(configurationRasLsNotTuned);

        // with local search (tuned)
        configurations.add(configurationEasLsTuned);
        configurations.add(configurationRasLsTuned);


        for (String configuration : configurations)
        {
            System.out.println(configuration);

            for (String instanceName : instanceNames)
            {
                System.out.println(instanceName);

                String path = folderPath + instanceName;

                int seed = 1;

                List<Integer> profits = new ArrayList<Integer>();

                for (int i = 0; i < rounds; i++)
                {
                    String argumentsString = "configuration instance " + seed + " " + path + " " + configuration;

                    String arguments[] = argumentsString.split(" ");

                    profits.add(Main.run(arguments, false));

                    seed++;
                }

                int min = Collections.min(profits);
                int max = Collections.max(profits);

                int sum = 0;

                for (Integer profit : profits)
                    sum += profit;

                double average = sum / rounds;

                double squareSum = 0.0;

                for (Integer profit: profits)
                    squareSum += Math.pow(profit - average, 2.0);

                double deviation = Math.sqrt(squareSum / (rounds - 1));

                System.out.println(min + ";" + max + ";" + String.format("%1$.2f", average) + ";" + String.format("%1$.2f", deviation));
            }
        }
    }


    // performs a single algorithm run
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


    /**
     * Defines a solver object for the given problem, configuration and local search
     * @param problem
     * @param conf
     * @param localSearch
     * @return
     */
    private static Solver defineSolver(Problem problem, Configuration conf, LocalSearch localSearch)
    {
        Solver solver = null;

        if (conf.algorithm == 0)   // define the algorithm
            solver = new SolverEAS(problem, conf.antNum, conf.evaporationRemains, localSearch,
                    conf.probabilityBestInModification, conf.selectionPower, conf.numberOfElitist,
                    conf.roundsToReinitialize, conf.factorQ, conf.mustOutputBestUpdate);
        else
            solver = new SolverRAS(problem, conf.antNum, conf.evaporationRemains, localSearch,
                    conf.probabilityBestInModification, conf.selectionPower, conf.numberOfDepositing,
                    conf.roundsToReinitialize, conf.factorQ, conf.mustOutputBestUpdate);

        return solver;
    }


    /**
     * Outputs the solution to a file if specified
     * @param solution
     * @param outputPath
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
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
