package com.kenshin0707.bots.presets;

import com.kenshin0707.bots.master.main;
import com.kenshin0707.bots.utils.bank;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Item;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import java.util.*;

public class Skill implements SkillPreset, Runnable {
    private List<Integer> actionIds = new ArrayList<>();
    protected Map<String, Location> locations = new HashMap<>();
    protected Location selectedLocation;
    protected String farmItem = "";
    public Item skillEquipment = null;
    protected GameObject currentFocus = null;
    private String action = "walking";
    private long totalFarmed = 0;
    private Timer timer;
    private boolean loaded = false;
    private int totalLoopsIdle = 0;
    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void readFile() {

    }

    @Override
    public void itemAdded(ItemEvent item) {
        if (item.getItem().getDefinition().getName().equals(farmItem)) {
            System.out.println("ADDED: " + item.getItem());
            currentFocus = null;
            this.totalFarmed++;
        }
        if (Inventory.getItems().size() == 28) {
            action = "walking";
            main parent = (main) Environment.getBot();
        }
    }

    @Override
    public void itemRemoved(ItemEvent item) {

    }

    @Override
    public void run() {
        lookForEquipment("");
        Player player = Players.getLocal();
        if (player != null) {
            if (!loaded) {
                loaded = true;
                selectedLocation.setup();
            }
            this.action = selectedLocation.walk(this.action);
            performDerp();
            startFarming();
        }
    }

    @Override
    public void setFarmItem(String item) {
        this.farmItem = item;
    }

    @Override
    public void setLocation(String name) {
        selectedLocation = locations.get(name);
    }

    @Override
    public List<String> getLocationNames() {
        List<String> names = new ArrayList<>();
        for (Location loc : locations.values()) {
            names.add(loc.name);
        }
        return names;
    }

    @Override
    public List<String> getItemNames() {
        if (selectedLocation == null) return new ArrayList<>();
        return selectedLocation.items;
    }

    protected void startIdleTimer() {
        if (timer == null) {
            timer = new Timer("MiningStarted");
        }
        Player player = Players.getLocal();
        if (player == null) return;
        if(actionIds.contains(player.getAnimationId())){
            totalLoopsIdle = 0;
        }else
        if(totalLoopsIdle == 4 && currentFocus != null){
            //4 interations of the loop was ran and still standing still
            currentFocus = null;
            totalLoopsIdle = 0;
            System.out.println("Standing still..Too long resetting");
        }else
        if (((player.getAnimationId() == -1 && !player.isMoving())) && currentFocus != null) {
            System.out.println("Standing still..Increase Idle");
            totalLoopsIdle++;
        }
    }

    protected void performDerp() {
        if (!action.equals("derping")) return;
        Player player = Players.getLocal();
        int random = new Random().nextInt(5 + 5) - 5;
        if (player == null) return;
        point derpPoint = new point(player.getPosition().getX() + random, player.getPosition().getY() + random, 0);
        selectedLocation.performWalk(derpPoint.coordinate());
        action = "skill";
    }

    public void startFarming() {

    }

    protected void lookForEquipment(String name) {
        if (skillEquipment != null) return;
        boolean itemFound = false;
        for (Item inv : Inventory.getItems()) {
            if (inv.getDefinition() == null) continue;
            if (inv.getDefinition().getName().contains(name)) {
                System.out.println(name + " found in Inventory!");
                itemFound = true;
                skillEquipment = inv;
                break;
            }
        }
        for (Item eqp : Equipment.getItems()) {
            if (eqp.getDefinition().getName().contains(name)) {
                System.out.println(name + " found Equipped!");
                itemFound = true;
                skillEquipment = eqp;
                break;
            }
        }
        if (!itemFound) {
            main parent = (main) Environment.getBot();
            if (parent != null) parent.setAction("equipment");
            bank.setItemToWithdraw(name, com.runemate.game.api.hybrid.local.Skill.WOODCUTTING);
        }
    }
    protected void randomEvents(){
        if(ChatDialog.hasText("A bird's nest falls out of the tree.")){
            currentFocus = GameObjects.newQuery().names("Bird nest").actions("Take").results().nearest();
            if(currentFocus != null){
                action = "picking up birds nest";
                currentFocus.interact("Take");
            }
        }
        Npc rndNpc = Npcs.newQuery().actions("Dismiss").results().nearest();
        if(rndNpc != null){
            System.out.println("Dismissing Random Event");
            rndNpc.interact("Dismiss");
        }

    }
    protected void addAnimationIds(List<Integer> ids){
        actionIds = ids;
    }
    public Dictionary<String,String> getUiDetails(){
        return getSkillDetails(new Hashtable<>());
    }

    public Dictionary<String,String> getSkillDetails(Dictionary<String,String> dictionary){
        return  dictionary;
    }
    protected String getAction(){
        return this.action;
    }
    protected long getTotalFarmed(){
        return this.totalFarmed;
    }
}