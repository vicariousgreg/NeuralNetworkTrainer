package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import network.ColorInput;
import network.Network;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class InteractController implements Initializable {
   private Network network;

   @FXML ColorPicker colorPicker;
   @FXML Rectangle rectangle;
   @FXML Text answer;

   private Random rand = new Random();

   public void initialize(URL location, ResourceBundle resources) { }

   public void setNetwork(Network network) {
      this.network = network;
   }

   public void train() {
      network.train();
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

      String guess = "";
      try {
         guess = (String) network.query(ColorInput.convertFXColor(color));
         System.out.println("Guess: " + guess);
      } catch (Exception e) {
         System.out.println("Invalid network input!");
         e.printStackTrace();
      }

      System.out.println(guess);
      answer.setText(guess);
   }

   public void clickCorrect() {
      commit(answer.getText());
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
      Color color = (Color) rectangle.getFill();
      double red = color.getRed();
      double green = color.getGreen();
      double blue = color.getBlue();

      System.out.printf("Color: %.3f %.3f %.3f is %s\n", red, blue, green, answer);

      try {
         network.addExperience(ColorInput.convertFXColor(color), answer);
      } catch (Exception e) {
         System.out.println("Experience does not match network's schema!");
         e.printStackTrace();
      }

      randomize();
   }
}
