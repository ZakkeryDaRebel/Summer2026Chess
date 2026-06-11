package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private HashMap<Integer, GameData> gameDatabase;
    private int nextGameID = 101;

    public MemoryGameDAO() {
        this.gameDatabase = new HashMap<>();
    }

    public int createGame(String gameName) {
        GameData game = new GameData(nextGameID++, null, null, gameName, new ChessGame());
        this.gameDatabase.put(game.gameID(), game);
        return game.gameID();
    }

    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = this.gameDatabase.get(gameID);
        if (game == null) {
            throw new DataAccessException("Invalid gameID");
        }
        return game;
    }

    public Collection<GameData> listGames() {
        return this.gameDatabase.values();
    }

    public void updateGame(GameData game) throws DataAccessException {
        this.gameDatabase.put(game.gameID(), game);
    }

    public void clearGames() {
        this.gameDatabase = new HashMap<>();
    }
}
