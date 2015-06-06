package gui.controller.component;

import gui.controller.NetworkControllerStack;
import gui.controller.widget.GenericHandler;
import gui.controller.widget.GenericList;
import gui.controller.widget.MemoryBox;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import model.network.memory.MemoryModule;

import java.io.*;
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
            selectClassification(item);
         }
      });

      classificationList.addAllItem(new GenericHandler<Object>() {
         @Override
         public void handle(Object item) {
            selectClassification(GenericList.kAll);
         }
      });
   }

   /**
    * Loads up the network data.
    */
   public void display() {
      try {
         loadClassifications();
         selectClassification(GenericList.kAll);
         classificationList.setSelectedItem(GenericList.kAll);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Loads the network's schema classifications.
    * @throws Exception
    */
   private void loadClassifications() throws Exception {
      // Populate classification list.
      classificationList.clear();
      Object[] classifications = network.schema.getOutputClassifications();
      for (int i = 0; i < classifications.length; ++i) {
         classificationList.add(classifications[i]);
      }
   }

   /**
    * Loads the network's memories for the given classification.
    * @param classification classification of memories to load
    * @throws Exception
    */
   private void loadMemory(Object classification) throws Exception {
      MemoryModule mem = network.getMemoryModule();

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
   }

   /**
    * Selects a classification, teaching the network.
    * @param classification selected classification
    */
   private void selectClassification(Object classification) {
      try {
         loadMemory(classification);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Consolidates the network's memories.
    */
   public void consolidate() {
      System.out.println("Consolidate");
      NetworkControllerStack.instance.postTask(new Task<Void>() {
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
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(NetworkControllerStack.instance.getStage());
      if (file != null) {
         NetworkControllerStack.instance.postTask(new Task<Void>() {
            @Override
            public Void call() {
               try {
                  FileOutputStream fos = new FileOutputStream(file);
                  ObjectOutputStream out = new ObjectOutputStream(fos);
                  out.writeObject(network.getMemoryModule());
                  out.close();
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        display();
                     }
                  });
               } catch (Exception e) {
                  e.printStackTrace();
               }

               return null;
            }
         });
      }
   }

   /**
    * Imports the network's memory from a file of the user's choice.
    */
   public void importMemory() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(NetworkControllerStack.instance.getStage());
      if (file != null) {
         NetworkControllerStack.instance.postTask(new Task<Void>() {
            @Override
            public Void call() {
               try {
                  FileInputStream fin = new FileInputStream(file);
                  ObjectInputStream ois = new ObjectInputStream(fin);
                  network.setMemoryModule(((MemoryModule) ois.readObject()));
                  ois.close();
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        display();
                     }
                  });
               } catch (Exception e) {
                  e.printStackTrace();
               }

               return null;
            }
         });
      }
   }
}
