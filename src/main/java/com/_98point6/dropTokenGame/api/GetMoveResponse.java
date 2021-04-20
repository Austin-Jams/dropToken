package com._98point6.dropTokenGame.api;

import lombok.*;


@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMoveResponse {
    private String type;
    private String player;
    private Integer column;
}
