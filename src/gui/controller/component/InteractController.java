package gui.controller.component;

import gui.controller.NetworkControllerStack;
import gui.controller.widget.ColorRandomizer;
import gui.controller.widget.GenericHandler;
import gui.controller.widget.GenericList;
import gui.controller.widget.MemoryBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.network.memory.Memory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class InteractController extends NetworkController implements Initializable {
   @FXML ListView listView;
   @FXML Rectangle colorBox;
   @FXML ColorPicker colorPicker;
   @FXML FlowPane prototypesPane;
   @FXML Button memoryButton;

   private GenericList<Object> classificationList;
   private MemoryBox prototypesBox;
   private ColorRandomizer randomizer;

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      prototypesBox = new MemoryBox(prototypesPane);
      classificationList = new GenericList<Object>(listView);
      randomizer = new ColorRandomizer(colorBox, colorPicker);

      // Set up classification list
      classificationList.addClickListener(new GenericHandler<Object>() {
         @Override
         public void handle(Object classification) {
            System.out.println(classification);
            correct();
         }
      });

      // Set up color randomizer
      randomizer.addListener(new GenericHandler<Color>() {
         @Override
         public void handle(Color item) {
            guess();
         }
      });
   }

   @Override
   public void display() {
      classificationList.clear();
      loadPrototypes();
      if (network != null) {
         classificationList.addAll(network.schema.getOutputClassifications());
         guess();
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
                        "../../view/memory.fxml"));
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Teaches the network using the randomizer and classification list.
    */
   public void correct() {
      Object classification = classificationList.getSelectedItem();
      Color color = randomizer.getValue();

      try {
         network.addMemory(new Memory(network.schema, color, classification));
      } catch (Exception e) {
         e.printStackTrace();
      }

      randomizer.randomize();
   }

   /**
    * Guesses the color in the color box.
    */
   private void guess() {
      if (network != null) {
         try {
            classificationList.setSelectedItem(network.query(randomizer.getValue()));
            listView.requestFocus();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Loads the network's memories.
    */
   private void loadPrototypes() {
      prototypesBox.clear();
      if (network != null) {
         try {
            // Populate prototypes box.
            prototypesBox.add(network.schema,
                  network.getPrototypes());
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
