package com.shikanga.mancala.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shikanga.mancala.businesslogic.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class RedisCache {
    private final static Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private final static String BOARD_CACHE_KEY = "BOARD";
    private final static String CURRENT_PLAYER_CACHE_KEY = "CURRENT_PLAYER_CACHE_KEY";
    private Jedis jedis;
    private Environment environment;

    private Gson gson;

    @Autowired
    public RedisCache(Environment environment) throws RedisCacheException {
        this.environment = environment;
        String redisHost = this.environment.getProperty("redis.host");
        if (redisHost == null || redisHost.isEmpty()){
            throw new RedisCacheException("Redis host must be supplied in environment variables");
        }
        String redisPortCacheValue = this.environment.getProperty("redis.port");
        if (redisPortCacheValue == null || redisPortCacheValue.isEmpty()){
            throw new RedisCacheException("Redis port must be supplied in environment variables");
        }
        int redisPort = Integer.parseInt(redisPortCacheValue);
        logger.info("Connecting to cache at host "+redisHost+" and port "+redisPort);
        this.jedis = new Jedis(redisHost, redisPort);
        gson = new Gson();
    }

    public Game getGameFromCache(String lookupKey){
        String boardCacheValue = jedis.hget(BOARD_CACHE_KEY, lookupKey);
        String playerCacheValue = jedis.hget(CURRENT_PLAYER_CACHE_KEY, lookupKey);
        logger.info("Gotten game in cache for lookup key "+lookupKey+ " currentPlayer " + playerCacheValue +" : "+boardCacheValue);
        if (boardCacheValue == null || boardCacheValue.isEmpty() || boardCacheValue.isBlank() || boardCacheValue.equals("{}")){
            return null;
        }

        if (playerCacheValue == null || playerCacheValue.isEmpty() || playerCacheValue.isBlank() || playerCacheValue.equals("{}")){
            return null;
        }
        int [][]  board = gson.fromJson(boardCacheValue, int[][].class);
        Game game = new Game();
        game.setBoard(board);
        game.setCurrentPlayer(Integer.valueOf(playerCacheValue));
        return game;
    }

    public void setGameInCache(String lookupKey, Game game){
        String boardJson = gson.toJson(game.getBoard());
        logger.info("Updating cache with lookup key "+lookupKey+ " currentPlayer "+ game.getCurrentPlayer() + " : "+boardJson);
        jedis.hset(BOARD_CACHE_KEY, lookupKey, boardJson);
        jedis.hset(CURRENT_PLAYER_CACHE_KEY, lookupKey, String.valueOf(game.getCurrentPlayer()));
    }
}


class RedisCacheException extends Exception{
    public RedisCacheException(String message){
        super(message);
    }
}