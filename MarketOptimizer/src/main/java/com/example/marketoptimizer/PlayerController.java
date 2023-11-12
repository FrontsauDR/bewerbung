package com.example.marketoptimizer;
import com.example.marketoptimizer.database.Connector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/*
 * Class to control player view and add functionality
 */
public class PlayerController {

    private StartController sController;

    @FXML
    private Text playerName;
    @FXML
    private ListView<String> orderList;
    @FXML
    private ScrollPane orderScrollPane;
    private NameSettings activePlayer;
    @FXML
    private VBox itemView;
    @FXML
    private GridPane startGrid;
    @FXML
    private Label currentCountText, currentSellText, currentBuyText, lastCountText, lastSellText, lastBuyText,
            beforeCountText, beforeSellText, beforeBuyText, allCountText, allSellText, allBuyText;


    @FXML
    void initialize() {
        Connector conn = new Connector("marketoptimizer.db");
        ArrayList<NameSettings> nameSettings = FileChecker.readJson();
        activePlayer = nameSettings.get(3);
        // set top left name of active player
        setPlayerName(activePlayer.getPlayerName());
        int playerID = getPlayerID(activePlayer.getPlayerName());
        // remove empty vbox for items at start
        startGrid.getChildren().remove(2);
        // fill ScrollPane with elements for activeplayer
        setOrderOverview(conn.getOrder(playerID), conn.getItems(playerID));
    }

    /**
     * Create ScrollPane with Buttons and Labels
     * @param orderList
     * @param itemList
     */
    private void setOrderOverview(String[] orderList, String[] itemList) {
        VBox vbox = new VBox();
        for(int i = 0; i < orderList.length; i++) {
            if(orderList[i] != null) {
                String[] singleOrder = orderList[i].split("\t");
                // create Button
                Button button = new Button(singleOrder[0]);
                button.setMinWidth(1000);
                // set Action for Button
                button.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        getInfos(button.getText());
                    }
                });
                // create Label
                Label label = new Label(singleOrder[1]);
                label.setMinWidth(150);
                // different style for each category of amounts of traded times
                if(Integer.parseInt(singleOrder[1]) < 10) {
                    label.setStyle("-fx-font-size: 14; -fx-text-fill: white; -fx-background-color: red");
                } else if(Integer.parseInt(singleOrder[1]) <= 30) {
                    label.setStyle("-fx-font-size: 14; -fx-text-fill: black; -fx-background-color: orange");
                } else {
                    label.setStyle("-fx-font-size: 14; -fx-text-fill: white; -fx-background-color: green");
                }
                label.setAlignment(Pos.CENTER);
                HBox hbox = new HBox(button, label);
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(hbox);
            }
        }
        // check if every item is orderlist. if not add item with black label and 0 count
        for(int n = 0; n < itemList.length; n++) {
            boolean found = false;
            for(int j = 0; j < orderList.length; j++) {
                if(orderList[j] != null) {
                    String[] singleOrder = orderList[j].split("\t");
                    if (singleOrder[0].equals(itemList[n])) {
                        found = true;
                    }

                }
            }
            // add items not traded with black label and 0 count
            if(!found && itemList[n] != null) {
                Button button = new Button(itemList[n]);
                button.setMinWidth(1000);
                Label label = new Label("0");
                label.setMinWidth(150);
                label.setStyle("-fx-font-size: 14; -fx-text-fill: white; -fx-background-color: black");
                label.setAlignment(Pos.CENTER);
                HBox hbox = new HBox(button, label);
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER);
                vbox.getChildren().add(hbox);
            }
        }
        // add everything to scrollpane
        orderScrollPane.setContent(vbox);
    }

    /**
     * set playername to text on top left cornor
     * @param playerString
     */
    private void setPlayerName(String playerString) {

        playerName.setText(playerString);
    }

    /**
     * get playerid for given playername
     * @param playerName
     * @return
     */
    private int getPlayerID(String playerName) {
        ArrayList<NameSettings> nameSettings = FileChecker.readJson();
        int id = 0;
        if(Objects.equals(playerName, nameSettings.get(0).getPlayerName())) {
            id = 1;
        } else if(Objects.equals(playerName, nameSettings.get(1).getPlayerName())) {
            id = 2;
        } else if(Objects.equals(playerName, nameSettings.get(2).getPlayerName())) {
            id = 3;
        }
        return id;
    }

    /**
     * get infos for items on click of button
     * @param item
     */
    private void getInfos(String item) {
        int playerID = getPlayerID(playerName.getText());
        Connector conn = new Connector("marketoptimizer.db");
        List<Integer> completeValues = new ArrayList<>();
        completeValues.addAll(Arrays.asList(conn.getCurrentValues(item, playerID)));
        completeValues.addAll(Arrays.asList(conn.getLastValues(item, playerID)));
        completeValues.addAll(Arrays.asList(conn.getOldValues(item, playerID)));
        completeValues.addAll(Arrays.asList(conn.getAllTimeValues(item, playerID)));
        System.out.println(completeValues.toString());
        showInfos(item, completeValues);
    }

    private void showInfos(String item, List<Integer> date) {
        if(startGrid.getChildren().get(2) != null) {
            startGrid.getChildren().remove(2);
        }
        startGrid.getChildren().add(itemView);

        currentCountText.setText(date.get(0).toString());
        currentSellText.setText(date.get(1).toString());
        currentBuyText.setText(date.get(2).toString());

        lastCountText.setText(date.get(3).toString());
        lastSellText.setText(date.get(4).toString());
        lastBuyText.setText(date.get(5).toString());

        beforeCountText.setText(date.get(6).toString());
        beforeSellText.setText(date.get(7).toString());
        beforeBuyText.setText(date.get(8).toString());

        allCountText.setText(date.get(9).toString());
        allSellText.setText(date.get(10).toString());
        allBuyText.setText(date.get(11).toString());

    }

    @FXML
    private void backButton() throws IOException {
        OptimizerApplication.changeScene("start-view.fxml");
    }

    @FXML
    private void exitButton() {
        Platform.exit();
        System.exit(0);
    }


}
