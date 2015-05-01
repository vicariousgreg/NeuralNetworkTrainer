package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.network.Parameters;
import model.network.schema.ColorSchema;
import model.network.Network;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
   private Stage stage;
   public static Network network = new Network(new ColorSchema());

   @FXML Pane train;
   @FXML Pane interact;
   @FXML Pane examine;
   @FXML Pane parameters;

   @FXML TrainController trainController;
   @FXML InteractController interactController;
   @FXML ExamineController examineController;
   @FXML ParametersController parametersController;

   public void initialize(URL location, ResourceBundle resources) {
      stage = new Stage();
      trainController.setNetwork(network);
      interactController.setNetwork(network);
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(false);
      parameters.setVisible(false);
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
      parameters.setVisible(false);
      System.out.println("Train");
   }

   public void onInteractClick() {
      train.setVisible(false);
      interact.setVisible(true);
      examine.setVisible(false);
      parameters.setVisible(false);
      interactController.train();
      System.out.println("Interact");
   }

   public void onExamineClick() {
      examineController.extractMemories(network);
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(true);
      parameters.setVisible(false);
      System.out.println("Examine");
   }

   public void onSetParametersClick() {
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(false);
      parameters.setVisible(true);
      parametersController.setNetwork(network);
   }
}
