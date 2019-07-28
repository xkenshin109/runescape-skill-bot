package com.kenshin0707.bots.presets;

import com.kenshin0707.bots.master.main;
import com.kenshin0707.bots.utils.bank;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPathBuilder;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;

import java.util.ArrayList;

public class Location {
    public String name;
    public ArrayList<String> items = new ArrayList<String>();
    public ArrayList<point> route = new ArrayList<point>();
    private int SIZE = 4;
    private int pointer = 0, direction  =1;
    public Coordinate getStep(){
        return route.get(pointer).coordinate();
    }
    public void performStep(){
        this.pointer = this.pointer + this.direction; //The index added with direction
        if(this.pointer>=this.route.size()){
            this.pointer = this.route.size() -1;
            //We reached the end
            //reverse direction to Bank
            this.direction = -1;
        }if(this.pointer < 0){
            //change direction
            this.direction = 1;
            //set pointer to zero
            this.pointer = 0;
        }

    }
    public void setup(){
        //All methods we would need to setup a mining location go here
        findClosestPath();
    }
    private Coordinate getBankArea(){
        return this.route.get(0).coordinate();
    }
    private Coordinate getTriggerArea(){
        //Last Index in array
        return this.route.get(this.route.size()-1).coordinate();
    }
    public boolean didPlayerTriggerBank(Player player){
        if(this.getBankArea().getArea().grow(SIZE,SIZE).contains(player,true) && Inventory.getItems().size() == 28){
            return true;
        }
        return false;
    }
    public boolean didPlayerTriggerMining(Player player){
        if(this.getTriggerArea().getArea().grow(SIZE,SIZE).contains(player,true)  && Inventory.getItems().size() != 28){
            return true;
        }
        return false;
    }
    private void findClosestPath(){
        int ptr = 0;
        int start = 0,end = route.size()-1;
        Player p = Players.getLocal();
        while(start<= end){
            Coordinate startCoord =route.get(start).coordinate();
            Coordinate endCoord =route.get(end).coordinate();
            Coordinate pointerCoord = route.get(ptr).coordinate();
            double distanceFromStart = p.distanceTo(startCoord);
            double distanceFromEnd = p.distanceTo(endCoord);
            double pointDistance = p.distanceTo(pointerCoord);

            if(distanceFromStart < pointDistance){
                ptr = start;
            }
            if(distanceFromEnd < pointDistance){
                ptr = end;
            }
            start++;
            end--;
        }
        if(Inventory.getItems().size() < 28){
            direction = 1;
        }else if(Inventory.getItems().size() == 28){
            direction = -1;
        }

        pointer = ptr;
    }
    public String walk(String action){
        try{
            if(!action.equals("walking")) return action;
            Player player = Players.getLocal();

            if(getStep().getArea().grow(3,3).contains(player)){

                if(didPlayerTriggerBank(player)){
                    bank.setState("deposit");
                    main parent = ((main) Environment.getBot());
                    parent.setAction("banking");
                    return "walking";
                }else if(didPlayerTriggerMining(player)){
                    return "skill";
                }
                performStep();
                return action;
            }else{
                performWalk(getStep());
                return action;
            }
        }catch(Exception ie){
            System.out.println("Finally Found the issue");
        }
        return "walking";
    }
    public void performWalk(Coordinate point){
        try{
            WebPathBuilder pathBuilder = Traversal.getDefaultWeb().getPathBuilder();
            WebPath path = pathBuilder.buildTo(point);
            if(path == null) return;
            path.step();
        }catch(Exception ie){
            System.out.println("");
        }
    }
}
