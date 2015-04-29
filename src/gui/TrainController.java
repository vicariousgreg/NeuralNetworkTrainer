package gui;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class TrainController implements Initializable {
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

   public void setColor() {
      Color color = colorPicker.getValue();
      colorBox.setFill(color);
   }

   public void randomizeColor() {
      colorBox.setFill(new Color(
         rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1));
   }

   public void clickRed() {
      commit("Red", 0);
   }

   public void clickOrange() {
      commit("Orange", 1);
   }

   public void clickYellow() {
      commit("Yellow", 2);
   }

   public void clickGreen() {
      commit("Green", 3);
   }

   public void clickBlue() {
      commit("Blue", 4);
   }

   public void clickPurple() {
      commit("Purple", 5);
   }

   public void commit(String answer, int index) {
      Color color = (Color) colorBox.getFill();
      double red = color.getRed();
      double green = color.getGreen();
      double blue = color.getBlue();

      System.out.printf("Color: %.3f %.3f %.3f is %s\n", red, blue, green, answer);
      MainController.addTestCase(red, green, blue, index);
      randomizeColor();
   }
}
