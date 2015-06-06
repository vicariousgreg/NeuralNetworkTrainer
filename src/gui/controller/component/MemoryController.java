package gui.controller.component;

import application.DialogFactory;
import gui.controller.widget.GenericHandler;
import gui.controller.widget.GenericList;
import gui.controller.widget.MemoryBox;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import model.network.memory.MemoryModule;

import java.net.URL;
import java.util.*;

public class MemoryController extends NetworkController implements Initializable {
   @FXML ListView listView;
   @FXML FlowPane shortTermPane;
   @FXML FlowPane longTermPane;

   private MemoryBox shortTermMemoryBox;
   private MemoryBox longTermMemoryBox;
   private GenericList<Object> classificationList;

   /**
    * Initialization.
    * Sets up listeners for GUI.
    */
   public void initialize(URL location, ResourceBundle resources) {
      shortTermMemoryBox = new MemoryBox(shortTermPane);
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
         classificationList.setSelectedItem(GenericList.kAll);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Loads the network's memories for the given classification.
    * @param classification classification of memories to load
    * @throws Exception
    */
   private void loadMemory(Object classification) {
      MemoryModule mem = network.getMemoryModule();

      try {
         // Populate short term memory box.
         shortTermMemoryBox.clear();
         if (classification == GenericList.kAll)
            shortTermMemoryBox.addAll(network.schema, mem.getShortTermMemory());
         else
            shortTermMemoryBox.add(network.schema, mem.getShortTermMemory(), classification);

         // Populate long term memory box.
         longTermMemoryBox.clear();
         if (classification == GenericList.kAll)
            longTermMemoryBox.addAll(network.schema, mem.getLongTermMemory());
         else
            longTermMemoryBox.add(network.schema, mem.getLongTermMemory(), classification);
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
      if (!DialogFactory.displaySaveDialog(
            network.getMemoryModule(),
            (Stage) listView.getScene().getWindow())) {
         DialogFactory.displayErrorDialog("Could not save memories!");
      }
   }

   /**
    * Imports the network's memory from a file of the user's choice.
    */
   public void importMemory() {
      MemoryModule mem = (MemoryModule) DialogFactory.displayLoadDialog(
            (Stage) listView.getScene().getWindow());
      if (mem != null) {
         network.setMemoryModule(mem);
         display();
      } else {
         DialogFactory.displayErrorDialog("Could not load memories!");
      }
   }
}
