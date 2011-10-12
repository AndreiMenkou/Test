package by.bsu.fpmi.menkov.ui;

import java.util.regex.Pattern;

public abstract class CommunicationProtocol {

    abstract public void processCommand(String command);

    abstract public void AIMove(String moveStr);

    abstract public void showThinking(int depth, int sdepth, int score, int msec, int nodes, int evals, String pv);

    abstract public void notify(String msg);

    abstract public void error(String err);

    protected static Pattern coordinateMovePattern =
            Pattern.compile("([a-h][1-8])([a-h][1-8])([qrnbQRNB])?");

    protected static Pattern timeControlsPattern =
            Pattern.compile("(\\d+) (\\d+)(?:\\:(\\d{2}))? (\\d+)");
}
