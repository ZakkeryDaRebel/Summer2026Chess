package chess;

import chess.calculator.AttackKingCalculator;
import chess.calculator.CastlingCalculator;
import chess.calculator.PawnCalculator;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final boolean[][] castlingPermissions = {{true, true, true}, {true, true, true}};
    private ChessMove lastMove;
    private final PawnCalculator pawnCalc;
    private final CastlingCalculator castleCalc;
    private final BoardLooper boardLooper;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        this.gameBoard.resetBoard();
        this.gameOver = false;
        this.lastMove = null;
        this.pawnCalc = new PawnCalculator();
        this.castleCalc = new CastlingCalculator();
        this.boardLooper = new BoardLooper();
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
        if (startPosition.getRow() < 1 || startPosition.getRow() > 8 ||
                startPosition.getColumn() < 1 || startPosition.getColumn() > 8) {
            return null;
        }

        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = this.gameBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> pieceMoves = piece.pieceMoves(this.gameBoard, startPosition);
        for (ChessMove move : pieceMoves) {
            try {
                ChessBoard originalBoard = (ChessBoard) this.gameBoard.clone();
                executeMove(move, null);
                if (!isInCheck(piece.getTeamColor())) {
                    validMoves.add(move);
                }
                this.gameBoard = originalBoard;
            } catch (CloneNotSupportedException e) {
                return null;
            }

        }
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            pawnCalc.canEnPassant(lastMove, this.gameBoard, startPosition, validMoves);
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !isInCheck(piece.getTeamColor())) {
            castleCalc.canCastle(castlingPermissions, this.gameBoard, startPosition, validMoves);
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
        ChessPosition startPos = move.getStartPosition();
        ChessPiece checkPiece = this.gameBoard.getPiece(startPos);
        if (checkPiece == null || this.turn != checkPiece.getTeamColor() || this.gameOver ||
                !validMoves(startPos).contains(move)) {
            throw new InvalidMoveException();
        }
        executeMove(move, updateSecondSpot(move));
        this.lastMove = move;
        castleCalc.updateCastlingPermissions(move, this.gameBoard, castlingPermissions);
        swapTeamTurn();
        if (isInCheckmate(this.turn) || isInStalemate(this.turn)) {
            this.gameOver = true;
        }
    }

    public void executeMove(ChessMove move, ChessMove secondPiece) {
        ChessPiece oldPiece = this.gameBoard.getPiece(move.getStartPosition());
        ChessPiece.PieceType newType = move.getPromotionPiece() != null ?
                                        move.getPromotionPiece() : oldPiece.getPieceType();
        ChessPiece newPiece = new ChessPiece(oldPiece.getTeamColor(), newType);

        this.gameBoard.addPiece(move.getStartPosition(), null);
        this.gameBoard.addPiece(move.getEndPosition(), newPiece);
        if (secondPiece != null) {
            if (secondPiece.getEndPosition() != null) {
                ChessPiece otherPiece = this.gameBoard.getPiece(secondPiece.getStartPosition());
                this.gameBoard.addPiece(secondPiece.getEndPosition(), otherPiece);
            }
            this.gameBoard.addPiece(secondPiece.getStartPosition(), null);
        }
    }

    public ChessMove updateSecondSpot(ChessMove move) {
        ChessPiece.PieceType type = this.gameBoard.getPiece(move.getStartPosition()).getPieceType();

        return switch (type) {
            case PAWN ->  pawnCalc.isEnPassant(move, this.gameBoard);
            case KING -> castleCalc.isCastleMove(move, castlingPermissions);
            default -> null;
        };
    }

    private ChessPosition findKing(TeamColor color) {
        return this.boardLooper.findPosition(pos -> {
            ChessPiece piece = this.gameBoard.getPiece(pos);
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                return pos;
            }
            return null;
        });
    }

    private boolean noValidMoves(TeamColor color) {
        return this.boardLooper.findPosition(pos -> {
            ChessPiece checkPiece = this.gameBoard.getPiece(pos);
            if (checkPiece != null && checkPiece.getTeamColor() == color && !validMoves(pos).isEmpty()) {
                return Boolean.FALSE;
            }
            return null;
        }) == null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return AttackKingCalculator.canAttackKing(this.gameBoard, teamColor, findKing(teamColor));
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
        castleCalc.loadBoard(board, castlingPermissions);
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
        return gameOver == chessGame.gameOver && turn == chessGame.turn &&
                Objects.equals(gameBoard, chessGame.gameBoard) && Objects.equals(lastMove, chessGame.lastMove) &&
                Objects.deepEquals(castlingPermissions, chessGame.castlingPermissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, gameBoard, gameOver, Arrays.deepHashCode(castlingPermissions), lastMove);
    }
}
