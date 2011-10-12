package by.bsu.fpmi.menkov.ai;

public class SearchTimeout extends java.util.TimerTask
{
    /** Reference to the AI object that is searching */
    private AI ai;
    /** Reference to the the Search object that needs to be stopped */
    private Search search;

    public SearchTimeout(AI ai, Search search)
    {
        this.ai = ai;
        this.search = search;
    }

    /**
     * Executes the task
     */
    public void run()
    {
        // Tell the AI to move now ONLY if it's current search thread
        // is the same that this task is meant to stop
        if(ai.search == this.search)
        {
            ai.moveNow();
        }
    }
}
