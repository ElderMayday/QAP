package program;

import java.util.Comparator;

/**
 * Created by Aldar on 14-May-18.
 */
public class SolutionComparator implements Comparator<Solution>
{
    @Override
    public int compare(Solution o1, Solution o2)
    {
        if (o1.objective < o2.objective)
            return -1;
        else
        {
            if (o2.objective < o1.objective)
                return 1;
            else
                return 0;
        }
    }
}
