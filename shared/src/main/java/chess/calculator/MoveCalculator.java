package chess.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface MoveCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos);

    default boolean outOfBounds(ChessPosition pos) {
        return pos.getRow() < 1 || pos.getRow() > 8 || pos.getColumn() < 1 || pos.getColumn() > 8;
    }

    default boolean isEnemy(ChessPiece startPiece, ChessPiece endPiece) {
        return startPiece.getTeamColor() != endPiece.getTeamColor();
    }

    default Collection<ChessMove> loopVerifier(ChessBoard board, ChessPosition pos, int[][] directions,
                                               boolean onlyOneSquare) {
        Collection<ChessMove> pieceMoves = new ArrayList<>();
        for (int[] move : directions) {
            ChessPosition newPos = new ChessPosition(pos.getRow() + move[0], pos.getColumn() + move[1]);
            boolean canKeepMoving = true;

            while(!outOfBounds(newPos) && canKeepMoving) {
                canKeepMoving = checkSpot(board, pos, newPos, pieceMoves) && !onlyOneSquare;
                newPos = new ChessPosition(newPos.getRow() + move[0], newPos.getColumn() + move[1]);
            }
        }
        return pieceMoves;
    }

    default boolean checkSpot(ChessBoard board, ChessPosition startPos, ChessPosition newPos,
                              Collection<ChessMove> pieceMoves) {
        ChessPiece capturedPiece = board.getPiece(newPos);
        if (capturedPiece == null) {
            pieceMoves.add(new ChessMove(startPos, newPos, null));
            return true;
        }
        if (isEnemy(board.getPiece(startPos), capturedPiece)) {
            pieceMoves.add(new ChessMove(startPos, newPos, null));
        }
        return false;
    }
}
