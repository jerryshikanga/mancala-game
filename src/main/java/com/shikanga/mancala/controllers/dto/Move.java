package com.shikanga.mancala.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class Move {
    private Integer player;
    private Integer pitIndex;

    public Move() {
        this.player = null;
        this.pitIndex = null;
    }
}
