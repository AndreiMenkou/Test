package by.bsu.fpmi.menkov;

import java.util.Stack;

/**
 * Game is an active chess game in play.
 * This class contains information about the current GameState
 * and handles logic of the game such as winning, losing, draws, etc.
 * and also provides communication between the Players to make
 * moves and offer draws, resign, etc.
 */
public class Game
{
    /** The current state of the game */
    private GameState currentState;

    /** A list of legal moves from the current position */
    private MoveList legalMoves;

	/** A stack of moves (plies) of the history of the game */
	private Stack<Move> moveStack;

    /** The number of half moves (plies) since the last pawn move or capture */
    private int reversiblePliesCount;

    /** The move number (initially 1, then increments after each of black's play) */
    private int moveNumber;

	/** Status of the game. Whether it is over or ongoing. */
	private boolean gameOver;

    /** The time controls for this game */
    private ClockFormat clockFormat;

	/**
	 * Creates a new chess Game from the default position.
	 */
	public Game()
	{
        setState(new GameState());
	}

    public void setState(GameState state)
    {
		currentState = state;
        legalMoves = currentState.generateMoves();
		moveStack = new Stack<Move>();
        reversiblePliesCount = 0;
        moveNumber = 1;
		gameOver = false;
        // By default set clock format to 40 moves in 20 minutes
        clockFormat = new ClockFormat(40,(long)(20*60*1000));
    }

	public GameState getCurrentState()
	{
		return this.currentState;
	}

    public ClockFormat getClockFormat()
    {
        return clockFormat;
    }

    public void setClockFormat(ClockFormat clockFormat)
    {
        this.clockFormat = clockFormat;
    }

	public MoveList getLegalMoves()
	{
		return this.gameOver ? new MoveList() : this.legalMoves;
	}

    public String getMoveHistoryString()
    {
        StringBuffer sb = new StringBuffer();
        Move move;
        int moveNum=0, i=0;
        for(i=0; i<moveStack.size(); i++)
        {
            move = moveStack.elementAt(i);
            if(i % 2 == 0)
            {
                ++moveNum;
                sb.append(moveNum + ". ");
            }
            sb.append(move.toString() + " ");
        }
        return sb.toString();
    }

    Stack<Move> getMoveStack()
    {
        return this.moveStack;
    }

     public int getReversiblePliesCount()
     {
         return this.reversiblePliesCount;
     }
     public int getMoveNumber()
     {
         return this.moveNumber;
     }

	public boolean doMove(String notation) {
		if (this.gameOver) {
            return false;
		}

        for(Move move : legalMoves) {
            if(move.toString().equals(notation)) {
                this.moveStack.push(move);
                currentState.doMove(move);
                legalMoves = currentState.generateMoves();
                if(currentState.getActivePlayer() == Player.WHITE) {
                    moveNumber++;
                }
                if(move.capturedPiece == null && currentState.getBoard()[move.dest].getType() != PieceType.PAWN)
                {
                    reversiblePliesCount++;
                    // @todo change this into claimDraw() and offerDraw()
                    // If 50 such moves (100 plies) have occurred, its a draw
                    if(reversiblePliesCount >= 100) {
                        declareResult("1/2-1/2 {50-move rule}");
                    }
                }
                else
                {
                    reversiblePliesCount = 0;
                }
                if(legalMoves.isEmpty()) {
                    if(currentState.isInCheck(currentState.getActivePlayer())) {
                        if(currentState.getActivePlayer() == Player.WHITE) {
                            declareResult("0-1 {Black mates}");
                        }
                        else {
                            declareResult("1-0 {White mates}");
                        }
                    }
                    else {
                        declareResult("1/2-1/2 {Stalemate}");
                    }
                }
                return true;
            }
        }
        return false;
	}

	/**
	 * Undoes the previous ply.
	 */
	public void undo()
	{
		if(this.moveStack.size() > 0)
			currentState.undoMove(this.moveStack.pop());
        legalMoves = currentState.generateMoves();
        this.gameOver = false; // Undo means you wanna play again
	}

	/**
	 * Called when the active player resigns and the opponent wins the game.
	 */
	public void resign()
	{
		if(getCurrentState().getActivePlayer() == Player.WHITE)
			declareResult("0-1 {White resigns}");
		else
			declareResult("1-0 {Black resigns}");
	}

	/**
	 * Declares the result of the game to the output.
	 * Note: This does not delete the object and therefore the
	 * interface should make necessary arrangements to end the game
	 * and dissalow further moves.
	 */
	private void declareResult(String r)
	{
		this.gameOver = true;
		Frittle.write(r);
	}

    /**
     * Checks if the game's result has been declared already
     *
     * @return <code>true</code> if the game is over, <code>false</code> if the
     * game is still on
     */
    public boolean isGameOver() {
        return gameOver;
    }
}