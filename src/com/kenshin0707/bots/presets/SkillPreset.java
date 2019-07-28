package com.kenshin0707.bots.presets;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Item;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

import java.util.List;

public interface SkillPreset{
    String farmItem = "";
    Item skillEquipment = null;
    GameObject currentFocus = null;
    void setAction(String action);
    void readFile();
    void itemAdded(ItemEvent item);
    void itemRemoved(ItemEvent item);
    void run();
    void setFarmItem(String item);
    void setLocation(String name);
    List<String> getLocationNames();
    List<String> getItemNames();
}
