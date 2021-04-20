package com._98point6.dropTokenGame.api;

import java.util.List;

import lombok.*;


/**
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetGamesResponse {
    private List<String> games;
}
