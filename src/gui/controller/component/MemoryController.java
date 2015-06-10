package gui.controller.component;

import gui.controller.DialogFactory;
import application.FileManager;
import gui.controller.widget.GenericHandler;
import gui.controller.widget.GenericList;
import gui.controller.widget.MemoryBox;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import model.network.memory.Memory;

import java.net.URL;
import java.util.*;

public class MemoryController extends NetworkController implements Initializable {
   @FXML ListView listView;
   @FXML FlowPane prototypesPane;
   @FXML FlowPane longTermPane;

   private MemoryBox prototypesBox;
   private MemoryBox longTermMemoryBox;
   private GenericList<Object> classificationList;

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      prototypesBox = new MemoryBox(prototypesPane);
      longTermMemoryBox = new MemoryBox(longTermPane);
      classificationList = new GenericList<Object>(listView);

      // Set up event handler for classification list.
      classificationList.addClickListener(new GenericHandler<Object>() {
         @Override
         public void handle(Object item) {
            loadMemory(item);
         }
      });

      classificationList.addAllItem(new GenericHandler<Object>() {
         @Override
         public void handle(Object item) {
            loadMemory(GenericList.kAll);
         }
      });
   }

   /**
    * Loads up the network data.
    */
   public void display() {
      try {
         // Populate classification list.
         classificationList.clear();
         classificationList.addAll(network.schema.getOutputClassifications());
         loadMemory(GenericList.kAll);
         loadPrototypes();
         classificationList.setSelectedItem(GenericList.kAll);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Loads the network's memories for the given classification.
    * @param classification classification of memories to load
    */
   private void loadMemory(Object classification) {
      try {
         // Populate long term memory box.
         longTermMemoryBox.clear();
         if (classification == GenericList.kAll)
            longTermMemoryBox.add(network.schema, network.getAllMemories());
         else
            longTermMemoryBox.add(network.schema, network.getMemories(classification));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Loads the network's prototypes.
    */
   private void loadPrototypes() {
      try {
         // Populate prototypes box.
         prototypesBox.clear();
         prototypesBox.add(network.schema, network.getPrototypes());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Consolidates the network's memories.
    */
   public void consolidate() {
      DialogFactory.displayTaskProgressDialog("Consolidating memories...",
            new Task<Void>() {
               @Override
               public Void call() {
                  System.out.println("Rebuilding network...");
                  network.train();
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        display();
                     }
                  });
                  return null;
               }
            });
   }

   /**
    * Exports the network's memory to a file of the user's choice.
    */
   public void exportMemory() {
      try {
         FileManager.saveMemories(
               DialogFactory.displayTextDialog("Enter memory set name:"),
               network.getAllMemories());
      } catch (Exception e) {
         DialogFactory.displayErrorDialog("Could not save memories!");
      }
   }

   /**
    * Imports the network's memory from a file of the user's choice.
    */
   public void importMemory() {
      try {
         String setName = (String) DialogFactory.displayEnumerationDialog(
               "Select a memory set:", FileManager.getMemoryList());

         if (setName != null) {
            List<Memory> newMemories = FileManager.loadMemories(setName);
            network.addMemories(newMemories);
            display();
         }
      } catch (Exception e) {
         DialogFactory.displayErrorDialog("Could not load memories!");
      }
   }
}
