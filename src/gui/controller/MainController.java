package gui.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.WorkSpace;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MainController implements Initializable, Observer {
   public static Stage stage;

   @FXML Pane interact;
   @FXML Pane examine;
   @FXML Pane parameters;
   @FXML TabPane tabPane;

   public void initialize(URL location, ResourceBundle resources) {
      WorkSpace.instance.setController(this);
      stage = new Stage();
      tabPane.setVisible(false);
   }

   public void newNetwork() {
      WorkSpace.instance.newNetwork();
      tabPane.setVisible(true);
   }

   public void closeNetwork() {
      WorkSpace.instance.closeNetwork();
      tabPane.setVisible(false);
   }

   public void loadNetwork() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(stage);
      if (file != null) {
         // Rebuild network on background thread.
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.loadNetwork(file);
               tabPane.setVisible(true);
               return null;
            }
         };
         //progress.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void saveNetwork() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showSaveDialog(stage);
      if (file != null) {
         // Rebuild network on background thread.
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.saveNetwork(file);
               tabPane.setVisible(true);
               return null;
            }
         };
         //progress.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   @Override
   public void update(Observable o, Object arg) {
      if (WorkSpace.instance.getNetwork() == null) {
         tabPane.setVisible(false);
      } else {
         tabPane.setVisible(true);
      }
   }
}
