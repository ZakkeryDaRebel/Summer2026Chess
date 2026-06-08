package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public class QueenCalculator implements MoveCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> piecesMoves = new RookCalculator().pieceMoves(board, pos);
        piecesMoves.addAll(new BishopCalculator().pieceMoves(board, pos));
        return piecesMoves;
    }
}
