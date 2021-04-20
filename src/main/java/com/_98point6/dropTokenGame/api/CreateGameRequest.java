package com._98point6.dropTokenGame.api;

import java.util.List;
import lombok.*;

/**
 *
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameRequest {
    private List<String> players;
    private Integer columns;
    private Integer rows;
}
