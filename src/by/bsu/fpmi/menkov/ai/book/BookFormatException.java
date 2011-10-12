package by.bsu.fpmi.menkov.ai.book;

public class BookFormatException extends Exception
{
    public BookFormatException(String err, int line)
    {
        super(err + " on line " + line);
    }
}
