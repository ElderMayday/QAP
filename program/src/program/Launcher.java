package program;

import java.util.*;

/**
 * Created by Aldar on 31-May-18.
 */
public class Launcher
{

    /**
     * Performs 10 runs per instance for each of the predefined configurations, counts best worst and standard deviation
     * @param args
     */
    public static void main(String[] args)
    {
        int rounds = 10;

        List<String> instanceNames = Arrays.asList("bur26g.dat",
                "chr15b.dat", "esc16d.dat", "had14.dat",
                "had20.dat", "nug28.dat", "scr12.dat",
                "tai17a.dat", "tai35a.dat", "wil50.dat");

        String folderPath = "QAP_instances/";

        List<String> configurations = new ArrayList<String>();

        // without local search
        configurations.add("--local-search-none --eas --m 24 --rho 0.97 --rounds-reinitialize 8693 --q 0.61 --k 0.29 --Q 2.34 --sigma 54 --end");
        configurations.add("--local-search-none --ras --m 70 --rho 0.38 --rounds-reinitialize 2128 --q 0.28 --k 0.64 --Q 3.2 --w 13 --end");

        // with local search (not tuned)
        configurations.add("--local-search-idsia --eas --m 24 --rho 0.97 --rounds-reinitialize 8693 --q 0.61 --k 0.29 --Q 2.34 --sigma 54 --end");
        configurations.add("--local-search-idsia --ras --m 70 --rho 0.38 --rounds-reinitialize 2128 --q 0.28 --k 0.64 --Q 3.2 --w 13 --end");

        // with local search (tuned)
        configurations.add("--local-search-idsia --eas --m 85 --rho 0.32 --rounds-reinitialize 8672 --q 0.42 --k 2.09 --Q 3.6 --sigma 54 --end");
        configurations.add("--local-search-idsia --ras --m 88 --rho 0.83 --rounds-reinitialize 8474 --q 0.67 --k 1.01 --Q 1.68 --w 15 --end");


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
}
