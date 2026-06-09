package chess.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalculator implements MoveCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> pieceMoves = new ArrayList<>();

        ChessPiece pawn = board.getPiece(pos);
        boolean isWhite = pawn.getTeamColor() == ChessGame.TeamColor.WHITE;
        int promotionRow = isWhite ? 8 : 1;
        int startRow = isWhite ? 2 : 7;
        int forward = isWhite ? 1 : -1;

        checkSpot(board, pos, new ChessPosition(pos.getRow() + forward, pos.getColumn()),
                pieceMoves, false, promotionRow);

        if (pieceMoves.size() == 1 && pos.getRow() == startRow) {
            checkSpot(board, pos, new ChessPosition(pos.getRow() + (forward * 2), pos.getColumn()),
                    pieceMoves, false, promotionRow);
        }

        int[][] diagonals = {{forward, 1}, {forward, -1}};
        for (int[] move : diagonals) {
            checkSpot(board, pos, new ChessPosition(pos.getRow() + move[0], pos.getColumn() + move[1]),
                    pieceMoves, true, promotionRow);
        }

        return pieceMoves;
    }

    public void checkSpot(ChessBoard board, ChessPosition startPos, ChessPosition newPos,
                             Collection<ChessMove> pieceMoves, boolean isDiagonal, int promotionRow) {
        if (outOfBounds(newPos)) {
            return;
        }
        ChessPiece capturedPiece = board.getPiece(newPos);
        if (capturedPiece == null && !isDiagonal) {
            addPromotion(pieceMoves, startPos, newPos, promotionRow);
        } else if (capturedPiece != null && isDiagonal && isEnemy(board.getPiece(startPos), capturedPiece)) {
            addPromotion(pieceMoves, startPos, newPos, promotionRow);
        }
    }

    public void addPromotion(Collection<ChessMove> pieceMoves, ChessPosition startPos, ChessPosition endPos,
                             int promotionRow) {
        if (endPos.getRow() == promotionRow) {
            ChessPiece.PieceType[] promotions = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP,
                    ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
            for (ChessPiece.PieceType promotion : promotions) {
                pieceMoves.add(new ChessMove(startPos, endPos, promotion));
            }
        } else {
            pieceMoves.add(new ChessMove(startPos, endPos, null));
        }
    }

    public void canEnPassant(ChessMove lastMove, ChessBoard board, ChessPosition piecePos, Collection<ChessMove> validMoves) {
        if (lastMove == null) {
            return;
        }
        ChessPosition startPos = lastMove.getStartPosition();
        ChessPosition endPos = lastMove.getEndPosition();
        if (board.getPiece(endPos).getPieceType() != ChessPiece.PieceType.PAWN) {
            return;
        }
        if (Math.abs(startPos.getRow() - endPos.getRow()) == 2 && piecePos.getRow() == endPos.getRow() &&
            Math.abs(endPos.getColumn() - piecePos.getColumn()) == 1) {
            int forward = board.getPiece(endPos).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
            validMoves.add(new ChessMove(piecePos,
                    new ChessPosition(piecePos.getRow() + forward, endPos.getColumn()), null));
        }
    }
}
