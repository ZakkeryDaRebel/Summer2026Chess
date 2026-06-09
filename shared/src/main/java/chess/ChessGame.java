package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard gameBoard;
    private boolean gameOver;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        this.gameBoard.resetBoard();
        this.gameOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    private void swapTeamTurn() {
        this.turn = this.turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = this.gameBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> pieceMoves = piece.pieceMoves(this.gameBoard, startPosition);
        for (ChessMove move : pieceMoves) {
            try {
                ChessBoard originalBoard = (ChessBoard) this.gameBoard.clone();
                executeMove(move);
                if (!isInCheck(piece.getTeamColor())) {
                    validMoves.add(move);
                }
                this.gameBoard = originalBoard;
            } catch (CloneNotSupportedException e) {
                return null;
            }

        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (this.gameBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException();
        }
        if (this.turn != this.gameBoard.getPiece(move.getStartPosition()).getTeamColor()) {
            throw new InvalidMoveException();
        }
        if (this.gameOver) {
            throw new InvalidMoveException();
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }
        executeMove(move);
        swapTeamTurn();
        if (isInCheckmate(this.turn) || isInStalemate(this.turn)) {
            this.gameOver = true;
        }
    }

    public void executeMove(ChessMove move) {
        ChessPiece oldPiece = this.gameBoard.getPiece(move.getStartPosition());
        ChessPiece newPiece = new ChessPiece(oldPiece.getTeamColor(), move.getPromotionPiece() != null ?
                move.getPromotionPiece() : oldPiece.getPieceType());
        this.gameBoard.addPiece(move.getStartPosition(), null);
        this.gameBoard.addPiece(move.getEndPosition(), newPiece);
    }

    private ChessPosition findKing(TeamColor color) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition checkPos = new ChessPosition(row, col);
                ChessPiece piece = this.gameBoard.getPiece(checkPos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    return checkPos;
                }
            }
        }
        return null;
    }

    private boolean noValidMoves(TeamColor color) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition checkPos = new ChessPosition(row, col);
                ChessPiece checkPiece = this.gameBoard.getPiece(checkPos);
                if (checkPiece != null && checkPiece.getTeamColor() == color && !validMoves(checkPos).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition checkPos = new ChessPosition(row, col);
                ChessPiece checkPiece = this.gameBoard.getPiece(checkPos);
                if (checkPiece == null) {
                    continue;
                }
                Collection<ChessMove> pieceMoves = checkPiece.pieceMoves(this.gameBoard, checkPos);
                for (ChessMove move : pieceMoves) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && noValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return gameOver == chessGame.gameOver && turn == chessGame.turn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, gameBoard, gameOver);
    }
}
