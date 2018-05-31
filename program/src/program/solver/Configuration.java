package program.solver;

/**
 * Configuration for the program run
 * Created by Aldar on 16-May-18.
 */
public class Configuration
{
    public int mode;  // 0 - irace, 1 - custom, 2 - experiment

    public int seed;
    public String path = null;
    public String outputPath = null;


    public int localSearch = -1;                 // 0 - None, 1 - LocalSearchIdsia
    public int algorithm = -1;                   // 0 - EAS, 1 - RAS
    public int antNum = -1;
    public double evaporationRemains = -1.0;            // a.k.a. rho
    public int roundsToReinitialize = -1;               // how many rounds are left before the next reinitialization
    public double probabilityBestInModification = -1.0; // a.k.a. q
    public double selectionPower = -1.0;                // a.k.a. k
    public double factorQ = -1.0;                       // a.k.a. Q
    public int numberOfElitist = -1;                    // a.k.a. sigma
    public int numberOfDepositing = -1;                 // a.k.a. w

    public Configuration(String[] args)
    {
        int index = 0;

        if (args[0].equals("--custom"))
        {
            mode = 1;
            index = 1;
        }
        else
        {
            mode = 0;

            this.seed = Integer.parseInt(args[2]);
            this.path = args[3];

            index = 4;
        }

        for (;index < args.length;)
        {
            if (args[index].equals("--path"))
            {
                if (path != null)
                    throw new IllegalArgumentException("Path cannot be redefined");

                if ((algorithm == 0) || (algorithm == 2))
                    throw new IllegalArgumentException("Path parameter must not be specified by this argument in this mode");

                path = args[index + 1];

                index += 2;
                continue;
            }

            if (args[index].equals("--output"))
            {
                if (outputPath != null)
                    throw new IllegalArgumentException("Output path cannot be redefined");

                if (algorithm == 2)
                    throw new IllegalArgumentException("Output path must not be specified by this argument in this mode");

                outputPath = args[index + 1];

                index += 2;
                continue;
            }

            if (args[index].equals("--seed"))
            {
                if (seed != -1)
                    throw new IllegalArgumentException("Seed cannot be redefined");

                if ((algorithm == 0) || (algorithm == 2))
                    throw new IllegalArgumentException("Seed parameter must not be specified by this argument in this mode");

                seed = Integer.parseInt(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--local-search-none"))
            {
                if (localSearch != -1)
                    throw new IllegalArgumentException("Local search cannot be redefined");

                localSearch = 0;

                index++;
                continue;
            }

            if (args[index].equals("--local-search-idsia"))
            {
                if (localSearch != -1)
                    throw new IllegalArgumentException("Local search cannot be redefined");

                localSearch = 1;

                index++;
                continue;
            }

            if (args[index].equals("--eas"))
            {
                if (algorithm != -1)
                    throw new IllegalArgumentException("Algorithm cannot be redefined");

                algorithm = 0;

                index++;
                continue;
            }

            if (args[index].equals("--ras"))
            {
                if (algorithm != -1)
                    throw new IllegalArgumentException("Algorithm cannot be redefined");

                algorithm = 1;

                index++;
                continue;
            }

            if (args[index].equals("--m"))
            {
                if (antNum != -1)
                    throw new IllegalArgumentException("Ants number (m) cannot be redefined");

                antNum = Integer.parseInt(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--rho"))
            {
                if (evaporationRemains >= 0.0)
                    throw new IllegalArgumentException("Evaporation remains factor (rho) cannot be redefined");

                evaporationRemains = Double.parseDouble(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--rounds-reinitialize"))
            {
                if (roundsToReinitialize != -1)
                    throw new IllegalArgumentException("Rounds reinitialize cannot be redefined");

                roundsToReinitialize = Integer.parseInt(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--q"))
            {
                if (probabilityBestInModification >= 0.0)
                    throw new IllegalArgumentException("Probability best (q) cannot be redefined");

                probabilityBestInModification = Double.parseDouble(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--k"))
            {
                if (selectionPower >= 0.0)
                    throw new IllegalArgumentException("Selection power (k) cannot be redefined");

                selectionPower = Double.parseDouble(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--Q"))
            {
                if (factorQ >= 0.0)
                    throw new IllegalArgumentException("Pheromone factor (Q) cannot be redefined");

                factorQ = Double.parseDouble(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--sigma"))
            {
                if (numberOfElitist != -1)
                    throw new IllegalArgumentException("Elitist ants number (sigma) cannot be redefined");

                if (algorithm != 0)
                    throw new IllegalArgumentException("Elitist ants number (sigma) can be only defined in case of EAS");

                numberOfElitist = Integer.parseInt(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].equals("--w"))
            {
                if (numberOfDepositing != -1)
                    throw new IllegalArgumentException("Number of depositing (w) cannot be redefined");

                if (algorithm != 1)
                    throw new IllegalArgumentException("Number of depositing (w) can be only defined in case of EAS");

                numberOfDepositing = Integer.parseInt(args[index + 1]);

                index += 2;
                continue;
            }

            if (args[index].startsWith("--end"))
                break;

            throw new IllegalArgumentException("Unknown parameter " + args[index]);
        }
    }
}
