package com.shikanga.mancala.controllers.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Move {
    private Integer player;
    private Integer pitIndex;

    public Move() {
        this.player = null;
        this.pitIndex = null;
    }
}
