package com.kenshin0707.bots.master;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public interface DisplayController {
    Initializable getDisplayController();
    void initialize(URL location, ResourceBundle resources);
    void update();
}
