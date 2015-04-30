package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import network.ColorSchema;
import network.Network;
import network.Experience;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {
   public static final Network network = new Network(new ColorSchema(), new int[] {3, 5, 6});
   public static final ArrayList<Experience> tests = new ArrayList<Experience>();

   @FXML Pane train;
   @FXML Pane interact;
   @FXML Pane examine;

   public void initialize(URL location, ResourceBundle resources) {
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(false);
   }

   public void onTrainClick() {
      train.setVisible(true);
      interact.setVisible(false);
      examine.setVisible(false);
      System.out.println("Train");
   }

   public void onInteractClick() {
      teach();
      train.setVisible(false);
      interact.setVisible(true);
      examine.setVisible(false);
      System.out.println("Interact");
   }

   public void onExamineClick() {
      train.setVisible(false);
      interact.setVisible(false);
      examine.setVisible(true);
      System.out.println("Examine");
   }

   public static void addTestCase(Color color, String result) {
      try {
         network.addExperience(color, result);
      } catch (Exception e) {
         System.out.println("Experience does not match network's schema!");
      }
   }

   public static String guessColor(Color color) {
      String guess = "";
      try {
         guess = network.query(color);
         System.out.println("Guess: " + guess);
      } catch (Exception e) {
         System.out.println("Invalid network input!");
      }
      return guess;
   }

   public static void teach() {
      network.train();
   }
}
