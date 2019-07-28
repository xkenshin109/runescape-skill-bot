package com.kenshin0707.bots.presets;

import com.runemate.game.api.hybrid.location.Coordinate;

public class point {
    public int x, y, plane;
    public point(int x, int y, int plane){
        this.x = x;
        this.y = y;
        this.plane = plane;
    }
    public Coordinate coordinate(){
        return new Coordinate(this.x,this.y,this.plane);
    }
}
