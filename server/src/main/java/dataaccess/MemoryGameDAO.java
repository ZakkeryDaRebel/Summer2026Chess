package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {

    private HashMap<Integer, GameData> gameDatabase;

    public MemoryGameDAO() {
        gameDatabase = new HashMap<>();
    }
}
