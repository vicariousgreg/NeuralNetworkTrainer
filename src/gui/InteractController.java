package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class InteractController implements Initializable {
   @FXML ColorPicker colorPicker;
   @FXML Rectangle rectangle;
   @FXML Text answer;

   private Random rand = new Random();

   public void initialize(URL location, ResourceBundle resources) {
      randomize();
   }

   public void setColor() {
      Color color = colorPicker.getValue();
      rectangle.setFill(color);
      guessColor();
   }

   public void randomize() {
      double r = rand.nextDouble();
      double g = rand.nextDouble();
      double b = rand.nextDouble();
      Color color = new Color(r, g, b, 1.0);
      colorPicker.setValue(color);
      rectangle.setFill(color);
      guessColor();
   }

   public void guessColor() {
      System.out.println("Guess color");
      answer.setText(colorPicker.getValue().toString());
      Color color = colorPicker.getValue();
      double red = color.getRed();
      double green = color.getGreen();
      double blue = color.getBlue();

      String guess = MainController.guessColor(red, green, blue);
      System.out.println(guess);
      answer.setText(guess);
   }

   public void clickCorrect() {
      String[] colorList = new String[] {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Purple"
      };
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
      Color color = (Color) rectangle.getFill();
      double red = color.getRed();
      double green = color.getGreen();
      double blue = color.getBlue();

      System.out.printf("Color: %.3f %.3f %.3f is %s\n", red, blue, green, answer);
      MainController.addTestCase(red, green, blue, index);
      randomize();
   }
}
