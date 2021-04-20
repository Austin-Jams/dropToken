package com._98point6.dropTokenGame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "game")
public class DropTokenGame {
    
    public enum GameStatus {
        DONE,
        IN_PROGRESS
    }
    
    
    @Id
    private String gameId;
    private String[][] board;
    private int tokensNeededToWin;
    private List<String> players;
    private String playerAtBat;
    private List<Move> moves;
    private String gameStatus;
    private String winner;
    private int rowCount;
    private int columnCount;
    
    
    
    public DropTokenGame(String gameId, List<String> players, int columns, int rows, int tokensNeededToWin) {
        this.gameId = gameId;
        this.board = new String[rows][columns];
        this.players = players;
        this.playerAtBat = players.get(0);
        this.moves = new ArrayList<>();
        this.gameStatus = GameStatus.IN_PROGRESS.name();
        this.rowCount = rows;
        this.columnCount = columns;
        this.tokensNeededToWin = tokensNeededToWin;
    }
    
    public void postMove(int column, String playerId) {
        int[] newTokenLocation = placeToken(column - 1, playerId);
        moves.add(new Move(playerId, column));
        //if the amount of moves made is greater than the threshold for the possibility of a game
        List<Move> moveMoves = moves.stream().filter(m -> Move.MoveType.MOVE.name().equalsIgnoreCase(m.getMoveType())).collect(
                Collectors.toList());
        if (moveMoves.size() >= calculateMinimumMovesNeededToWin()) {
            boolean didWin = checkForWin(newTokenLocation);
            if (didWin) {
                winner = playerId;
                gameStatus = GameStatus.DONE.name();
                log.debug(playerId + " has won the game.");
            } else if (isBoardFull()) {
                //Draw
                winner = null;
                gameStatus = GameStatus.DONE.name();
                log.debug("Game has ended in a draw.");
            }
        }
        if (gameStatus.equalsIgnoreCase(GameStatus.IN_PROGRESS.name())) {
            int currentTurnPosition = players.indexOf(playerId);
            if (currentTurnPosition != players.size() - 1) {
                playerAtBat = players.get(currentTurnPosition + 1);
            } else {
                playerAtBat = players.get(0);
            }
            log.debug("It is now " + playerAtBat + "'s turn to place a token");
        }
    }
    
    private int[] placeToken(int column, String playerId) {
        for (int i = rowCount - 1; i >= 0; i--) {
            if (board[i][column] == null) {
                board[i][column] = playerId;
                int[] newTokenLocation = new int[2];
                newTokenLocation[0] = i;
                newTokenLocation[1] = column;
                log.debug("New token placed at (" + i + ", " + column + ")");
                return newTokenLocation;
            }
        }
        //should not happen due to validation
        return null;
    }
    
    public int calculateMinimumMovesNeededToWin() {
        return (players.size() * tokensNeededToWin) - players.size() + 1;
    }
    
    public boolean isValidMove(int column) {
        //column out of bounds
        if (column > columnCount - 1 || column < 0) {
            log.debug("Column out of bounds.");
            return false;
            //test overflow
        } else if (board[0][column] != null) {
            log.debug("Column is full.");
            return false;
        } else {
            return true;
        }
    }
    
    //Board is full when the number of moves made is equivalent to the number of spaces available
    private boolean isBoardFull() {
        List<Move> moveMoves = moves.stream().filter(m -> m.getMoveType().equalsIgnoreCase(Move.MoveType.MOVE.name())).collect(
                Collectors.toList());
        return moveMoves.size() == columnCount * rowCount;
    }
    
    //Search all possible directions
    private boolean checkForWin(int[] tokenLocation) {
        if (checkColumn(tokenLocation)) {
            return true;
        } else if (checkRow(tokenLocation)) {
            return true;
        } else if (checkBackSlashDiagonal(tokenLocation)) {
            return true;
        } else
            return checkForwardSlashDiagonal(tokenLocation);
    }
    
    //Looks at array of a search direction and determines if a win is true
    private boolean isWinningList(List<String> boardSlice) {
        String[] x = boardSlice.toArray(new String[0]);;
        int tokensInAColumn = 0;
        for (String playerId : boardSlice) {
            if (tokensInAColumn == tokensNeededToWin) {
                break;
            }
            if (playerId != null && playerId.equalsIgnoreCase(playerAtBat)) {
                tokensInAColumn++;
            } else {
                tokensInAColumn = 0;
            }
        }
        return tokensInAColumn == tokensNeededToWin;
    }
    
