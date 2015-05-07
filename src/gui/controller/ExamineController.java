package gui.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import model.WorkSpace;
import model.network.memory.Memory;
import model.network.memory.MemoryModule;
import model.network.schema.Schema;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ExamineController implements Initializable {
   @FXML VBox shortTermBox;
   @FXML VBox longTermBox;

   public void initialize(URL location, ResourceBundle resources) {
      WorkSpace.instance.examine.setController(this);
   }

   public void consolidateMemory() {
      Task<Void> task = new Task<Void>() {
         @Override
         public Void call() {
            System.out.println("Rebuilding network...");
            WorkSpace.instance.consolidateMemory();
            return null;
         }
      };
      MainController.progress.progressProperty().bind(task.progressProperty());
      new Thread(task).start();
   }

   public void saveMemory() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showSaveDialog(MainController.stage);
      if (file != null) {
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.saveMemory(file);
               return null;
            }
         };
         MainController.progress.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void loadMemory() {
      FileChooser fileChooser = new FileChooser();
      final File file = fileChooser.showOpenDialog(MainController.stage);
      if (file != null) {
         Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
               WorkSpace.instance.loadMemory(file);
               return null;
            }
         };
         MainController.progress.progressProperty().bind(task.progressProperty());
         new Thread(task).start();
      }
   }

   public void wipeMemory() {
      Task<Void> task = new Task<Void>() {
         @Override
         public Void call() {
            WorkSpace.instance.wipeMemory();
            return null;
         }
      };
      MainController.progress.progressProperty().bind(task.progressProperty());
      new Thread(task).start();
   }

   public void clearMemory() {
      if (shortTermBox.getChildren() != null)
         shortTermBox.getChildren().clear();
      if (longTermBox.getChildren() != null)
         longTermBox.getChildren().clear();
   }

   public void setMemory(MemoryModule memoryModule) {
      clearMemory();
      Schema schema = memoryModule.schema;

      Object[] classifications = schema.getOutputClassifications();
      Map<Object, List<Memory>> shortTermMemory = memoryModule.getShortTermMemory();
      Map<Object, List<Memory>> longTermMemory = memoryModule.getLongTermMemory();

      for (int i = 0; i < classifications.length; ++i) {
         String classification = classifications[i].toString();

         Label shortClassificationLabel = new Label(classification);
         Label longClassificationLabel = new Label(classification);
         shortClassificationLabel.setPadding(new Insets(10, 0, 0, 0));
         longClassificationLabel.setPadding(new Insets(10, 0, 0, 0));

         FlowPane shortTermMemoryPane = new FlowPane();
         FlowPane longTermMemoryPane= new FlowPane();

         for (Memory memory : shortTermMemory.get(classification)) {
            try {
               shortTermMemoryPane.getChildren().add(schema.toFXNode(memory, 25, 25));
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
         shortTermBox.getChildren().add(shortClassificationLabel);
         shortTermBox.getChildren().add(shortTermMemoryPane);

         for (Memory memory : longTermMemory.get(classification)) {
            try {
               longTermMemoryPane.getChildren().add(schema.toFXNode(memory, 25, 25));
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
         longTermBox.getChildren().add(longClassificationLabel);
         longTermBox.getChildren().add(longTermMemoryPane);
      }
   }
}
