package com._98point6.dropTokenGame.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com._98point6.dropTokenGame.game.DropTokenGame;
import com._98point6.dropTokenGame.game.Move;
import com._98point6.dropTokenGame.manager.GameManager;
import com._98point6.dropTokenGame.api.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class DropTokenController {
    
    GameManager gameManager;
    
    public DropTokenController(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    @GetMapping(value = "/drop_token")
    public ResponseEntity<GetGamesResponse> getAllInProgressGames() {
        List<String> games = gameManager.getInProgressGames();
        if (games.size() >= 1) {
            return ResponseEntity.ok(new GetGamesResponse(games));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping(value = "/drop_token")
    public ResponseEntity<CreateGameResponse> createNewGame(@RequestBody CreateGameRequest request) {
        if (request.getColumns() < 4 || request.getRows() < 4) {
            log.debug("Board too small.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (request.getPlayers().size() < 2) {
            log.debug("Need at least two players to play.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        //Check for duplicate players
        boolean isDuplicatePlayerId = false;
        Set<String> set = new HashSet<>();

        for (String playerId: request.getPlayers()) {
            if (!set.add(playerId)) {
                isDuplicatePlayerId = true;
            }
        }
        if (isDuplicatePlayerId) {
            log.debug("Duplicate playerId's are not allowed.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String gameId = gameManager.createNewGame(request);
        return ResponseEntity.ok(new CreateGameResponse(gameId));
    }
    
    @GetMapping(value = "/drop_token/{gameId}")
    public ResponseEntity<?> getGameStatus(@PathVariable String gameId) {
        //No game
        if (!gameManager.doesGameExist(gameId)) {
            log.debug("Game not found: " + gameId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DropTokenGame dropTokenGame = gameManager.getGame(gameId);
        //DRAW
        if (dropTokenGame.getWinner() == null && !dropTokenGame.getGameStatus().equalsIgnoreCase(DropTokenGame.GameStatus.DONE.name())) {
            return ResponseEntity.ok(new GameStatusDrawResponse(dropTokenGame.getPlayers(), dropTokenGame.getMoves(), dropTokenGame.getGameStatus()));
        }
        return ResponseEntity.ok(new GameStatusResponse(dropTokenGame.getPlayers(), dropTokenGame.getMoves(),
                dropTokenGame.getWinner(), dropTokenGame.getGameStatus()));
    }
    
    @PostMapping(value = "/drop_token/{gameId}/{playerId}")
    public ResponseEntity<PostMoveResponse> postMove(@PathVariable String gameId,
            @PathVariable("playerId") String playerId, @RequestBody PostMoveRequest req) {
        if (!gameManager.doesGameExist(gameId)) {
            log.debug("Game not Found: " + gameId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        DropTokenGame dropTokenGame = gameManager.getGame(gameId);
        //player not in game
        if (!dropTokenGame.getPlayers().contains(playerId)) {
            log.debug(playerId + " not in game " + gameId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //not players move
        if (!dropTokenGame.getPlayerAtBat().equalsIgnoreCase(playerId)) {
            log.debug(playerId + " is not up to move. Game: " + gameId);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        // invalid move
        if (!dropTokenGame.isValidMove(req.getColumn() - 1)) {
            log.debug("Move is not valid");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //Game is already over
        if (dropTokenGame.getGameStatus().equalsIgnoreCase(DropTokenGame.GameStatus.DONE.name())) {
            log.debug("Game is already completed. " + gameId);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        dropTokenGame.postMove(req.getColumn(), playerId);
        gameManager.updateDropTokenGame(dropTokenGame);
        return ResponseEntity.ok(new PostMoveResponse(gameId, dropTokenGame.getMoves().size()));
    }
    
    @DeleteMapping(value = "/drop_token/{gameId}/{playerId}")
    public ResponseEntity<String> playerQuit(@PathVariable String gameId, @PathVariable String playerId) {
        if (!gameManager.doesGameExist(gameId)) {
            log.debug("Game not Found: " + gameId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game not found");
        }
        DropTokenGame dropTokenGame = gameManager.getGame(gameId);
        //Player not in game
        if (!dropTokenGame.getPlayers().contains(playerId)) {
            log.debug(playerId + " is not in " + gameId + ".");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("%s is not a player in this game.", playerId));
        }
        //Game is Over
        if (dropTokenGame.getGameStatus().equalsIgnoreCase(DropTokenGame.GameStatus.DONE.name())) {
            log.debug(gameId + " is over.");
            return ResponseEntity.status(HttpStatus.GONE).body("Game is already over.");
        }
        log.debug(playerId + " has quit game" + gameId);
        gameManager.quitGame(gameId, playerId);
        return ResponseEntity.accepted().body(String.format("%s has quit the game.", playerId));
    }
    
    //Request Params refer to indexes not actual move number
    @GetMapping(value = "/drop_token/{gameId}/moves")
    public ResponseEntity<GetMovesResponse> getMoves(@PathVariable String gameId,
            @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "until", required = false)  Integer until) {
        if (!gameManager.doesGameExist(gameId)) {
            log.debug("Game not Found: " + gameId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Move> moves = gameManager.getMoves(gameId);
        // both optionals used
        if (start != null && until != null ) {
            if (until < 0 || start < 0 || until < start ||  until > moves.size() - 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            List<Move> movesSubset;
            if (until.equals(start)) {
                List<Move> move = new ArrayList<>();
                move.add(moves.get(start));
                movesSubset = move;
            } else {
                movesSubset = moves.subList(start, until);
            }
            List<GetMoveResponse> moveResponseList = movesSubset.stream()
                    .map(m -> GetMoveResponse.builder()
                            .column(m.getColumn()).player(m.getPlayer()).type(m.getMoveType()).build()).collect(
                    Collectors.toList());
            return ResponseEntity.ok(new GetMovesResponse(moveResponseList));
        } else if (start != null) {
            //just start used
            until = moves.size() - 1;
            if (start < 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            List<Move> movesSubset = moves.subList(start, until);
            List<GetMoveResponse> moveResponseList = mapMoveListToMoveResponseList(movesSubset);
            return ResponseEntity.ok(new GetMovesResponse(moveResponseList));
        } else if (until != null) {
            //just until used
            if (until > moves.size() - 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            List<Move> movesSubset = moves.subList(0,until);
            List<GetMoveResponse> moveResponseList = mapMoveListToMoveResponseList(movesSubset);
            return ResponseEntity.ok(new GetMovesResponse(moveResponseList));
        } else {
            //no optional params
            return ResponseEntity.ok(new GetMovesResponse(mapMoveListToMoveResponseList(moves)));
        }
    }
    
    @GetMapping(value = "/drop_token/{gameId}/moves/{moveNumber}")
    public ResponseEntity<GetMoveResponse> getMove(@PathVariable String gameId, @PathVariable("moveNumber") Integer moveNumber) {
        if (!gameManager.doesGameExist(gameId)) {
            log.debug(gameId + " not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Move> moves = gameManager.getMoves(gameId);
        //Move has not happened
        if (moves.size() - 1 < moveNumber) {
            log.debug(moveNumber + " has not been played in " + gameId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Move move = moves.get(moveNumber - 1);
        return ResponseEntity.ok(GetMoveResponse.builder().
                type(move.getMoveType())
                .player(move.getPlayer())
                .column(move.getColumn())
                .build());
    }
    
    private List<GetMoveResponse> mapMoveListToMoveResponseList(List<Move> moves) {
        return moves.stream()
                .map(m -> GetMoveResponse.builder()
                        .column(m.getColumn()).player(m.getPlayer()).type(m.getMoveType()).build()).collect(
                        Collectors.toList());
    }
}
