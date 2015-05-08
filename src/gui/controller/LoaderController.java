package gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
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
   private static File openFile;
   private Stage stage;

   @FXML ListView networkList;

   public void initialize(URL location, ResourceBundle resources) {
      this.stage = new Stage();
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

         File dataFile = new File("src/gui/controller/data");
         File[] networks = dataFile.listFiles();

         for (File file : networks) {
            data.add(file.getName());
         }

         networkList.setItems(data);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void newNetwork() {
      WorkSpace.instance.newNetwork();
   }

   public void importNetwork() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(stage);
      try {
         File override = new File("src/gui/controller/data/" + file.getName());
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

   public void loadNetwork(String name) {
      openFile = new File("src/gui/controller/data/" + name);
      if (openFile != null) {
         try {
            WorkSpace.instance.loadNetwork(openFile);
            Stage stage = (Stage) networkList.getScene().getWindow();
            stage.close();
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
