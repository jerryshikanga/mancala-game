package com.shikanga.mancala;

import com.shikanga.mancala.businesslogic.Game;
import com.shikanga.mancala.exceptions.EmptyPitException;
import com.shikanga.mancala.exceptions.InvalidPitException;
import com.shikanga.mancala.exceptions.InvalidPlayerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class GameTests {
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    @Test
    public void testWhenInitialBoardIsSetupThenAllPitsAreSetToSix() {
        int[][] expectedBoard = {
                {6, 6, 6, 6, 6, 6, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenMakeMoveInSimpleCasePitSetToZeroNextIncremented(){
        int [][] expectedBoard = {
                {0, 7, 7, 7, 7, 7, 1},
                {6, 6, 6, 6, 6, 6, 0}
        };
        game.makeMove(0);
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenMakeMoveExceedsCurrentPlayerBoardOpponentBoardIncremented(){
        int[][] initialBoard = {
                {6, 6, 6, 6, 6, 6, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int [][] expectedBoard = {
                {6, 0, 7, 7, 7, 7, 1},
                {7, 6, 6, 6, 6, 6, 0}
        };
        game.setBoard(initialBoard);
        game.makeMove(1);
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenMakeMoveThatOverlapsToOpponentShouldNotLandInOpponentBigPit(){
        int[][] initialBoard = {
                {6, 6, 6, 6, 6, 7, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int [][] expectedBoard = {
                {6, 6, 6, 6, 6, 0, 1},
                {7, 7, 7, 7, 7, 7, 0}
        };
        game.setBoard(initialBoard);
        game.makeMove(5);
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenMakeMoveInDoubleOverlapThenCurrentBoardWillBeIncremented(){
        int[][] initialBoard = {
                {6, 6, 6, 6, 6, 11, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int [][] expectedBoard = {
                {7, 7, 7, 7, 6, 0, 1},
                {7, 7, 7, 7, 7, 7, 0}
        };
        game.setBoard(initialBoard);
        game.makeMove(5);
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenCountPlayerStonesIsCalledSumOfHisStonesIsReturned(){
        int[][] board = {
                {6, 6, 6, 6, 6, 11, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        game.setBoard(board);
        int expectedPlayerOneStones = 41;
        int expectedPlayerTwoStones = 36;
        assertEquals(expectedPlayerOneStones, game.countPlayerStones(0));
        assertEquals(expectedPlayerTwoStones, game.countPlayerStones(1));
    }

    @Test
    public void testWhenPlayerHasNoStonesGameIsOver(){
        assertFalse(game.isGameOver());
        int[][] playerOneLostBoard = {
                {0, 0, 0, 0, 0, 0, 6},
                {1, 2, 3, 4, 5, 6, 6}
        };
        game.setBoard(playerOneLostBoard);
        assertTrue(game.isGameOver());
        int[][] playerTwoLostBoard = {
                {6, 6, 6, 6, 6, 6, 6},
                {0, 0, 0, 0, 0, 0, 6}
        };
        game.setBoard(playerTwoLostBoard);
        assertTrue(game.isGameOver());
    }

    @Test
    public void testWhenPlayerIsMoreThanTwoOrLessThanZeroExceptionIsThrown(){
        assertThrows(InvalidPlayerException.class, () ->
                game.validateMove(3, 0)
        );
        assertThrows(InvalidPlayerException.class, () ->
                game.validateMove(-1, 0)
        );
    }

    @Test
    public void testWhenPitIsEmptyExceptionIsThrown(){
        int[][] playerTwoEmptyBoard = {
                {6, 6, 6, 6, 6, 6, 6},
                {0, 0, 0, 0, 0, 0, 6}
        };
        game.setBoard(playerTwoEmptyBoard);
        assertThrows(EmptyPitException.class, () ->
                game.validateMove(1, 0)
        );
    }


    @Test
    public void testWhenPitIndexIsLessThanZeroOrMoreThanSixExceptionIsThrown(){
        assertThrows(InvalidPitException.class, () ->
                game.validateMove(0, 8)
        );
        assertThrows(InvalidPitException.class, () ->
                game.validateMove(1, -1)
        );
    }

    @Test
    public void testWhenLastStoneLandsInAnyOtherPitNextPlayerIsOpponent(){
        game.setCurrentPlayer(0);
        int nextPlayer = game.makeMove(1);
        assertEquals(1, nextPlayer);
        assertEquals(1, game.getCurrentPlayer());
    }

    @Test
    public void testWhenLastStoneLandsInSelfBigPitNextPlayerIsSelf(){
        game.setCurrentPlayer(0);
        int nextPlayer = game.makeMove(0);
        assertEquals(0, nextPlayer);
        assertEquals(0, game.getCurrentPlayer());
    }

    @Test
    public void testWhenLastStoneLandsInEmptyPitOppositeOpponentPitGetCollected(){
        int[][] initialBoard = {
                {3, 6, 6, 0, 6, 6, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int [][] expectedBoard = {
                {0, 7, 7, 1, 6, 6, 6},
                {6, 6, 0, 6, 6, 6, 0}
        };
        game.setBoard(initialBoard);
        game.makeMove(0);
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenLastStoneLandsInANonEmptyPitOpponentPitIsNotCollected(){
        int[][] initialBoard = {
                {3, 6, 6, 1, 6, 6, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        int [][] expectedBoard = {
                {0, 7, 7, 2, 6, 6, 0},
                {6, 6, 6, 6, 6, 6, 0}
        };
        game.setBoard(initialBoard);
        game.makeMove(0);
        assertArrayEquals(expectedBoard, game.getBoard());
    }

    @Test
    public void testWhenCapturingStonesSelfBigPitIsFilled(){
        int[][] initialBoard = {
                {1, 1, 1, 1, 1, 1, 0},
                {1, 0, 1, 1, 1, 1, 0}
        };
        int [][] expectedBoard = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 0, 1, 1, 1, 0, 0}
        };
        game.setBoard(initialBoard);
        game.captureFromOpponentOppositePit(0, 0);
        assertArrayEquals(expectedBoard, game.getBoard());
    }
}

