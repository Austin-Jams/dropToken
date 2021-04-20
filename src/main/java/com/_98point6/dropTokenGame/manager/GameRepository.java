package com._98point6.dropTokenGame.manager;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com._98point6.dropTokenGame.game.DropTokenGame;


public interface GameRepository extends MongoRepository<DropTokenGame, String> {
    DropTokenGame findByGameId(String gameId);
    List<DropTokenGame> findAllByGameStatus(String gameStatus);
}
