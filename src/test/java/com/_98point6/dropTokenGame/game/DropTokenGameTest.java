package com._98point6.dropTokenGame.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import java.util.Arrays;



@RunWith(MockitoJUnitRunner.class)
public class DropTokenGameTest {
    
    @Test
    public void columnWinTest() {
        DropTokenGame dtg = new DropTokenGame("randomGameID", Arrays.asList("player1", "player2"),
                2, 2, 2);
        dtg.postMove(1, "player1");
        dtg.postMove(2, "player2");
        
        Assertions.assertEquals(DropTokenGame.GameStatus.IN_PROGRESS.name(), dtg.getGameStatus());
        Assertions.assertEquals(2, dtg.getMoves().size());
        Assertions.assertNull(dtg.getWinner());
        
        dtg.postMove(1, "player1");
        Assertions.assertEquals(DropTokenGame.GameStatus.DONE.name(), dtg.getGameStatus());
        Assertions.assertEquals(3, dtg.getMoves().size());
        Assertions.assertEquals("player1", dtg.getWinner());
    }
    
    @Test
    public void forwardSlashDiagonalWinTest() {
        DropTokenGame dtg = new DropTokenGame("randomGameID", Arrays.asList("player1", "player2"),
                4, 4, 4);
        dtg.postMove(4, "player1");
        dtg.postMove(4, "player2");
        dtg.postMove(4, "player1");
        dtg.postMove(4, "player2");
        dtg.postMove(2, "player1");
        dtg.postMove(3, "player2");
        dtg.postMove(3, "player1");
        dtg.postMove(3, "player2");
        dtg.postMove(3, "player1");
        dtg.postMove(2, "player2");
        dtg.postMove(2, "player1");
        
        Assertions.assertEquals(DropTokenGame.GameStatus.IN_PROGRESS.name(), dtg.getGameStatus());
        Assertions.assertNull(dtg.getWinner());
        
        dtg.postMove(1, "player2");
        Assertions.assertEquals(DropTokenGame.GameStatus.DONE.name(), dtg.getGameStatus());
        Assertions.assertEquals(12, dtg.getMoves().size());
        Assertions.assertEquals("player2", dtg.getWinner());
    }
    
    @Test
    public void backSlashDiagonalWinCheck() {
        DropTokenGame dtg = new DropTokenGame("randomGameID", Arrays.asList("player1", "player2"),
                4, 4, 4);
        dtg.postMove(1, "player1");
        dtg.postMove(1, "player2");
        dtg.postMove(1, "player1");
        dtg.postMove(1, "player2");
        dtg.postMove(3, "player1");
        dtg.postMove(2, "player2");
        dtg.postMove(2, "player1");
        dtg.postMove(2, "player2");
        dtg.postMove(2, "player1");
        dtg.postMove(3, "player2");
        dtg.postMove(3, "player1");
        Assertions.assertEquals(DropTokenGame.GameStatus.IN_PROGRESS.name(), dtg.getGameStatus());
        Assertions.assertEquals(11, dtg.getMoves().size());
        Assertions.assertNull(dtg.getWinner());
        dtg.postMove(4, "player2");
        Assertions.assertEquals(DropTokenGame.GameStatus.DONE.name(), dtg.getGameStatus());
        Assertions.assertEquals(12, dtg.getMoves().size());
        Assertions.assertEquals("player2", dtg.getWinner());
    }
    
    
    @Test
    public void rowWinTest() {
        DropTokenGame dtg = new DropTokenGame("randomGameID", Arrays.asList("player1", "player2"),
                3, 3, 3);
        dtg.postMove(1, "player1");
        dtg.postMove(1, "player2");
        dtg.postMove(3, "player1");
        dtg.postMove(3, "player2");
        Assertions.assertEquals(DropTokenGame.GameStatus.IN_PROGRESS.name(), dtg.getGameStatus());
        Assertions.assertEquals(4, dtg.getMoves().size());
        Assertions.assertNull(dtg.getWinner());
        
        dtg.postMove(2, "player1");
        Assertions.assertEquals(DropTokenGame.GameStatus.DONE.name(), dtg.getGameStatus());
        Assertions.assertEquals(5, dtg.getMoves().size());
        Assertions.assertEquals("player1", dtg.getWinner());
    }
    
    @Test
    public void forwardSlashDiagonalWinBigBoardTest() {
        DropTokenGame dtg = new DropTokenGame("randomGameID", Arrays.asList("player1", "player2"),
                5, 5, 4);
        dtg.postMove(1,"player1");
        dtg.postMove(2, "player2");
        dtg.postMove(2, "player1");
        dtg.postMove(3, "player2");
        dtg.postMove(3, "player1");
        dtg.postMove(4, "player2");
        dtg.postMove(4, "player1");
        dtg.postMove(4, "player2");
        dtg.postMove(4, "player1");
        dtg.postMove(1, "player2");
        dtg.postMove(5, "player1");
        dtg.postMove(5, "player2");
        dtg.postMove(5, "player1");
        dtg.postMove(5, "player2");
        dtg.postMove(5, "player1");
        dtg.postMove(1, "player2");
        Assertions.assertEquals(DropTokenGame.GameStatus.IN_PROGRESS.name(), dtg.getGameStatus());
        Assertions.assertEquals(16, dtg.getMoves().size());
        Assertions.assertNull(dtg.getWinner());
        
        dtg.postMove(3, "player1");
        Assertions.assertEquals(DropTokenGame.GameStatus.DONE.name(), dtg.getGameStatus());
        Assertions.assertEquals(17, dtg.getMoves().size());
        Assertions.assertEquals("player1", dtg.getWinner());
    }
}
