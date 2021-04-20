package com._98point6.dropTokenGame.api;

import java.util.List;

import com._98point6.dropTokenGame.game.Move;
import lombok.*;


/**
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameStatusResponse {
    
    private List<String> players;
    private List<Move> moves;
    private String winner;
    private String state;
}
