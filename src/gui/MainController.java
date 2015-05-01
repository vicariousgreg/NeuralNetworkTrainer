package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import network.ColorSchema;
import network.Network;
import network.Experience;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {
   private Stage stage;
   public static Network network = new Network(new ColorSchema());

   @FXML Pane train;
   @FXML Pane interact;
   @FXML Pane examine;

   @FXML TrainController trainController;
   @FXML InteractController interactController;
   @FXML ExamineController examineController;

   public void initialize(URL location, ResourceBundle resources) {
      stage = new Stage();
      trainController.setNetwork(network);
      interactController.setNetwork(network);
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(false);
   }

   public void loadNetwork() {
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showOpenDialog(stage);
      if (file != null) {
         try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            network = (Network) ois.readObject();
            trainController.setNetwork(network);
            interactController.setNetwork(network);
            ois.close();
         } catch (Exception e) {
            System.out.println("Could not load newtork!");
            e.printStackTrace();
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
            System.out.println("Could not save newtork!");
            e.printStackTrace();
         }
      }
   }

   public void onTrainClick() {
      train.setVisible(true);
      interact.setVisible(false);
      examine.setVisible(false);
      System.out.println("Train");
   }

   public void onInteractClick() {
      train.setVisible(false);
      interact.setVisible(true);
      examine.setVisible(false);
      interactController.train();
      System.out.println("Interact");
   }

   public void onExamineClick() {
      examineController.extractMemories(network);
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(true);
      System.out.println("Examine");
   }
}
