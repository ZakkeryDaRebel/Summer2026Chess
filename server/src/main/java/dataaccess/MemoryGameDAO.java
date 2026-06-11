package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> gameDatabase;

    public MemoryGameDAO() {
        this.gameDatabase = new ArrayList<>();
    }

    public int createGame(String gameName) throws DataAccessException {
        int gameID = this.gameDatabase.size();
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        if (!this.gameDatabase.add(game)) {
            throw new DataAccessException("Faileed to create game");
        }
        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try {
            GameData game = this.gameDatabase.get(gameID);
            if (game == null) {
                throw new DataAccessException("Invalid gameID");
            }
            return game;
        } catch (IndexOutOfBoundsException ex) {
            throw new DataAccessException("Invalid gameID");
        }
    }

    public Collection<GameData> listGames() {
        return this.gameDatabase;
    }

    public void clearGames() {
        this.gameDatabase = new ArrayList<>();
    }
}
