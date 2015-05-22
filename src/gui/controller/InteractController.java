package gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.WorkSpace;
import model.network.memory.Memory;
import model.network.memory.MemoryModule;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class InteractController extends NetworkController implements Initializable, Observer {
   @FXML ListView networkList;
   @FXML ListView classificationList;
   @FXML Rectangle colorBox;
   @FXML ColorPicker colorPicker;
   @FXML FlowPane shortTermMemoryBox;
   @FXML Button memoryButton;

   /** Random generator for color generation. */
   private Random rand;

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      this.rand = new Random();
      WorkSpace.instance.addObserver(this);

      // Set up event handler for network list.
      networkList.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent click) {
            // Load selection
            String selection = (String)
                  networkList.getSelectionModel().getSelectedItem();
            selectNetwork(selection);
         }
      });

      // Set up event handler for classification list.
      classificationList.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent click) {
            // Load selection
            String selection = (String)
                  classificationList.getSelectionModel().getSelectedItem();
            classificationList.getSelectionModel().clearSelection();
            selectClassification(selection);
         }
      });

      // Set up event handler for color box
      colorBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent click) {
            randomize();
         }
      });
   }

   @Override
   public void display() {
      loadNetworks();
   }

   /**
    * Loads networks from the WorkSpace.
    */
   private void loadNetworks() {
      try {
         ObservableList data = FXCollections.observableArrayList();

         List<String> names = WorkSpace.instance.getNetworkNames();
         for (String name : names) {
            data.add(name);
         }
         networkList.setItems(data);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Selects a network by name.
    * Queries the WorkSpace for the Network.
    * @param networkName name of selected network
    */
   private void selectNetwork(String networkName) {
      // Query workspace for network
      //   Set to selected
      network = WorkSpace.instance.getNetwork(networkName);

      // Null check
      if (network != null) {
         System.out.println("Loaded network: " + networkName);

         try {
            loadClassifications();
            loadMemory();
         } catch (Exception e) {
            e.printStackTrace();
         }

         // Run randomizer
         randomize();
      }
   }

   private void loadClassifications() throws Exception {
      // Populate classification list.
      ObservableList data = FXCollections.observableArrayList();
      Object[] classifications = network.schema.getOutputClassifications();
      for (int i = 0; i < classifications.length; ++i) {
         data.add(classifications[i]);
      }
      classificationList.setItems(data);
   }

   private void loadMemory() throws Exception {
      // Populate memory box.
      shortTermMemoryBox.getChildren().clear();
      MemoryModule mem = network.getMemoryModule();
      Map<Object, List<Memory>> shortTermMemory = mem.getShortTermMemory();

      for (Object key : shortTermMemory.keySet()) {
         for (Memory memory : shortTermMemory.get(key)) {
            shortTermMemoryBox.getChildren().add(
                  network.schema.toFXNode(memory, 25, 25));
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
         network.addMemory(colorBox.getFill(), classification);
         loadMemory();
      } catch (Exception e) {
         e.printStackTrace();
      }

      randomize();
   }

   /**
    * Sets the color of the box according to the color picker.
    */
   public void setColor() {
      Color color = colorPicker.getValue();
      colorBox.setFill(color);
      guess();
   }

   /**
    * Randomizes the color box.
    */
   private void randomize() {
      double r = rand.nextDouble();
      double g = rand.nextDouble();
      double b = rand.nextDouble();
      Color color = new Color(r, g, b, 1.0);
      colorPicker.setValue(color);
      colorBox.setFill(color);
      guess();
   }

   /**
    * Guesses the color in the color box.
    */
   private void guess() {
      try {
         String answer = (String) network.query(colorBox.getFill());
         classificationList.getSelectionModel().select(answer);
         classificationList.requestFocus();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Loads up the memory screen.
    */
   public void viewMemory() {
      if (network != null) {
         try {
            NetworkControllerStack.instance.push(
                  getClass().getResource(
                        "../view/memory.fxml"));
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void update(Observable o, Object arg) {
      loadNetworks();
   }
}
