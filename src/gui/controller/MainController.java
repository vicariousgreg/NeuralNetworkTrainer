package gui.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
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
   public static ProgressBar progress;

   @FXML Pane interact;
   @FXML Pane examine;
   @FXML Pane parameters;
   @FXML TabPane tabPane;
   @FXML ProgressBar progressBar;

   public void initialize(URL location, ResourceBundle resources) {
      WorkSpace.instance.setController(this);
      stage = new Stage();
      tabPane.setVisible(false);
      this.progress = progressBar;
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
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.loadNetwork(file);
               return null;
            }
         };
         progressBar.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void saveNetwork() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showSaveDialog(stage);
      if (file != null) {
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.saveNetwork(file);
               return null;
            }
         };
         progressBar.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void consolidateMemory() {
      Task<Void> task = new Task<Void>() {
         @Override
         public Void call() {
            System.out.println("Rebuilding network...");
            WorkSpace.instance.consolidateMemory();
            return null;
         }
      };
      progressBar.progressProperty().bind(task.progressProperty());
      new Thread(task).start();
   }

   public void saveMemory() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showSaveDialog(MainController.stage);
      if (file != null) {
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.saveMemory(file);
               return null;
            }
         };
         progressBar.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void loadMemory() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(MainController.stage);
      if (file != null) {
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.loadMemory(file);
               return null;
            }
         };
         progressBar.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void wipeMemory() {
      Task<Void> task = new Task<Void>() {
         @Override
         public Void call() {
            WorkSpace.instance.wipeMemory();
            return null;
         }
      };
      progressBar.progressProperty().bind(task.progressProperty());
      new Thread(task).start();
   }

   @Override
   public void update(Observable o, Object arg) {
      System.out.println("Main Controller update!");
      progressBar.progressProperty().unbind();
      progressBar.setProgress(0);

      if (arg instanceof String) {
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Error!");
         alert.setHeaderText((String) arg);
         alert.setContentText(null);
         alert.showAndWait();
      } else {
         if (WorkSpace.instance.getNetwork() == null) {
            tabPane.setVisible(false);
         } else {
            tabPane.setVisible(true);
         }
      }
   }
}
