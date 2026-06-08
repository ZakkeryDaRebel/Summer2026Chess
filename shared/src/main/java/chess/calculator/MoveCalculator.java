package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public interface MoveCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos);

    default boolean outOfBounds(ChessPosition pos) {
        return pos.getRow() < 1 || pos.getRow() > 8 || pos.getColumn() < 1 || pos.getColumn() > 8;
    }

//    default Collection<ChessMove> loopVerifier(ChessBoard board, ChessPosition pos, int[][] directions) {
//
//    }

    default Collection<ChessMove> spotVerifier(ChessBoard board, ChessPosition pos, int[][] directions) {
        Collection<ChessMove> pieceMoves = new ArrayList<>();
        for (int[] move : directions) {
            ChessPosition newPos = new ChessPosition(pos.getRow() + move[0], pos.getColumn() + move[1]);
            if (outOfBounds(newPos)) {
                continue;
            }
            if (board.getPiece(newPos) == null) {
                pieceMoves.add(new ChessMove(pos, newPos, null));
            } else if (board.getPiece(newPos).getTeamColor() != board.getPiece(pos).getTeamColor()) {
                pieceMoves.add(new ChessMove(pos, newPos, null));
            }
        }
        return pieceMoves;
    }


}
