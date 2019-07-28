package com.kenshin0707.bots.mining;

import com.kenshin0707.bots.presets.Skill;
import com.kenshin0707.bots.presets.mining.MiningLocation;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;


public class mining extends Skill { ;
    private Dictionary<String, Map<Color,Color>> oreMappings = new Hashtable<>();
    private boolean loaded = false;
    private static int MINING_ANIMATION_ID_ONE = 625, MINING_ANIMATION_ID_TWO = 626;
    public int totalMine;
    public mining(){
        readFile();
        oreMappings.put("Tin ore", new HashMap<>());
        oreMappings.get("Tin ore").put(new Color(48,48,48),new Color(106,106,106));
        oreMappings.put("Copper ore",new HashMap<>());
        oreMappings.get("Copper ore").put(new Color(48,48,48), new Color(74,48,32));
        oreMappings.put("Iron ore",new HashMap<>());
        oreMappings.get("Iron ore").put(new Color(48,48,48), new Color(32,17,14));
        lookForEquipment("pickaxe");
        List<Integer> ids = new ArrayList<>();
        ids.add(MINING_ANIMATION_ID_ONE);
        ids.add(MINING_ANIMATION_ID_TWO);
        addAnimationIds(ids);
    }
    public void run(){

        Player player = Players.getLocal();
        if(player != null){
            lookForEquipment("pickaxe");
            startIdleTimer();
            if(!loaded){
                loaded = true;
                selectedLocation.setup();
            }
            setAction(selectedLocation.walk(getAction()));
            performDerp();
            startFarming();
        }
    }

     public void readFile(){
        try{
            String text = new String(Files.readAllBytes(Paths.get("out/production/RuneMate/com/kenshin0707/bots/presets/mining/mining-locations.txt")));
            JSONObject object = new JSONObject(text);
            for(Object obj: (JSONArray)object.get("locations")){
                MiningLocation temp = new MiningLocation(obj);
                locations.put(temp.name,temp);
            }
        }catch(Exception ie){
            System.out.println("Could not find the file");
        }
    }
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
            if(currentFocus == null){
                currentFocus = GameObjects
                        .newQuery()
                        .colorSubstitutions(oreMappings.get(farmItem))
                        .actions("Mine").within(player.getArea().grow(10,10)).results().nearest();
                if(currentFocus == null) return;
                currentFocus.interact("Mine");
            }

        }catch (Exception ie){
            System.out.println("error - startMining(): " + ie.getMessage());
        }
    }
    public Dictionary<String,String> getSkillDetails(Dictionary<String,String> dictionary){
        dictionary.put("action",getAction());
        dictionary.put("currentLvl", com.runemate.game.api.hybrid.local.Skill.MINING.getCurrentLevel()+" LvL");
        dictionary.put("currentExp", com.runemate.game.api.hybrid.local.Skill.MINING.getExperience()+" EXP");
        dictionary.put("farmed", getTotalFarmed()+" ORES");
        return  dictionary;
    }
}

