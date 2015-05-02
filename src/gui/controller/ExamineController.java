package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.WorkSpace;
import model.network.Memory;
import model.network.schema.Schema;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ExamineController implements Initializable {
   @FXML VBox memoryList;

   public void initialize(URL location, ResourceBundle resources) {
      WorkSpace.instance.examine.setController(this);
   }

   public void clearMemories() {
      memoryList.getChildren().clear();
   }

   public void setMemories(Schema schema, ArrayList<Memory> memories) {
      clearMemories();

      for (Memory exp : memories) {
         try {
            HBox memoryBox = new HBox();
            memoryBox.getChildren().add(schema.toFXNode(exp, 25, 25));
            memoryBox.getChildren().add(new Text(schema.translateOutput(exp.outputVector).toString()));
            memoryList.getChildren().add(memoryBox);
         } catch (Exception e) {
            System.out.println("Error loading memory!");
            e.printStackTrace();
         }
      }
   }

}
