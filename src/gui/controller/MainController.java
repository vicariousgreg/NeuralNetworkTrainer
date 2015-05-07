package gui.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
   public static MainController instance;
   public static Stage stage;
   public static Stage loaderStage;
   public static ProgressBar progress;

   @FXML Pane interact;
   @FXML Pane examine;
   @FXML ProgressBar progressBar;

   public void initialize(URL location, ResourceBundle resources) {
      instance = this;
      WorkSpace.instance.setController(this);
      stage = new Stage();
      loaderStage = new Stage();
      this.progress = progressBar;

   }

   public void openLoader() {
      try {
         Parent root = FXMLLoader.load(getClass().getResource("../view/loader.fxml"));
         loaderStage.setTitle("Load Network");
         loaderStage.setScene(new Scene(root));
         loaderStage.show();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public void newNetwork() {
      WorkSpace.instance.newNetwork();
   }

   public void closeNetwork() {
      WorkSpace.instance.closeNetwork();
      openLoader();
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


   public void editParameters() throws IOException {
      Parent root = FXMLLoader.load(
            MainController.class.getResource("../view/parameters.fxml"));
      stage.setTitle("Edit Parameters");
      stage.setScene(new Scene(root));
      stage.show();
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
            loaderStage.show();
         } else {
            loaderStage.close();
         }
      }
   }
}
