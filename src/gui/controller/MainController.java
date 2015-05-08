package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
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

   private static Parent loader;
   static {
      try {
         loader = FXMLLoader.load(MainController.class
               .getResource("../view/loader.fxml"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @FXML Pane interact;
   @FXML Pane examine;
   @FXML ProgressBar progressBar;

   public void initialize(URL location, ResourceBundle resources) {
      instance = this;
      WorkSpace.instance.setController(this);
      loaderStage = new Stage();
      this.progress = progressBar;

      try {
         loader = FXMLLoader.load(getClass().getResource("../view/loader.fxml"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void setStage(Stage newStage) {
      stage = newStage;
   }

   public static void openLoader() {
      loaderStage.setTitle("Load Network");
      loaderStage.setScene(new Scene(loader));
      loaderStage.showAndWait();
   }

   public void closeNetwork() {
      WorkSpace.instance.closeNetwork();
   }

   public void saveNetwork() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showSaveDialog(stage);
      if (file != null) {
         try {
            WorkSpace.instance.saveNetwork(file);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public void exportNetwork() {
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
            stage.hide();
            loaderStage.requestFocus();
         } else {
            loaderStage.close();
            stage.show();
            stage.requestFocus();
         }
      }
   }
}
