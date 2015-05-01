package gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import network.Network;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class TrainController implements Initializable {
   private Network network;

   @FXML ColorPicker colorPicker;
   @FXML FlowPane buttonPane;
   @FXML Rectangle colorBox;

   private Random rand = new Random();

   public void initialize(URL location, ResourceBundle resources) {
      buttonPane.setOrientation(Orientation.VERTICAL);
      randomizeColor();
   }

   public void setNetwork(Network network) {
      buttonPane.getChildren().clear();
      Object[] classifications = network.schema.getOutputClassifications();

      for (int i = 0; i < classifications.length; ++i) {
         String name = classifications[i].toString();
         final Button button = new Button(name);
         button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
               commit(button.getText());
            }
         });
         buttonPane.getChildren().add(button);
      }
      this.network = network;
   }

   public void setColor() {
      Color color = colorPicker.getValue();
      colorBox.setFill(color);
   }

   public void randomizeColor() {
      colorBox.setFill(new Color(
         rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1));
   }

   public void commit(String answer) {
      Color color = (Color) colorBox.getFill();
      float red = (float) color.getRed();
      float green = (float) color.getGreen();
      float blue = (float) color.getBlue();

      System.out.printf("Color: %.3f %.3f %.3f is %s\n", red, blue, green, answer);

      try {
         network.addExperience(color, answer);
      } catch (Exception e) {
         System.out.println("Experience does not match network's schema!");
      }

      randomizeColor();
   }
}
