package com.kenshin0707.bots.utils;

import com.kenshin0707.bots.master.main;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

/**
 * Created by Jeremy on 7/19/2019.
 */
public  class bank{
    private static String state ="";
    private static String withdrawItem ="";
    public static void setItemToWithdraw(String item, Skill skill){
        withdrawItem = item;
        state = "withdraw";

    }
    public static void setState(String newState){
        state = newState;
    }
    public static void run(String item){
        if(Bank.isOpen()){
            if(Inventory.getItems().size() == 28){
                setState("deposit");
            }
            switch(state){
                case "withdraw":
                    Bank.withdraw(withdrawItem, 1);
                    setState("");
                    break;
                case "deposit":
                    if(item != null){
                        Bank.depositAllExcept(item);
                    }else{
                        Bank.depositInventory();
                    }
                    if(Inventory.getItems().size() != 28){
                        setState("");
                        main parent = (main) Environment.getBot();
                        if(parent!= null) parent.setAction("skill");
                    }
                    break;
                default:
                    Bank.close();
                    if(Inventory.contains(withdrawItem)){
                        main parent = (main) Environment.getBot();
                        if(parent!= null) parent.setAction("skill");
                    }
            }
        }else{
            Bank.open();
        }
    }

}