package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import network.Experience;
import network.Network;
import network.Schema;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class ExamineController implements Initializable {
   @FXML VBox memoryList;

   public void initialize(URL location, ResourceBundle resources) { }

   public void extractMemories(Network network) {
      memoryList.getChildren().clear();
      ArrayList<Experience> memory = network.getMemory();
      Schema schema = network.schema;

      for (Experience exp : memory) {
         try {
            HBox memoryBox = new HBox();
            memoryBox.getChildren().add(exp.toFXNode(25, 25));
            memoryBox.getChildren().add(new Text(exp.output.toString()));
            memoryList.getChildren().add(memoryBox);
         } catch (Exception e) {
            System.out.println("Error loading memory!");
            e.printStackTrace();
         }
      }
   }
}
