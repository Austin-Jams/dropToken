package com._98point6.dropTokenGame.api;


import lombok.*;


/**
 *
 */
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostMoveResponse {
    private String moveLink;
    
    //format to response
    public PostMoveResponse(String gameId, int moveNumber) {
        this.moveLink = createMoveLink(gameId, moveNumber);
    }
    
    private String createMoveLink(String gameId, int moveNumber) {
        return "/" + gameId + "/moves/" + moveNumber + "/";
    }
}
