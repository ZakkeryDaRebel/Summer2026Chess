package chess.calculator;

import chess.*;

import java.util.Collection;

public class AttackKingCalculator {

    private static final BoardLooper boardLooper = new BoardLooper();

    public static boolean canAttackKing(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition kingPos) {
        return boardLooper.findPosition(pos -> {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null && piece.getTeamColor() != teamColor) {
                Collection<ChessMove> pieceMoves = piece.pieceMoves(board, pos);
                for (ChessMove attack : pieceMoves) {
                    if (attack.getEndPosition().equals(kingPos)) {
                        return Boolean.TRUE;
                    }
                }
            }
            return null;
        }) == Boolean.TRUE;
    }
}
