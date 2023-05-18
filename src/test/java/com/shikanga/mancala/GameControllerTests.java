package com.shikanga.mancala;

import com.shikanga.mancala.businesslogic.Game;
import com.shikanga.mancala.controllers.GameController;
import com.shikanga.mancala.controllers.dto.Move;
import com.shikanga.mancala.exceptions.NoGameFoundException;
import com.shikanga.mancala.utils.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTests {
    @Mock
    private RedisCache redisCache;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GameController gameController;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();
        when(request.getSession()).thenReturn(session);
    }

    public static int[][] generateRandomBoard() {
        int rows = 2;
        int cols = 7;
        int[][] array = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = random.nextInt(1, 8); // Generates random numbers from 1 to 7
            }
        }

        return array;
    }

    @Test
    void testWhenNewGameStartedNewGameIsInstantiatedAndRedisCacheIsSet() {
        String sessionId = session.getId();
        Game game = new Game();
        Game result = gameController.startNewGame(request);
        assertNotNull(result);
        assertEquals(game, result);
        verify(redisCache, times(1)).setGameInCache(sessionId, game);
    }

    @Test
    void testWhenGetCurrentGameReturnsWithExistingGame() {
        String sessionId = session.getId();
        Game game = new Game();
        int [][] randomBoard = generateRandomBoard();
        game.setBoard(randomBoard);
        game.setCurrentPlayer(2);
        when(redisCache.getGameFromCache(sessionId)).thenReturn(game);
        Game result = gameController.getCurrentGame(request);
        assertNotNull(result);
        assertEquals(game, result);
        verify(redisCache, times(1)).getGameFromCache(sessionId);
    }

    @Test
    void testWhenGetCurrentGameCalledWithoutExistingGameThenExceptionIsThrown() {
        String sessionId = session.getId();
        when(redisCache.getGameFromCache(sessionId)).thenReturn(null);
        assertThrows(NoGameFoundException.class, () -> {
            gameController.getCurrentGame(request);
        });
        verify(redisCache, times(1)).getGameFromCache(sessionId);
    }

    @Test
    void testWhenMakeMoveWithExistingGameCacheIsReadAndUpdated() {
        String sessionId = session.getId();
        Game game = new Game();
        game.setBoard(generateRandomBoard());
        Move move = new Move(0, 1);
        when(redisCache.getGameFromCache(sessionId)).thenReturn(game);
        Game result = gameController.makeMove(move, request);
        assertNotNull(result);
        assertEquals(game, result);
        verify(redisCache, times(1)).getGameFromCache(sessionId);
        verify(redisCache, times(1)).setGameInCache(sessionId, game);
    }

    @Test
    void testWhenMakeMoveWithoutExistingGameExceptionIsThrown() {
        String sessionId = session.getId();
        Move move = new Move(0, 1);
        when(redisCache.getGameFromCache(sessionId)).thenReturn(null);
        assertThrows(NoGameFoundException.class, () -> {
            gameController.makeMove(move, request);
        });
        verify(redisCache, times(1)).getGameFromCache(sessionId);
        verify(redisCache, never()).setGameInCache(anyString(), any(Game.class));
    }
}
