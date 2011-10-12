package by.bsu.fpmi.menkov;

import java.util.Random;

public class Zobrist {
	
	private static long bitStrings[];
	
	static {
		bitStrings = new long [907];
		Random random = new Random();
		for(int i = 0; i < 907; i++) {
			bitStrings[i] = random.nextLong();
		}
	}
	
	public static long hash(GameState state) {
		long hash = 0;
		// First the board position
		// There are 832 bitStrings for this part
		// 13 different ways each square can be
		Piece p = null;
		int i, j = 0, k;
		for(i = 0; i < 64; i++) {
			p = state.getBoard()[i];
			if(p == null) {
				j = 0;
			} else {
				switch(p.getPlayer()) {
					case WHITE:
						switch(p.getType()) {
							case PAWN:
								j = 1;
							break;
							case KNIGHT:
								j = 2;
							break;
							case BISHOP:
								j = 3;
							break;
							case ROOK:
								j = 4;
							break;
							case QUEEN:
								j = 5;
							break;
							case KING:
								j = 6;
							break;
						}
					break;
					case BLACK:						
						switch(p.getType()) {
							case PAWN:
								j = 7;
							break;
							case KNIGHT:
								j = 8;
							break;
							case BISHOP:
								j = 9;
							break;
							case ROOK:
								j = 10;
							break;
							case QUEEN:
								j = 11;
							break;
							case KING:
								j = 12;
							break;
						}
					break;
				}
			}
			k = i * 13 + j;
			// Now XOR the corresponding bitString to the hash
			hash = hash ^ bitStrings[k];
		}
		if (state.getActivePlayer() == Player.WHITE) {
			hash = hash ^ bitStrings[832];
		} else {
			hash = hash ^ bitStrings[833];
		}
		if (state.canCastleWhiteKingside()) {
			hash = hash ^ bitStrings[834];
		} else {
			hash = hash ^ bitStrings[835];
		}
		if (state.canCastleBlackKingside()) {
			hash = hash ^ bitStrings[836];
		} else {
			hash = hash ^ bitStrings[837];
		}
		if (state.canCastleWhiteQueenside()) {
			hash = hash ^ bitStrings[838];
		} else {
			hash = hash ^ bitStrings[839];
		}
		if (state.canCastleBlackQueenside()) {
			hash = hash ^ bitStrings[840];
		} else {
			hash = hash ^ bitStrings[841];
		}
		if (state.getEnPassant() == -1) {
			hash = hash ^ bitStrings[842];
		} else {
			hash = hash ^ bitStrings[843+state.getEnPassant()];
		}
		return hash;
	}
}