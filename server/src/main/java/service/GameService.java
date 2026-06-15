package service;

import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(DAOFactory daoFactory) {
        this.authDAO = daoFactory.getAuthDAO();
        this.gameDAO = daoFactory.getGameDAO();
    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ResponseException {
        ServiceUtils.badRequestChecker("Bad Request: Please input a name for the game", request.gameName());
        ServiceUtils.validateAuth(authToken, this.authDAO);

        try {
            int gameID = this.gameDAO.createGame(request.gameName());
            return new CreateGameResponse(gameID);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Server Error: Failed to create the game" + e.getMessage());
        }
    }

    public ListGamesResponse listGames(String authToken) {
        return null;
    }

    public void joinGame(JoinGameRequest request, String authToken) {
        return;
    }
}
