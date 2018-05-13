package program;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * An instance of a QAP problem
 * Created by Aldar on 07-May-18.
 */
public class Problem
{
    public final int size;
    public final int[][] distance;  // a.k.a. a
    public final int[][] flow;      // a.k.a. b


    /**
     * Loads a problem instance from a file
     * @param file
     * @throws FileNotFoundException
     */
    public Problem(File file) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(file);

        size = scanner.nextInt();

        distance = new int[size][size];
        flow = new int[size][size];

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                distance[i][j] = scanner.nextInt();

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                flow[i][j] = scanner.nextInt();

        scanner.close();
    }
}
