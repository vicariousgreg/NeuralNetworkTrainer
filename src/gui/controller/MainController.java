package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.WorkSpace;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable, Observer {
   public static MainController instance;
   public static Stage stage;
   public static Stage loaderStage;
   public static Stage parameterStage;
   public static ProgressBar progress;

   static {
      try {
         loaderStage = new Stage();
         parameterStage = new Stage();
         Parent loader = FXMLLoader.load(MainController.class
               .getResource("../view/loader.fxml"));
         loaderStage.setTitle("Load Network");
         loaderStage.setScene(new Scene(loader));

         Parent parameterSetter = FXMLLoader.load(
               MainController.class.getResource("../view/parameters.fxml"));
         parameterStage.setTitle("Edit Parameters");
         parameterStage.setScene(new Scene(parameterSetter));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @FXML Pane interact;
   @FXML Pane examine;
   @FXML ProgressBar progressBar;

   public void initialize(URL location, ResourceBundle resources) {
      instance = this;
      progress = progressBar;
      WorkSpace.instance.setController(this);
   }

   public static void setStage(Stage newStage) {
      stage = newStage;
   }

   public static void openLoader() {
      loaderStage.showAndWait();
   }

   public void closeNetwork() {
      if (WorkSpace.instance.hasChanged()) {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Warning: Unsaved Changes");
         alert.setHeaderText("Closing will discard unsaved changes.  Continue?");
         alert.setContentText(null);
         Optional<ButtonType> result = alert.showAndWait();
         if (result.isPresent() && result.get() == ButtonType.OK) {
            WorkSpace.instance.closeNetwork();
         }
      } else {
         WorkSpace.instance.closeNetwork();
      }
   }

   public void saveNetwork() {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Confirm Save");
      alert.setHeaderText("Are you sure?");
      alert.setContentText(null);
      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
         LoaderController.instance.saveNetwork();
      }
   }

   public void exportNetwork() {
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

   public void editParameters() throws IOException {
      parameterStage.showAndWait();
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
         if (WorkSpace.instance.openNetwork()) {
            loaderStage.close();
            stage.show();
            stage.requestFocus();
         } else {
            loaderStage.show();
            stage.hide();
            loaderStage.requestFocus();
         }
      }
   }
}
