package com._98point6.dropTokenGame.game;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "move")
public class Move {
    
    public enum MoveType {
        QUIT,
        MOVE
    }
    
    private String moveType;
    private String player;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer column;
    
    public Move(String player, Integer column) {
        this.moveType = MoveType.MOVE.name();
        this.player = player;
        this.column = column;
    }
    
    public Move(String player) {
        this.moveType = MoveType.QUIT.name();
        this.player = player;
    }
}
