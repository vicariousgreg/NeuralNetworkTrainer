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
   @FXML Button guess;
   @FXML Text answer;

   private Random rand = new Random();

   public void initialize(URL location, ResourceBundle resources) {
   }

   public void setColor() {
      Color color = colorPicker.getValue();
      rectangle.setFill(color);
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
}
