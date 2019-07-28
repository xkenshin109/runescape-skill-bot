package com.kenshin0707.bots.master.display;

import com.kenshin0707.bots.master.DisplayController;
import com.kenshin0707.bots.master.main;
import com.runemate.game.api.hybrid.local.Skill;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Dictionary;
import java.util.ResourceBundle;

public class displayController implements DisplayController, Initializable {
    @FXML
    private Button start;
    @FXML
    private ComboBox<String> skillList;
    @FXML
    private ComboBox<String> farmingList;
    @FXML
    private ComboBox<String> locationList;
    @FXML
    private Label currentLvl,currentAction,exp,total;

    private main script;
    private Initializable _controller;
    public displayController(main script) {
        this.script = script;
    }

    @Override
    public Initializable getDisplayController() {
        if(_controller == null){
            _controller = this;
        }

        return _controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        skillList.getItems().add("Mining");
        skillList.getItems().add("Woodcut");
        skillList.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                loadLocations();
            }
        });
        locationList.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                script.updateLocation(skillList.getSelectionModel().getSelectedItem(),
                        locationList.getSelectionModel().getSelectedItem());
                loadFarmingItems();
            }
        });
        skillList.getSelectionModel().select(0);


        start.setOnAction(event -> {
            script.updateLocation(this.skillList.getSelectionModel().getSelectedItem(),
                    this.locationList.getSelectionModel().getSelectedItem());
            script.updateItem(this.farmingList.getSelectionModel().getSelectedItem());
            script.isRunning = true;
            script.startBotTimer();

        });
    }
    private void loadLocations(){
        locationList.getItems().clear();
        locationList.getItems().addAll(script.startScript.getLocationNames());
        locationList.getSelectionModel().select(0);
    }
    private void loadFarmingItems(){
        farmingList.getItems().clear();
        farmingList.getItems().addAll(script.startScript.getItemNames());
    }
    @Override
    public void update(){
        //This is where all the UI Elements will be updated
        try{
            com.kenshin0707.bots.presets.Skill s=  script.startScript;
            Dictionary<String,String> dict = script.startScript.getUiDetails();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    currentLvl.setText(dict.get("currentLvl"));
                    currentAction.setText(dict.get("action"));
                    total.setText(dict.get("farmed"));
                    exp.setText(dict.get("currentExp"));
                }
            });
        }catch (Exception ie){
            System.out.println("displayController - updateDetails() " + ie.getMessage());
        }
    }
}
