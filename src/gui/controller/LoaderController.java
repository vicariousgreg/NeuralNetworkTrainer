package gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.WorkSpace;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoaderController implements Initializable {
   private static final String kDataPath = "src/gui/controller/data/";

   public static LoaderController instance;
   private static File openFile;
   private Stage stage;
   private Stage saveStage;

   private Parent saveDialog;

   @FXML ListView networkList;

   public void initialize(URL location, ResourceBundle resources) {
      instance = this;
      this.stage = new Stage();
      this.saveStage = new Stage();
      networkList.setOnMouseClicked(new EventHandler<MouseEvent>() {

         @Override
         public void handle(MouseEvent click) {
            if (click.getClickCount() == 2) {
               loadNetwork((String) networkList.getSelectionModel()
                     .getSelectedItem());
            }
         }
      });

      try {
         ObservableList data = FXCollections.observableArrayList();

         File dataFile = new File(kDataPath);
         File[] networks = dataFile.listFiles();

         for (File file : networks) {
            data.add(file.getName());
         }

         networkList.setItems(data);

         FXMLLoader loader = new FXMLLoader(
            getClass().getResource("../view/saver.fxml"));
         saveDialog = loader.load();
         final TextField nameField = (TextField)loader.getNamespace().get("nameField");
         Button saveButton = (Button)loader.getNamespace().get("saveButton");
         saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               confirmSave(nameField.getText());
            }
         });
         Button cancelButton = (Button)loader.getNamespace().get("cancelButton");
         cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               saveStage.close();
            }
         });

         saveStage.setTitle("Save network...");
         saveStage.setScene(new Scene(saveDialog));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void newNetwork() {
      openFile = null;
      WorkSpace.instance.newNetwork();
   }

   public void importNetwork() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(stage);
      try {
         File override = new File(kDataPath + file.getName());
         if (override != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Overwrite");
            alert.setHeaderText("A network with the same name already exists.  Overwrite?");
            alert.setContentText(null);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
               Files.copy(Paths.get(file.getAbsolutePath()),
                     Paths.get(override.getAbsolutePath()),
                     StandardCopyOption.REPLACE_EXISTING);
               loadNetwork(override.getName());
            }
         } else {
            Files.copy(Paths.get(file.getAbsolutePath()),
                  Paths.get("src/gui/controller/data/" + file.getName()),
                  StandardCopyOption.REPLACE_EXISTING);
            networkList.getItems().add(override.getName());
            loadNetwork(override.getName());
         }
      } catch (Exception e) {
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Error!");
         alert.setHeaderText("Error importing network!");
         alert.setContentText(null);
         alert.showAndWait();
      }
   }

   public void saveNetwork() {
      if (openFile == null) {
         saveStage.showAndWait();
      } else {
         confirmSave(openFile.getName());
      }
   }

   public void confirmSave(String name) {
      try {
         saveStage.close();
         File newFile = new File(kDataPath + name);
         newFile.delete();
         WorkSpace.instance.saveNetwork(newFile);
         openFile = newFile;
      } catch (Exception e) {
         e.printStackTrace();
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Error!");
         alert.setHeaderText("Error saving network!");
         alert.setContentText(null);
         alert.showAndWait();
      }
   }

   public void loadNetwork(String name) {
      openFile = new File(kDataPath + name);
      if (openFile != null) {
         try {
            WorkSpace.instance.loadNetwork(openFile);
         } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error loading network!");
            alert.setContentText(null);
            alert.showAndWait();
         }
      }
   }
}
