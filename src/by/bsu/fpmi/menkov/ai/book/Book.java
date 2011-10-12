package by.bsu.fpmi.menkov.ai.book;

import by.bsu.fpmi.menkov.GameState;
import by.bsu.fpmi.menkov.InvalidFENException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Vector;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Book
{
	private Hashtable<Long,Vector<BookMove>> hashtable;
    
    Pattern weightedMovePattern = Pattern.compile("([a-h][1-8])([a-h][1-8])([qrnbQRNB])?\\{(\\d+)\\}");

    public Book(String filename) throws IOException, BookFormatException
    {
        hashtable = new Hashtable<Long,Vector<BookMove>>(120000);
        InputStream bookStream = getClass().getResourceAsStream(filename);
        if(bookStream == null)
            throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new InputStreamReader(bookStream));
        for(int line = 1; br.ready(); line+=2)
        {
            String FEN = br.readLine();
            if(FEN.equals("#END#"))
            {
                break;
            }

            GameState state;
            try
            {
                state = new GameState(FEN);
            }
            catch(InvalidFENException e)
            {
                throw new BookFormatException(e.getMessage(), line);
            }

            String moves = br.readLine();
            Vector<BookMove> weightedMoves = new Vector<BookMove>();
            Matcher matcher = weightedMovePattern.matcher(moves);
            // Try to search for moves such as e2e4{1234}
            while(matcher.find())
            {
                String moveStr;
                if(matcher.group(3) != null) // with promotion
                    moveStr = matcher.group().substring(0, 5);
                else                        // no promotion
                    moveStr = matcher.group().substring(0,4);
                // Get the weight
                int weight = Integer.parseInt(matcher.group(4));
                if(weight == 0) // Bad move
                    continue;
                BookMove bookMove = new BookMove(moveStr, weight);
                weightedMoves.addElement(bookMove);
            }

            hashtable.put(new Long(state.hash()), weightedMoves);
        }
        br.close();
    }

    public String getMoveFrom(GameState state)
    {
        Vector<BookMove> weightedMoves = hashtable.get(new Long(state.hash()));
        if(weightedMoves == null || weightedMoves.size() == 0)
            return null;

        int sumWeights = 0;
        for(int i = 0; i < weightedMoves.size(); i++) {
            sumWeights += weightedMoves.elementAt(i).weight;
        }
        int random = (int)(Math.random() * sumWeights);
        int x = 0;
        for(int i=0; i<weightedMoves.size(); i++)
        {
            if(random < (x+weightedMoves.elementAt(i).weight))
                return weightedMoves.elementAt(i).moveStr;
            else
                x = x + weightedMoves.elementAt(i).weight;
        }
        throw new RuntimeException("Unexpected Error by book randomizer");
    }

    public int size()
    {
        return hashtable.size();
    }
}

class BookMove
{
    public int weight;
    public String moveStr;

    public BookMove(String  moveStr, int weight)
    {
        this.moveStr = moveStr;
        this.weight = weight;
    }
}