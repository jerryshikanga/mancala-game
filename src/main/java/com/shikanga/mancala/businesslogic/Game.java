package com.shikanga.mancala.businesslogic;

import com.shikanga.mancala.exceptions.EmptyPitException;
import com.shikanga.mancala.exceptions.InvalidPitException;
import com.shikanga.mancala.exceptions.InvalidPlayerException;
import com.shikanga.mancala.exceptions.WrongPlayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public class Game {
    private final int NUM_PLAYERS = 2;
    private final int NUM_PITS = 6;
    private final int BIG_PIT_INDEX = NUM_PITS;

    private final Logger logger;

    private int[][] board;

    private int currentPlayer;

    public Game() {
        board = new int[NUM_PLAYERS][NUM_PITS + 1];
        currentPlayer = 0;

        // Initialize the board with 6 stones in each pit
        for (int playerIndex = 0; playerIndex < NUM_PLAYERS; playerIndex++) {
            for (int pitIndex = 0; pitIndex < NUM_PITS; pitIndex++){
                int NUM_STONES_IN_PIT_AT_START = 6;
                board[playerIndex][pitIndex] = NUM_STONES_IN_PIT_AT_START;
            }
        }
        logger = LoggerFactory.getLogger(Game.class);
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    private int switchBoardOrPlayer(int currentBoardOrPlayer){
        if (currentBoardOrPlayer == 0){
            return 1;
        } else{
            return  0;
        }
    }

    public void validateMove(int player, int pitIndex){
        String message;
        if (player < 0 || player >= NUM_PLAYERS){
            message = "The player ID " + player + " is invalid.";
            this.logger.warn(message);
            throw new InvalidPlayerException(message);
        }
        if (pitIndex < 0 || pitIndex > BIG_PIT_INDEX){
            message = "The pit index " + pitIndex + " is invalid.";
            this.logger.warn(message);
            throw new InvalidPitException(message);
        }
        if (this.board[player][pitIndex] <= 0){
            message = "The pit is empty!";
            this.logger.warn(message);
            throw new EmptyPitException("The pit is empty!");
        }
        if(player!=currentPlayer){
            message = "Player "+player+" has played instead of "+currentPlayer;
            this.logger.warn(message);
            throw new WrongPlayerException(message);
        }
    }

    public int getOpponentCorrespondingPitIndex(int currentPlayerPitIndex){
        // the pits are traversed in opposite directions therefore we have to get the opposite index
        return (NUM_PITS-1) - currentPlayerPitIndex;
    }

    public void captureFromOpponentOppositePit(int player, int pitIndex){
        // Empty from opponents pit into self big pit
        int opponentCorrespondingPitIndex = this.getOpponentCorrespondingPitIndex(pitIndex);
        int opponent = this.switchBoardOrPlayer(player);
        int opponentPitStones = board[opponent][opponentCorrespondingPitIndex];
        board[opponent][opponentCorrespondingPitIndex] = 0;
        board[player][BIG_PIT_INDEX] += opponentPitStones;
        this.logger.info("Captured "+ opponentPitStones + " stones from opponent pit "+opponentCorrespondingPitIndex);
    }

    public void makeMove(int playerWhoHasMadeMove, int pitIndex) {
        this.validateMove(playerWhoHasMadeMove, pitIndex);
        this.logger.info("Move "+ pitIndex +" by player " + playerWhoHasMadeMove+ " validated");
        int currentBoard = playerWhoHasMadeMove;
        int currentStoneCount = board[currentBoard][pitIndex];
        board[currentBoard][pitIndex] = 0;

        int nextPlayer = this.switchBoardOrPlayer(playerWhoHasMadeMove);

        int currentPitIndex = pitIndex+1;
        while (currentStoneCount > 0){
            // Handle a case where we should not sow in the opponents BigPit
            if (currentBoard != playerWhoHasMadeMove && currentPitIndex == BIG_PIT_INDEX){
                this.logger.debug("Next pit is opponents big pit. Skipping.");
                currentBoard = this.switchBoardOrPlayer(currentBoard);
                currentPitIndex = 0;
                continue;
            }

            // Add stone to current pit, decrement number of stones and move to next pit
            board[currentBoard][currentPitIndex]++;
            currentStoneCount --;

            // check if it is the last stone, and it landed in the current player's own side of the board
            // run a few extra checks if this is the case
            // these checks have to be run before switching boards or incrementing the currentPitIndex otherwise we end up with wrong data
            if (currentStoneCount == 0 && currentBoard == playerWhoHasMadeMove){
                this.logger.debug("Last stone landed in current player's side of the board. Running extra checks.");
                // Try to determine the next player
                // the next player is always the opponent unless the current player last stone lands in his own big pit
                if (currentPitIndex == BIG_PIT_INDEX){
                    this.logger.debug("Last stone landed in current player's big pit. He/She has another chance to play.");
                    nextPlayer = playerWhoHasMadeMove;
                }

                // check if this last stone landed in an own pit which is not a big pit
                // we know that the pit was empty before by checking if it has only one stone after incrementing
                // If this is the case then we can capture the stones in the opponents pit opposite the current pit
                int numberOfStonesInCurrentPitAfterIncrementing = board[currentBoard][currentPitIndex];
                if (currentPitIndex != BIG_PIT_INDEX && numberOfStonesInCurrentPitAfterIncrementing == 1){
                    this.logger.info("Last stone landed in a pit belonging to the current player that is empty. Will begin capture of opponent's stones.");
                    this.captureFromOpponentOppositePit(playerWhoHasMadeMove, currentPitIndex);
                }
            }

            // Increment currentPitIndex after checks
            currentPitIndex ++;

            // check if we are at the end of the current players pits and move to the opponents pits
            if (currentPitIndex > NUM_PITS){
                this.logger.debug("Switching to opponent's side of the board");
                currentBoard = this.switchBoardOrPlayer(currentBoard);
                currentPitIndex = 0;
            }
        }
        this.currentPlayer = nextPlayer;
    }

    public int countPlayerStones(int player){
        int [] pitsWithoutBigPit = Arrays.copyOfRange(this.board[player], 0, NUM_PITS);
        return Arrays.stream(pitsWithoutBigPit).sum();
    }

    public Boolean isGameOver(){
        boolean playerOneHasStones = this.countPlayerStones(0) > 0;
        boolean playerTwoHasStones = this.countPlayerStones(1) > 0;
        this.logger.debug("playerOneHasStones "+playerOneHasStones+ " playerTwoHasStones "+playerTwoHasStones);
        return !playerOneHasStones || !playerTwoHasStones;
    }
}

