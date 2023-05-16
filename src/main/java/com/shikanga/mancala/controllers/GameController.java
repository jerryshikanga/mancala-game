package com.shikanga.mancala.controllers;

import com.shikanga.mancala.businesslogic.Game;
import com.shikanga.mancala.utils.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    @Autowired
    private RedisCache redisCache;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @GetMapping("/")
    public Game getGame(HttpServletRequest request){
        String cacheKey = request.getRemoteAddr();
        Game game = redisCache.getGameFromCache(cacheKey);
        if (game == null){
            game = new Game();
            redisCache.setGameInCache(cacheKey, game);
        }
        return game;
    }
}
