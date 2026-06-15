package service;

import dataaccess.DAOFactory;
import dataaccess.MemoryDAOFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import response.CreateGameResponse;

public class GameServiceTests {

    private GameService gameService;
    private UserService userService;
    private String gameName = "Final Game!";

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
        for (String token : authTokens) {
            TestUtils.assertUnauthorized(new CreateGameRequest[]{new CreateGameRequest(this.gameName)},
                    request -> this.gameService.createGame(request, token));
        }
    }
}
