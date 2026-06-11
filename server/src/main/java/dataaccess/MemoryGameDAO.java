package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> gameDatabase;

    public MemoryGameDAO() {
        this.gameDatabase = new ArrayList<>();
    }

    public int createGame(String gameName) {
        GameData game = new GameData(gameDatabase.size(), null, null, gameName, new ChessGame());
        this.gameDatabase.add(game);
        return game.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try {
            GameData game = this.gameDatabase.get(gameID);
            if (game == null) {
                throw new IndexOutOfBoundsException();
            }
            return game;
        } catch (IndexOutOfBoundsException ex) {
            throw new DataAccessException("Invalid gameID");
        }
    }

    public Collection<GameData> listGames() {
        return this.gameDatabase;
    }

    public void updateGame(GameData game) {
        this.gameDatabase.set(game.gameID(), game);
    }

    public void clearGames() {
        this.gameDatabase = new ArrayList<>();
    }
}
