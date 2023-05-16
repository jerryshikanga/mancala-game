package com.shikanga.mancala.utils;

import com.google.gson.Gson;
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

    private final static String GAME_CACHE_KEY = "GAME";
    private Jedis jedis;
    private Environment environment;

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
    }

    public Game getGameFromCache(String lookupKey){
        Gson gson = new Gson();
        String cacheValue = jedis.hget(GAME_CACHE_KEY, lookupKey);
        logger.info("Gotten game in cache for lookup key "+lookupKey+" : "+cacheValue);
        if (cacheValue == null || cacheValue.isEmpty() || cacheValue.isBlank()){
            return null;
        }
        return gson.fromJson(cacheValue, Game.class);
    }

    public void setGameInCache(String lookupKey, Game game){
        Gson gson = new Gson();
        String json = gson.toJson(game);
        logger.info("Updating cache with lookup key "+lookupKey+" : "+json);
        jedis.hset(GAME_CACHE_KEY, lookupKey, json);
    }
}


class RedisCacheException extends Exception{
    public RedisCacheException(String message){
        super(message);
    }
}