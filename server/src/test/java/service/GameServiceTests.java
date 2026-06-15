package service;

import chess.ChessGame;
import dataaccess.DAOFactory;
import dataaccess.MemoryDAOFactory;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

import java.util.Iterator;

public class GameServiceTests {

    private GameService gameService;
    private UserService userService;
    private final String gameName = "Final Game!";

    @BeforeEach
    public void setUpServices() {
        DAOFactory daoFactory = new MemoryDAOFactory();
        this.gameService = new GameService(daoFactory);
        this.userService = new UserService(daoFactory);
    }

    @Test
    public void createGameSuccessful() {
        String authToken = TestUtils.createAuthUser(this.userService);
        CreateGameResponse response = this.gameService.createGame(new CreateGameRequest(this.gameName), authToken);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.gameID());
    }

    @Test
    public void createGameBadRequest() {
        String authToken = TestUtils.createAuthUser(this.userService);
        TestUtils.assertBadRequest(new CreateGameRequest[]{new CreateGameRequest(null)},
                request -> this.gameService.createGame(request, authToken));
    }

    @Test
    public void createGameUnauthorized() {
        String[] authTokens = {null, "Invalid authToken"};
        CreateGameRequest request = new CreateGameRequest(this.gameName);
        TestUtils.assertUnauthorized(authTokens, token -> this.gameService.createGame(request, token));
    }

    public void assertList(ListGamesResponse response) {
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.games());
    }

    @Test
    public void listNoGamesSuccessful() {
        String authToken = TestUtils.createAuthUser(this.userService);
        ListGamesResponse response = this.gameService.listGames(authToken);
        assertList(response);
        Assertions.assertTrue(response.games().isEmpty());
    }

    public void assertNewGame(GameData gameData, int gameID, String gameName) {
        Assertions.assertEquals(gameName, gameData.gameName());
        Assertions.assertNull(gameData.whiteUsername());
        Assertions.assertNull(gameData.blackUsername());
        Assertions.assertEquals(new ChessGame(), gameData.game());
        Assertions.assertEquals(gameID, gameData.gameID());
    }

    @Test
    public void listOneGameSuccessful() {
        String authToken = TestUtils.createAuthUser(this.userService);
        this.gameService.createGame(new CreateGameRequest(this.gameName), authToken);
        ListGamesResponse response = this.gameService.listGames(authToken);
        assertList(response);
        Assertions.assertEquals(1, response.games().size());
        assertNewGame(response.games().iterator().next(), 0, this.gameName);
    }

    @Test
    public void listManyGamesSuccessful() {
        String authToken = TestUtils.createAuthUser(this.userService);
        int numGames = 3;
        for (int i = 0; i < numGames; i++) {
            this.gameService.createGame(new CreateGameRequest("Game #" + i), authToken);
        }
        ListGamesResponse response = this.gameService.listGames(authToken);
        assertList(response);
        Assertions.assertEquals(numGames, response.games().size());
        Iterator<GameData> iterator = response.games().iterator();
        GameData game = iterator.next();
        int gameIter = 0;
        while (game != null) {
            assertNewGame(game, gameIter, "Game #" + gameIter++);
            if (iterator.hasNext()) {
                game = iterator.next();
            } else {
                game = null;
            }
        }
    }

    @Test
    public void listGamesUnauthorized() {
        String[] authTokens = {null, "Invalid authToken"};
        TestUtils.assertUnauthorized(authTokens, token -> this.gameService.listGames(token));
    }
}
