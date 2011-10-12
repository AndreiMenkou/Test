package by.bsu.fpmi.menkov.ui;

import by.bsu.fpmi.menkov.*;
import java.util.regex.Matcher;

public class XBoard extends CommunicationProtocol {
	public void processCommand(String input) {
		if (input.equals("protover 2")) {
			Frittle
					.write("feature myname=\"dev_il "
							+ Frittle.version
							+ "\" setboard=1 analyze=0 variants=\"normal\" colors=0 debug=1 done=1");
		} else if (input.equals("resign")) {
			Frittle.getGame().resign();
		} else if (input.equals("go")) {
			// First check if the game is still on
			if (Frittle.getGame().isGameOver())
				this.error("Game Over");
			// If so, ask the AI to make a move
			else {
				Frittle.debug("dev_il is playing "
						+ (Frittle.getGame().getCurrentState()
								.getActivePlayer()));
				Frittle.getAI().go();
			}
		} else if (input.equals("force")) {
			Frittle.debug("dev_il is inactive");
			Frittle.getAI().destroyThreads();
			Frittle.getAI().forceMode = true;
		} else if (input.equals("undo")) {
			Frittle.getGame().undo();
			Frittle.debug("Reversed one move");
		} else if (input.equals("remove")) {
			Frittle.getGame().undo();
			Frittle.getGame().undo();
			Frittle.debug("Reversed two moves");
		} else if (input.startsWith("debug")) {
			Frittle.debugMode = input.substring(6).equals("on");
		} else if (input.equals("post")) {
			Frittle.getAI().showThinking = true;
			Frittle.debug("Thinking on");
		} else if (input.equals("nopost")) {
			Frittle.getAI().showThinking = false;
			Frittle.debug("Thinking off");
		} else if (input.equals("hard")) {
			Frittle.getAI().ponderMode = true;
			Frittle.debug("Pondering on");
		} else if (input.equals("easy")) {
			Frittle.getAI().ponderMode = false;
			Frittle.debug("Pondering off");
		} else if (input.startsWith("setboard")) {
			String FEN = input.substring(9); // After "setboard "
			try {
				Frittle.getAI().destroyThreads();
				GameState state = new GameState(FEN);
				Frittle.getGame().setState(state);
				Frittle.debug("OK");
			} catch (InvalidFENException e) {
				this.error(e.getMessage());
			}
		} else if (input.startsWith("sd")) {
			try {
				int depth = Integer.parseInt(input.substring(3)); // After "sd "
				Frittle.getAI().searchDepth = depth;
				Frittle.debug("OK");
			} catch (NumberFormatException e) {
				this.error("Invalid Search Depth");
			}
		} else if (input.startsWith("level")) {
			Matcher matcher = timeControlsPattern.matcher(input.substring(6));
			if (matcher.matches()) {
				try {
					// Get moves per session (0 if not tournament mode)
					int movesPerSession = Integer.parseInt(matcher.group(1));
					// Get base time in minutes
					long baseTime = Long.parseLong(matcher.group(2)) * 60 * 1000;
					// Add seconds component, if any
					if (matcher.group(3) != null) {
						baseTime += Long.parseLong(matcher.group(3)) * 1000;
					}
					// Get move increment from seconds
					long increment = Long.parseLong(matcher.group(4)) * 1000;
					// Set clock format on game
					if (movesPerSession > 0) // Tournament format
						Frittle.getGame().setClockFormat(
								new ClockFormat(movesPerSession, baseTime));
					else
						// Fischer format
						Frittle.getGame().setClockFormat(
								new ClockFormat(baseTime, increment));
					// Reset AI clock
					Frittle.getAI().clock.set(baseTime);
					Frittle.debug("OK");
				} catch (NumberFormatException e) {
				}
			}
		} else if (input.startsWith("time")) {
			try {
				// the time X commands sends X centiseconds
				long time = Long.parseLong(input.substring(5)) * 10;
				Frittle.getAI().clock.set(time);
			} catch (NumberFormatException e) {
			}
		} else if (input.equals("?")) {
			Frittle.getAI().moveNow();
		} else if (input.startsWith("perft")) {
			Perft p = new Perft(Frittle.getGame().getCurrentState());
			p.test(Integer.parseInt(input.substring(6)));
		} else if (coordinateMovePattern.matcher(input).matches())// Try to
																	// parse the
																	// move and
																	// apply it
		{
			if (Frittle.getGame().doMove(input)) {
				// The move was made, success!
				// If not in forceMode mode and the game is still on, let the AI
				// play the next move
				if (Frittle.getAI().forceMode == false
						&& Frittle.getGame().isGameOver() == false)
					Frittle.getAI().go();
			} else {
				// Illegal move
				Frittle.write("Illegal Move");
			}
		} else {
			// Do nothing if errornous command was made in XBoard mode
		}
	}

	public void AIMove(String moveStr) {
		Frittle.write("move " + moveStr);
	}

	public void showThinking(int depth, int sdepth, int score, int msec,
			int nodes, int evals, String pv) {
		Frittle.write(depth + " " + score + " " + (msec / 10) + " " + nodes
				+ " " + pv);
	}

	public void notify(String msg) {
		Frittle.write("telluser " + msg);
	}

	public void error(String err) {
		Frittle.write("tellusererror " + err);
	}
}
