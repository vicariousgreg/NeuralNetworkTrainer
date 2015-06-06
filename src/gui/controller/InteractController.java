package gui.controller;

import application.FileManager;
import gui.controller.widget.ClassificationHandler;
import gui.controller.widget.ClassificationList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.network.Network;
import model.network.memory.Memory;
import model.network.memory.MemoryModule;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class InteractController extends NetworkController implements Initializable {
   @FXML ListView listView;
   @FXML Rectangle colorBox;
   @FXML ColorPicker colorPicker;
   @FXML FlowPane shortTermMemoryBox;
   @FXML Button memoryButton;

   /** Random generator for color generation. */
   private Random rand;

   private ClassificationList classificationList;

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      this.rand = new Random();

      classificationList = new ClassificationList(listView);
      classificationList.addClickListener(new ClassificationHandler() {
         @Override
         public void handle(String classification) {
            selectClassification(classification);
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
      try {
         loadClassifications();
         loadMemory();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void setNetwork(Network network) {
      if (network != super.getNetwork()) {
         super.setNetwork(network);

         // Null check
         if (network != null) {
            super.setNetwork(network);
            System.out.println("Loaded network: " + FileManager.instance.getName(network));

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
   }

   /**
    * Loads the network's schema classifications.
    */
   private void loadClassifications() {
      classificationList.clear();
      Object[] classifications = network.schema.getOutputClassifications();
      for (int i = 0; i < classifications.length; ++i)
         classificationList.add((String) classifications[i]);
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
    * Commits the guessed classification to memory.
    */
   public void correct() {
      selectClassification(classificationList.getSelectedClassification());
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
         classificationList.setSelectedClassification(answer);
         listView.requestFocus();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Loads the network's memories.
    * @throws Exception
    */
   private void loadMemory() throws Exception {
      if (network != null) {
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
}