    private boolean checkColumn(int[] tokenLocation) {
        List<String> columnCopy = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            if (board[i][tokenLocation[1]] != null) {
                columnCopy.add(board[i][tokenLocation[1]]);
            } else {
                columnCopy.add("");
            }
        }
        return isWinningList(columnCopy);
    }
    
    private boolean checkRow(int[] tokenLocation) {
        List<String> rowCopy = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            if (board[tokenLocation[0]][i] != null) {
                rowCopy.add(board[tokenLocation[0]][i]);
            } else {
                rowCopy.add("");
            }
        }
        return isWinningList(rowCopy);
    }
    
    private boolean checkBackSlashDiagonal(int[] tokenLocation) {
        List<String> backSlashDiagonalValues = new ArrayList<>();
        List<int[]> backSlashDiagonalPoints = new ArrayList<int[]>();
        int[] topBorderPoint = getBackSlashBorderPoint(tokenLocation);
        int rowStarter = topBorderPoint[0];
        int columnStarter = topBorderPoint[1];
        while (rowStarter < rowCount && columnStarter < columnCount) {
            backSlashDiagonalPoints.add(new int[] {rowStarter, columnStarter});
            rowStarter++;
            columnStarter++;
        }
        for (int[] point : backSlashDiagonalPoints) {
            if (board[point[0]][point[1]] != null) {
                backSlashDiagonalValues.add(board[point[0]][point[1]]);
            } else {
                backSlashDiagonalValues.add("");
            }
        }
        return isWinningList(backSlashDiagonalValues);
    }
    
    private int[] getBackSlashBorderPoint(int[] tokenLocation) {
        int[] backSlashBorderPoint = new int[2];
        //already border point
        if (tokenLocation[0] == 0 || tokenLocation[1] == 0) {
            backSlashBorderPoint = tokenLocation;
        } else if (tokenLocation[0] > tokenLocation[1]) {
            int rowPoint = tokenLocation[0] - tokenLocation[1];
            backSlashBorderPoint[0] = rowPoint;
        } else if (tokenLocation[0] < tokenLocation[1]) {
            int columnPoint = tokenLocation[1] - tokenLocation[0];
            backSlashBorderPoint[1] = columnPoint;
        }
        return backSlashBorderPoint;
    }
    
    private int[] getForwardSlashBorderPoint(int[] tokenLocation) {
        int[] forwardSlashBorderPoint = new int[2];
        //already a border point
        if (tokenLocation[0] == 0 || tokenLocation[1] == columnCount -1) {
            forwardSlashBorderPoint = tokenLocation;
        } else if (tokenLocation[0] < columnCount - tokenLocation[1] - 1) {
            int columnPoint = tokenLocation[0] - tokenLocation[1];
            forwardSlashBorderPoint[1] = columnPoint;
        } else if (tokenLocation[0] > columnCount - tokenLocation[1] - 1) {
            int columnPoint = columnCount - 1;
            int rowPoint = tokenLocation[0] - (columnCount - tokenLocation[1] - 1);
            forwardSlashBorderPoint[1] = columnPoint;
            forwardSlashBorderPoint[0] = rowPoint;
        } else if (tokenLocation[0] == columnCount - tokenLocation[1] - 1) {
            forwardSlashBorderPoint[1] = columnCount - 1;
        }
        return forwardSlashBorderPoint;
    }
    
    private boolean checkForwardSlashDiagonal(int[] tokenLocation) {
        List<String> forwardSlashDiagonalValues = new ArrayList<>();
        List<int[]> forwardSlashDiagonalPoints = new ArrayList<int[]>();
        int[] topBorderPoint = getForwardSlashBorderPoint(tokenLocation);
        int rowStarter = topBorderPoint[0];
        int columnStarter = topBorderPoint[1];
        while (rowStarter < rowCount && columnStarter >= 0) {
            forwardSlashDiagonalPoints.add(new int[] {rowStarter, columnStarter});
            rowStarter++;
            columnStarter--;
        }
        for (int[] point : forwardSlashDiagonalPoints) {
            if (board[point[0]][point[1]] != null) {
                forwardSlashDiagonalValues.add(board[point[0]][point[1]]);
            } else {
                forwardSlashDiagonalValues.add("");
            }
        }
        return isWinningList(forwardSlashDiagonalValues);
    }

}
