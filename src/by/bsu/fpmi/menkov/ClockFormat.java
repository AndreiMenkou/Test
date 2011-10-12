package by.bsu.fpmi.menkov;

/**
 * A chess clock can be in either of the following three formats:
 *
 * (1) Tournament time - X moves in Y seconds. After every X moves, Y seconds
 * are added to the clock.
 * (2) Fischer time - Y seconds initially, plus Z seconds added after each move.
 * (3) Fixed move time - Z seconds per move, no time carried forward to next move.
 *
 * Frittle currently supports formats (1) and (2).
 */
public class ClockFormat
{
    /**
     * Number of moves per session (X in tournament time). If this value is 0
     * it means that the whole game should be played in base time.
     */
    private int movesPerSession;

    /**
     * Base time. In tournament mode this amount of time is added after the
     * session is complete. In Fischer mode, this is the base time to which
     * increments are made and the whole game is to be played.
     *
     * The value is in milliseconds
     */
    private long baseTime;

    /**
     * Time increment after each move. In tournament mode this is 0. For fischer
     * mode it is the increment of time to the clock of each move.
     */
    private long increment;

    /**
     * Tournament mode constructor.
     *
     * @param movesPerSession   number of moves per session
     * @param baseTime          amount of time in a session (in milliseconds)
     */
    public ClockFormat(int movesPerSession, long baseTime)
    {
        this.movesPerSession = movesPerSession;
        this.baseTime = baseTime;
        this.increment = 0;
    }
    /**
     * Fischer mode constructor.
     *
     * @param baseTime       the amount of time to set at the start (in milliseconds)
     * @param increment      the amount of time added after each move (in milliseconds)
     */
    public ClockFormat(long baseTime, long increment)
    {
        this.movesPerSession = 0;
        this.baseTime = baseTime;
        this.increment = increment;
    }

    /**
     * Number of moves per session (X in tournament time). If this value is 0
     * it means that the whole game should be played in base time.
     *
     * @return the number moves per session
     */
    public int getMovesPerSession() {
        return movesPerSession;
    }

    /**
     * Base time. In tournament mode this amount of time is added after the
     * session is complete. In Fischer mode, this is the base time to which
     * increments are made and the whole game is to be played.
     * 
     * @return the base time in milliseconds
     */
    public long getBaseTime() {
        return baseTime;
    }

    /**
     * Time increment after each move. In tournament mode this is 0. For fischer
     * mode it is the increment of time to the clock of each move.
     *
     * @return the increment in milliseconds
     */
    public long getIncrement() {
        return increment;
    }

    /**
     * Textual phrase for this clock format.
     *
     * @return  String representation of the clock format
     */
    public String toString()
    {
        if(movesPerSession > 0)
        {
            return movesPerSession + " moves in " + baseTime/1000 + " seconds ";
        }
        else
        {
            return baseTime/1000 + " seconds initially, plus " + increment/1000
                    + " seconds per move";
        }
    }

}
