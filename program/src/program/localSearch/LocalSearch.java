package program.localSearch;

import program.Solution;

/**
 * Created by Aldar on 12-May-18.
 */
public abstract class LocalSearch
{
    /**
     * Perform local search for a given solution
     * NOTE: it modifies the solution
     * @param solution
     * @throws Exception
     */
    public abstract void search(Solution solution) throws Exception;
}
