package com._98point6.dropTokenGame.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com._98point6.dropTokenGame.game.DropTokenGame;
import com._98point6.dropTokenGame.game.Move;
import com._98point6.dropTokenGame.api.CreateGameRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class DefaultGameManager implements GameManager {
    
    GameRepository gameRepository;
    
    final int tokensNeededToWin = 4;

    public DefaultGameManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    

    @Override
    public String createNewGame(CreateGameRequest request) {
        final String gameId = UUID.randomUUID().toString().replace("-", "");
        DropTokenGame dropTokenGame = new DropTokenGame(gameId, request.getPlayers(), request.getColumns(), request.getRows(), tokensNeededToWin);
        updateDropTokenGame(dropTokenGame);
        return gameId;
    }
    
    @Override
    public Boolean doesGameExist(String gameId) {
        try {
            DropTokenGame dtg = gameRepository.findByGameId(gameId);
            return dtg != null;
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        return false;
    }
    
    @Override
    public DropTokenGame getGame(String gameId) {
        return gameRepository.findByGameId(gameId);
    }
    
    @Override
    public List<String> getInProgressGames() {
        List<DropTokenGame> inProgressGames;
        try {
            inProgressGames = gameRepository.findAllByGameStatus(DropTokenGame.GameStatus.IN_PROGRESS.name());
        } catch (Exception e) {
            log.error("Error looking up in progress games.");
            inProgressGames = new ArrayList<>();
        }
        return inProgressGames.stream().map(DropTokenGame::getGameId).collect(Collectors.toList());
    }
    
    //Remove Player
    //If not enough players to continue end the game
    @Override
    public void quitGame(String gameId, String playerId) {
        Move quitMove = new Move(playerId);
        DropTokenGame dropTokenGame = getGame(gameId);
        List<Move> moves = dropTokenGame.getMoves();
        moves.add(quitMove);
        dropTokenGame.setMoves(moves);
        List<String> players = dropTokenGame.getPlayers();
        players.remove(playerId);
        if (players.size() < 2) {
            dropTokenGame.setGameStatus(DropTokenGame.GameStatus.DONE.name());
        }
        updateDropTokenGame(dropTokenGame);
    }
    
    @Override
    public List<Move> getMoves(String gameId) {
        DropTokenGame dropTokenGame = getGame(gameId);
        return dropTokenGame.getMoves();
    }
    
    @Override
    public void updateDropTokenGame(DropTokenGame dropTokenGame) {
        try{
            gameRepository.save(dropTokenGame);
        } catch (Exception e) {
            log.error("DB error: " + e);
        }
        
    }
    
}
