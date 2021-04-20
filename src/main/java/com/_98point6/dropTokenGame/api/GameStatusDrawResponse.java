package com._98point6.dropTokenGame.api;

import java.util.List;

import com._98point6.dropTokenGame.game.Move;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GameStatusDrawResponse {
    private List<String> players;
    private List<Move> moves;
    private String state;
}
