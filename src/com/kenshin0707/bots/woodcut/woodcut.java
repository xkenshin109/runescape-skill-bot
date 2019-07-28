package com.kenshin0707.bots.woodcut;

import com.kenshin0707.bots.presets.Skill;
import com.kenshin0707.bots.presets.woodcut.WoodcutLocation;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class woodcut extends Skill {
    private boolean loaded = false;
    public woodcut(){
        lookForEquipment("axe");
        readFile();
        List<Integer> ids = new ArrayList<>();
        ids.add(879);
        addAnimationIds(ids);
    }

    @Override
    public void readFile() {
        try{
            String text = new String(Files.readAllBytes(Paths.get("out/production/RuneMate/com/kenshin0707/bots/presets/woodcut/woodcut-locations.txt")));
            JSONObject object = new JSONObject(text);
            for(Object obj: (JSONArray)object.get("locations")){
                WoodcutLocation temp = new WoodcutLocation(obj);
                locations.put(temp.name,temp);
            }
        }catch(Exception ie){
            System.out.println("Could not find the file");
        }
    }

    @Override
    public void run() {

        Player player = Players.getLocal();
        if(player != null){
            lookForEquipment("axe");
            startIdleTimer();
            System.out.println("ACTION: " + getAction()+" PLAYER ANIMATION ID:" + player.getAnimationId() + " IS WALKING: " + player.isMoving());
            if(!loaded){
                loaded = true;
                selectedLocation.setup();
            }
            randomEvents();
            setAction(selectedLocation.walk(getAction()));
            performDerp();
            startFarming();
        }

    }

    @Override
    public void startFarming(){
        if(!getAction().equals("skill")) return;

        if(Inventory.getItems().size() == 28){
            setAction("walking");
            return;
        }
        Player player = Players.getLocal();
        long rnd = Math.round(Math.random()*100);
        if((rnd == 90 || rnd == 1) && currentFocus == null){
            setAction("derping");
            return;
        }
        try{
            if(player == null) return;
            String trees = "";
            if(farmItem.equals("Logs")){
                trees = "Tree";
            }
            if(farmItem.equals("Oak logs")){
                trees = "Oak";
            }
            if(farmItem.equals("Yew logs")){
                trees = "Yew";
            }
            if(currentFocus == null){
                currentFocus = GameObjects
                        .newQuery()
                        .names(trees)
                        .actions("Chop down").within(player.getArea().grow(20,20)).results().nearest();

                if(currentFocus == null) return;
                currentFocus.interact("Chop down");
                startIdleTimer();
            }
        }catch (Exception ie){
            System.out.println("error - startMining(): " + ie.getMessage());
        }
    }
    public Dictionary<String,String> getSkillDetails(Dictionary<String,String> dictionary){
        dictionary.put("action",getAction());
        dictionary.put("currentLvl", com.runemate.game.api.hybrid.local.Skill.WOODCUTTING.getCurrentLevel()+" LvL");
        dictionary.put("currentExp", com.runemate.game.api.hybrid.local.Skill.WOODCUTTING.getExperience()+" EXP");
        dictionary.put("farmed", getTotalFarmed()+" LOGS");

        return  dictionary;
    }
}
