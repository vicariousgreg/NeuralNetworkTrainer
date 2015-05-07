package gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.WorkSpace;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoaderController implements Initializable {
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
      Stage stage = (Stage) networkList.getScene().getWindow();
      stage.close();
   }

   public void loadNetwork(String name) {
      final File file = new File("src/gui/controller/data/" + name);
      if (file != null) {
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.loadNetwork(file);
               Stage stage = (Stage) networkList.getScene().getWindow();
               stage.close();
               return null;
            }
         };
         new Thread(task).start();
      }
   }
}
