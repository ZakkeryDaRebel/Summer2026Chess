package chess.calculator;

import chess.*;

import java.util.Collection;

public class CastlingCalculator {

    private static final int QUEENSIDE = 0;
    private static final int KING = 1;
    private static final int KINGSIDE = 2;
    private static final int WHITESIDE = 0;
    private static final int BLACKSIDE = 1;

    public void canCastle(boolean[][] castlingPermissions, ChessBoard board, ChessPosition startPos,
                          Collection<ChessMove> validMoves) {
        ChessGame.TeamColor color = board.getPiece(startPos).getTeamColor();
        int side = color == ChessGame.TeamColor.WHITE ? WHITESIDE : BLACKSIDE;
        int row = startPos.getRow();

        if (!castlingPermissions[side][KING]) {
            return;
        }
        if (castlingPermissions[side][QUEENSIDE] && checkSpots(board, color, new ChessPosition(row, 2),
                new ChessPosition(row, 3), new ChessPosition(row, 4))) {
            validMoves.add(new ChessMove(startPos, new ChessPosition(row, 3), null));
        }
        if (castlingPermissions[side][KINGSIDE] && checkSpots(board, color, new ChessPosition(row, 6),
                new ChessPosition(row, 7))) {
            validMoves.add(new ChessMove(startPos, new ChessPosition(row, 7), null));
        }
    }

    public ChessMove isCastleMove(ChessMove move, boolean[][] castlingPermissions) {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        if (Math.abs(startPos.getColumn() - endPos.getColumn()) != 2 ||
            startPos.getRow() != endPos.getRow()) {
            return null;
        }

        int side = startPos.getRow() == 0 ? WHITESIDE : BLACKSIDE;
        castlingPermissions[side][1] = false;

        boolean castleQueen = startPos.getColumn() > endPos.getColumn();
        int startCol = castleQueen ? 1 : 8;
        int endCol = castleQueen ? 4 : 6;
        return new ChessMove(new ChessPosition(startPos.getRow(), startCol),
                new ChessPosition(startPos.getRow(), endCol), null);

    }

    public void loadBoard(ChessBoard board, boolean[][] castlingPermissions) {
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        ChessPiece pieceAOne = board.getPiece(new ChessPosition(1, 1));
        ChessPiece pieceEOne = board.getPiece(new ChessPosition(1, 5));
        ChessPiece pieceHOne = board.getPiece(new ChessPosition(1, 8));
        ChessPiece pieceAEight = board.getPiece(new ChessPosition(8, 1));
        ChessPiece pieceEEight = board.getPiece(new ChessPosition(8, 5));
        ChessPiece pieceHEight = board.getPiece(new ChessPosition(8, 8));

        castlingPermissions[WHITESIDE][QUEENSIDE] = (pieceAOne != null && pieceAOne.equals(whiteRook));
        castlingPermissions[WHITESIDE][KING] = (pieceEOne != null && pieceEOne.equals(whiteKing));
        castlingPermissions[WHITESIDE][KINGSIDE] = (pieceHOne != null && pieceHOne.equals(whiteRook));
        castlingPermissions[BLACKSIDE][QUEENSIDE] = (pieceAEight != null && pieceAEight.equals(blackRook));
        castlingPermissions[BLACKSIDE][KING] = (pieceEEight != null && pieceEEight.equals(blackKing));
        castlingPermissions[BLACKSIDE][KINGSIDE] = (pieceHEight != null && pieceHEight.equals(blackRook));
    }

    public void updateCastlingPermissions(ChessMove move, ChessBoard board, boolean[][] castlingPermissions) {
        ChessPiece checkPiece = board.getPiece(move.getEndPosition());
        if (checkPiece.getPieceType() == ChessPiece.PieceType.KING) {
            int pieceColor = checkPiece.getTeamColor() == ChessGame.TeamColor.WHITE ? 0 : 1;
            castlingPermissions[pieceColor][1] = false;
        }
        captureCorner(move, castlingPermissions);
    }

    private boolean checkSpots(ChessBoard board, ChessGame.TeamColor color, ChessPosition... spots) {
        for (ChessPosition pos : spots) {
            if (board.getPiece(pos) != null || AttackKingCalculator.canAttackKing(board, color, pos)) {
                return false;
            }
        }
        return true;
    }

    public void captureCorner(ChessMove move, boolean[][] castlingPermissions) {
        int[][] corners = {{1, 1}, {1, 8}, {8, 1}, {8, 8}};
        int[][] castlingLocation = {{WHITESIDE, QUEENSIDE}, {WHITESIDE, KINGSIDE},
                                    {BLACKSIDE, QUEENSIDE}, {BLACKSIDE, KINGSIDE}};
        for (int i = 0; i < corners.length; i++) {
            if (move.getEndPosition().equals(new ChessPosition(corners[i][0], corners[i][1]))) {
                castlingPermissions[castlingLocation[i][0]][castlingLocation[i][1]] = false;
            }
        }
    }
}
