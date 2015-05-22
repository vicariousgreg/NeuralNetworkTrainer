package gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.network.Network;
import model.network.memory.Memory;
import model.network.memory.MemoryModule;

import java.net.URL;
import java.util.*;

public class MemoryController extends NetworkController implements Initializable {
   @FXML ListView classificationList;
   @FXML FlowPane shortTermMemoryBox;
   @FXML FlowPane longTermMemoryBox;

   /** All Classification item. */
   private static final String kAll = "=== ALL ===";

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      // Set up event handler for classification list.
      classificationList.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent click) {
            // Load selection
            String selection = (String)
                  classificationList.getSelectionModel().getSelectedItem();
            selectClassification(selection);
         }
      });
   }

   public void display() {
      try {
         loadClassifications();
         selectClassification(kAll);
         classificationList.getSelectionModel().select(kAll);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void loadClassifications() throws Exception {
      // Populate classification list.
      ObservableList data = FXCollections.observableArrayList();
      Object[] classifications = network.schema.getOutputClassifications();
      data.add(kAll);
      for (int i = 0; i < classifications.length; ++i) {
         data.add(classifications[i]);
      }
      classificationList.setItems(data);
   }

   private void loadMemory(String classification) throws Exception {
      // Populate short term memory box.
      shortTermMemoryBox.getChildren().clear();
      MemoryModule mem = network.getMemoryModule();
      Map<Object, List<Memory>> shortTermMemory = mem.getShortTermMemory();

      for (Object key : shortTermMemory.keySet()) {
         if (classification.equals(kAll) || key.equals(classification)) {
            for (Memory memory : shortTermMemory.get(key)) {
               shortTermMemoryBox.getChildren().add(
                     network.schema.toFXNode(memory, 25, 25));
            }
         }
      }

      // Populate long term memory box.
      longTermMemoryBox.getChildren().clear();
      Map<Object, List<Memory>> longTermMemory = mem.getLongTermMemory();

      for (Object key : longTermMemory.keySet()) {
         if (classification.equals(kAll) || key.equals(classification)) {
            for (Memory memory : longTermMemory.get(key)) {
               longTermMemoryBox.getChildren().add(
                     network.schema.toFXNode(memory, 25, 25));
            }
         }
      }
   }

   /**
    * Selects a classification, teaching the network.
    * @param classification selected classification
    */
   private void selectClassification(String classification) {
      System.out.println("Selected " + classification);

      try {
         loadMemory(classification);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
