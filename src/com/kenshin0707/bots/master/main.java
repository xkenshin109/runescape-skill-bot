package com.kenshin0707.bots.master;

import com.kenshin0707.bots.master.display.displayController;
import com.kenshin0707.bots.mining.mining;
import com.kenshin0707.bots.presets.Skill;
import com.kenshin0707.bots.presets.SkillPreset;
import com.kenshin0707.bots.utils.bank;
import com.kenshin0707.bots.woodcut.woodcut;
import com.runemate.game.api.client.embeddable.EmbeddableUI;
import com.runemate.game.api.hybrid.GameEvents;
import com.runemate.game.api.hybrid.RuneScape;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Jeremy on 7/19/2019.
 */
public class main extends LoopingBot implements InventoryListener, EmbeddableUI {


    public Skill startScript;
    public boolean isRunning = false;
    private Initializable _displayController;
    private ObjectProperty<Node> botInterfaceProperty;
    private String action = "skill";
    private Timer farmUntil;
    private Map<String,Skill> _allPresets = new HashMap<>();
    public main(){
        //Default to mining for now
        this.startScript = new mining();
        setEmbeddableUI(this);
        _allPresets.put("Mining",new mining());
        _allPresets.put("Woodcut", new woodcut());
    }
    @Override
    public void onStart(String[] args){
        if(botInterfaceProperty == null ) botInterfaceProperty();
        this.getEventDispatcher().addListener(this);
        this.getEventDispatcher().addListener(new InventoryListener() {
            @Override
            public void onItemAdded(ItemEvent event) {
                startScript.itemAdded(event);
            }
            @Override
            public  void onItemRemoved(ItemEvent event){
                startScript.itemRemoved(event);
            }
        });
        farmUntil = new Timer("FarmTill");

    }
    @Override
    public void onPause(){

    }
    @Override
    public void onResume(){

    }
    @Override
    public void onLoop() {
        if(!RuneScape.isLoggedIn() || !RuneScape.isMapLoaded()) return;

        if(isRunning){
            switch (this.action){
                case "banking":
                    String itemName;
                    ItemDefinition def = startScript.skillEquipment.getDefinition();
                    if(def == null) itemName = "";
                    else itemName = def.getName();
                    bank.run(itemName);
                    break;
                case "skill":
                default:
                    startScript.run();
                    break;
            }

        }

        ((DisplayController)_displayController).update();
    }

    public void startBotTimer(){
        double HOURS = 1.44e+7;
        farmUntil.schedule(new TimerTask() {
            @Override
            public void run() {
                RuneScape.logout();
                Execution.delay(1000); // wait a second
                GameEvents.Universal.LOGIN_HANDLER.disable();
                startBreakTimer();
                isRunning = false;
            }
        },(long) HOURS);
    }
    public void startBreakTimer(){
        double BREAKHOURS = 10000;
        farmUntil.schedule(new TimerTask() {
            @Override
            public void run() {
                GameEvents.Universal.LOGIN_HANDLER.enable();
                Execution.delay(1000); //Wait Another Second
                startBotTimer();
                isRunning = true;
            }
        },(long) BREAKHOURS);
    }
    @Override
    public ObjectProperty<? extends Node> botInterfaceProperty() {
        if(botInterfaceProperty == null){
            FXMLLoader loader = new FXMLLoader();
            _displayController = new displayController(this);
            loader.setController(_displayController);
            try {
                InputStream st = new FileInputStream("out/production/RuneMate/com/kenshin0707/bots/master/display/gui.fxml");
                Node node = loader.load(st);
                botInterfaceProperty = new SimpleObjectProperty<>(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return botInterfaceProperty;
    }
    public void updateLocation(String preset, String location){
        startScript = _allPresets.get(preset);
        startScript.setLocation(location);

    }
    public void updateItem(String item){
        startScript.setFarmItem(item);
    }
    public void setAction(String action){
        this.action = action;
    }
    public void setSubAction(String action){
        this.startScript.setAction(action);
    }

}