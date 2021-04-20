package com._98point6.dropTokenGame.manager;

import java.util.List;

import com._98point6.dropTokenGame.game.DropTokenGame;
import com._98point6.dropTokenGame.game.Move;
import com._98point6.dropTokenGame.api.CreateGameRequest;


public interface GameManager {
    String createNewGame(CreateGameRequest request);
    Boolean doesGameExist(String gameId);
    DropTokenGame getGame(String gameId);
    List<String> getInProgressGames();
    void quitGame(String gameId, String playerId);
    List<Move> getMoves(String gameId);
    void updateDropTokenGame(DropTokenGame dropTokenGame);
}
