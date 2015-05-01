package gui;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import network.Network;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class TrainController implements Initializable {
   private Network network;

   @FXML ColorPicker colorPicker;
   @FXML Rectangle colorBox;
   @FXML Button redbutton;
   @FXML Button orangebutton;
   @FXML Button yellowbutton;
   @FXML Button greenbutton;
   @FXML Button bluebutton;
   @FXML Button purplebutton;

   private Random rand = new Random();

   public void initialize(URL location, ResourceBundle resources) {
      randomizeColor();
   }

   public void setNetwork(Network network) {
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

   public void clickRed() {
      commit("Red");
   }

   public void clickOrange() {
      commit("Orange");
   }

   public void clickYellow() {
      commit("Yellow");
   }

   public void clickGreen() {
      commit("Green");
   }

   public void clickBlue() {
      commit("Blue");
   }

   public void clickPurple() {
      commit("Purple");
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
