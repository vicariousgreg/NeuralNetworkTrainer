package gui.controller;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Interact;
import model.WorkSpace;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class InteractController implements Initializable {
   private Interact interact;

   @FXML Pane pane;
   @FXML FlowPane buttonPane;
   @FXML ProgressIndicator progress;
   @FXML ColorPicker colorPicker;
   @FXML Rectangle rectangle;
   @FXML Text answer;

   private Random rand = new Random();

   public void initialize(URL location, ResourceBundle resources) {
      interact = WorkSpace.instance.interact;
      WorkSpace.instance.interact.setController(this);
      progress.setVisible(false);
      buttonPane.setOrientation(Orientation.VERTICAL);
   }

   public void clearClassifications() {
      buttonPane.getChildren().clear();
   }

   public void setClassifications(Object[] classifications) {
      clearClassifications();

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
      randomize();
      guessColor();
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
   }

   public void guessColor() {
      System.out.println("Guess color");
      Color color = colorPicker.getValue();

      String guess = "";
      try {
         guess = interact.guess(color).toString();
         System.out.println("Guess: " + guess);
      } catch (Exception e) {
         System.out.println("Invalid network input!");
         e.printStackTrace();
      }

      System.out.println(guess);
      answer.setText(guess);
   }

   public void consolidateMemories() {
      // Rebuild network on background thread.
      Task<Void> task = new Task<Void>() {
         @Override
         public Void call() {
            System.out.println("Rebuilding network...");
            progress.setVisible(true);
            WorkSpace.instance.consolidateMemories();
            progress.setVisible(false);
            guessColor();
            return null;
         }
      };
      progress.progressProperty().bind(task.progressProperty());
      new Thread(task).start();
   }

   public void clickSkip() {
      randomize();
      guessColor();
   }

   public void clickCorrect() {
      commit(answer.getText());
   }

   public void commit(String answer) {
      Color color = (Color) rectangle.getFill();
      float red = (float) color.getRed();
      float green = (float) color.getGreen();
      float blue = (float) color.getBlue();

      System.out.printf("Color: %.3f %.3f %.3f is %s\n", red, blue, green, answer);

      try {
         interact.addMemory(color, answer);
      } catch (Exception e) {
         System.out.println("Experience does not match network's schema!");
      }
      randomize();
      guessColor();
   }
}
