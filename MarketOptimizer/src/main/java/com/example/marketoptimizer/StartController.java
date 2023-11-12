package com.example.marketoptimizer;


import com.example.marketoptimizer.database.Connector;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;

/*
 * Class to control start page and add functionality
 */
public class StartController {

    private PlayerController playerC;

    @FXML
    private Label welcomeText;
    @FXML
    private VBox playerColumn;
    @FXML
    private TextField player1, player2, player3;
    @FXML
    private TextArea inputField, quickBarField;
    @FXML
    private ComboBox<String> playerComboBox, playerComboBox2 = new ComboBox();
    private Stage primaryStage;


    ArrayList<NameSettings> nameArray = new ArrayList<>();
    ArrayList<NameSettings> settingsArray = new ArrayList<>();

    /**
     * Initialize gets called at start
     * @throws SQLException
     */
    @FXML
    void initialize() throws SQLException, ParseException {
        insertSettings();
        Connector conn = new Connector("test.db");
        conn.createDataBase();
        conn.createTable();
        Connector conn2 = new Connector("marketoptimizer.db");
    }

    /**
     * Read Input, basic processing and call method to transfer it to DB
     */
    @FXML
    protected void onReadButtonClick() throws ParseException {
        String selectedPlayer = playerComboBox.getValue();
        String input = inputField.getText();
        String[] line = input.split("\n");
        // map playername to int for userID
        int playerNumber = getPlayerID(selectedPlayer);

        Connector conn = new Connector("marketoptimizer.db");
        String lastOrder = conn.getLastOrder(playerNumber);
        int newOrder = removeDuplicate(line, lastOrder);
        // process each line of input
        for(int n = 0; n < newOrder; n++) {
            String[] splittedLine = line[n].split("\\t");
            // repeat for quantity of item
            int count = Integer.parseInt(splittedLine[1]);
            for (int i = 1; i <= count; i++) {
                // Get price
                String[] priceArray = splittedLine[4].split(" ");
                long price = Long.parseLong(priceArray[0].replace(".", ""));
                // call connector to insert into database
                conn.insertOrder(splittedLine[2], price, splittedLine[0], playerNumber);
            }
        }
        // update orderCount after inserting new orders
        conn.updateOrderCount(playerNumber);
        inputField.setText("");
    }

    /**
     * Read input for quickbar list
     */
    @FXML
    protected void onQuickBarReadButtonClick() {
        String selectedPlayer = playerComboBox2.getValue();
        String input = quickBarField.getText();
        String[] line = input.split("\n");
        int playerNumber = getPlayerID(selectedPlayer);

        Connector conn = new Connector("marketoptimizer.db");
        conn.deleteItems(playerNumber);

        for (String s : line) {
            String[] splittedLine = s.split(" ", 2);
            if (splittedLine[0].equals("-")) {
                conn.insertItems(splittedLine[1], playerNumber);
            }
        }
        quickBarField.setText("");
    }

    /**
     * Method to save new Names in JSON
     */
    @FXML
    private void applyButton() throws SQLException {
        NameSettings nameSettings1 = new NameSettings("player1", player1.getText());
        NameSettings nameSettings2 = new NameSettings("player2", player2.getText());
        NameSettings nameSettings3 = new NameSettings("player3", player3.getText());
        nameArray.add(nameSettings1);
        nameArray.add(nameSettings2);
        nameArray.add(nameSettings3);

        writeJson();
        // Insert/update playernames
        Connector connector = new Connector("marketoptimizer.db");
        for (int i = 1; i <= 3; i++) {
            String player = switch (i) {
                case 1:
                    yield player1.getText();
                case 2:
                    yield player2.getText();
                default:
                    yield player3.getText();
            };
            if(connector.checkPlayer(i) == null) {
                connector.insertPlayer(i, player);
            } else {
                connector.updatePlayer(i, player);
            }
        }
    }

    /**
     * Call FileChecker.readJson() to read settings from file
     * Load settings from file into UI Elements
     */
    public void insertSettings() {
        settingsArray = FileChecker.readJson();
        player1.setText(settingsArray.get(0).getPlayerName());
        player2.setText(settingsArray.get(1).getPlayerName());
        player3.setText(settingsArray.get(2).getPlayerName());

        // Fill ComboBox
        for (int i = 0; i <= 2; i++) {
            if (!Objects.equals(settingsArray.get(i).getPlayerName(), "")) {
                playerComboBox.getItems().add(settingsArray.get(i).getPlayerName());
                playerComboBox2.getItems().add(settingsArray.get(i).getPlayerName());
            }
        }
    }

    /**
     * Write settings to Json file
     */
    private void writeJson() {
        String workingDirectory = System.getProperty("user.dir");
        String absolutePath = workingDirectory + File.separator + "settings.json";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(absolutePath), nameArray);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void switchPlayer1() throws IOException {
        writeActivePlayer(player1.getText());
        OptimizerApplication.changeScene("player-view.fxml");
    }

    @FXML
    private void switchPlayer2() throws IOException {
        writeActivePlayer(player2.getText());
        OptimizerApplication.changeScene("player-view.fxml");
    }

    @FXML
    private void switchPlayer3() throws IOException {
        writeActivePlayer(player3.getText());
        OptimizerApplication.changeScene("player-view.fxml");
    }

    /**
     * helper method to transform playerName to playerNumber
     * @param playerName
     * @return
     */
    private int getPlayerID(String playerName) {
        int id = 0;
        if(Objects.equals(playerName, player1.getText())) {
            id = 1;
        } else if(Objects.equals(playerName, player2.getText())) {
            id = 2;
        } else if(Objects.equals(playerName, player3.getText())) {
            id = 3;
        }
        return id;
    }

    /**
     * remove Duplicate by filtering with time and name of item
     * go through orderList in reversed order and remember item that is duplicate
     * @param orderList
     * @param comp
     * @return array index of duplicate item
     * @throws ParseException
     */
    private int removeDuplicate(String[] orderList, String comp) throws ParseException {
        int count = 0;
        // first insert of orders
        if (comp.equals("null" + "\t" + "null")) {
            return orderList.length;
            // all other inserts of orders
        } else {
            String[] comparison = comp.split("\\t");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("y-M-d h:m");
            Date comparisonDate = dateFormatter.parse(comparison[1]);
            String comparisonName = comparison[0];
            // from last order to first and search for duplicate
            for (int i = orderList.length - 1; i >= 0; i--) {
                String[] splittedLine = orderList[i].split("\\t");
                String date = splittedLine[0].replace(".", "-");
                Date originDate = dateFormatter.parse(date);
                String originName = splittedLine[2];
                // comparison of date and name to get duplicate+
                if (comparisonDate.compareTo(originDate) == 0 && comparisonName.equals(originName)) {
                    // if first order would be duplicate count = 0, -1 to differentiate from no duplicate
                    count = i-1;
                }
            }
            // if no duplicate was found
            if(count == 0) {
                return orderList.length;
            }

            return count+1;
        }

    }

    private void writeActivePlayer(String name) {
        settingsArray = FileChecker.readJson();
        NameSettings activePlayer = new NameSettings("activePlayer", name);
        nameArray.add(settingsArray.get(0));
        nameArray.add(settingsArray.get(1));
        nameArray.add(settingsArray.get(2));
        nameArray.add(activePlayer);
        writeJson();
    }

    @FXML
    private void exitButton() {
        Platform.exit();
        System.exit(0);
    }



}

