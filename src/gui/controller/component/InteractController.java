package gui.controller.component;

import gui.controller.NetworkControllerStack;
import gui.controller.widget.GenericHandler;
import gui.controller.widget.GenericList;
import gui.controller.widget.MemoryBox;
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
   @FXML FlowPane shortTermMemoryPane;
   @FXML Button memoryButton;

   /** Random generator for color generation. */
   private Random rand;

   private GenericList<Object> classificationList;
   private MemoryBox shortTermMemoryBox;

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      rand = new Random();
      shortTermMemoryBox = new MemoryBox(shortTermMemoryPane);

      classificationList = new GenericList<Object>(listView);
      classificationList.addClickListener(new GenericHandler<Object>() {
         @Override
         public void handle(Object classification) {
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
      boolean newNetwork = (network != super.getNetwork());
      super.setNetwork(network);

      if (network != null) {
         if (newNetwork) {
            super.setNetwork(network);
            loadClassifications();

            // Run randomizer
            randomize();
         }

         try {
            loadMemory();
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         classificationList.clear();
         shortTermMemoryBox.clear();
      }
   }

   /**
    * Commits the guessed classification to memory.
    */
   public void correct() {
      selectClassification(classificationList.getSelectedItem());
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
   private void selectClassification(Object classification) {
      try {
         network.addMemory(colorBox.getFill(), classification);
         shortTermMemoryBox.add(
               new Memory(network.schema, colorBox.getFill(), classification),
               network.schema);
      } catch (Exception e) {
         e.printStackTrace();
      }

      randomize();
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
         classificationList.setSelectedItem(answer);
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
         shortTermMemoryBox.clear();
         MemoryModule mem = network.getMemoryModule();
         Map<Object, List<Memory>> shortTermMemory = mem.getShortTermMemory();

         for (Object key : shortTermMemory.keySet()) {
            for (Memory memory : shortTermMemory.get(key)) {
               shortTermMemoryBox.add(memory, network.schema);
            }
         }
      }
   }
}
