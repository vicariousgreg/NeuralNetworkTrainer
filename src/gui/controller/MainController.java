package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.network.schema.ColorSchema;
import model.network.Network;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
   public static Stage stage;
   public static Network network;
   public static Network backupNetwork;

   @FXML Pane train;
   @FXML Pane interact;
   @FXML Pane examine;
   @FXML Pane parameters;
   @FXML TabPane tabPane;

   @FXML TrainController trainController;
   @FXML InteractController interactController;
   @FXML ExamineController examineController;
   @FXML ParametersController parametersController;

   public void initialize(URL location, ResourceBundle resources) {
      stage = new Stage();
      tabPane.setVisible(false);
   }

   public void loadNetwork() {
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showOpenDialog(stage);
      if (file != null) {
         try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            network = (Network) ois.readObject();
            backupNetwork = network.clone();
            trainController.setNetwork(network);
            interactController.setNetwork(network);
            examineController.extractMemories(network);
            parametersController.setNetwork(network);
            tabPane.setVisible(true);
            ois.close();
         } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error loading network!");
            alert.setContentText(null);
            alert.showAndWait();
         }
      }
   }

   public void saveNetwork() {
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showSaveDialog(stage);
      if (file != null) {
         try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(network);
            out.close();
         } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error saving network!");
            alert.setContentText(null);
            alert.showAndWait();
         }
      }
   }

   public static Network restoreNetwork() {
      network = backupNetwork.clone();
      return network;
   }
}
