package dataaccess;

import chess.ChessGame;
import model.GameData;
import java.util.ArrayList;

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
}
