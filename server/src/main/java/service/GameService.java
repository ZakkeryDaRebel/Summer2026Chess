package service;

import dataaccess.AuthDAO;
import dataaccess.DAOFactory;
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
        ServiceUtils.validateAuth(authToken, this.authDAO);
        return null;
    }

    public ListGamesResponse listGames(String authToken) {
        return null;
    }

    public void joinGame(JoinGameRequest request, String authToken) {
        return;
    }
}
