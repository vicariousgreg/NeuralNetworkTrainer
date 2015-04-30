package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import network.Network;
import network.Experience;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {
   public static final Network network = new Network(new int[] {3, 3, 6});
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

   public static void addTestCase(double r, double g, double b, int colorIndex) {
      double[] answer = new double[6];
      answer[colorIndex] = 1.0;

      network.addExperience(new Experience(new double[]{r, g, b}, answer));
   }

   public static String guessColor(double r, double g, double b) {
      double[] result = network.fire(new double[]{r, g, b});
      System.out.println("Guess: " + Arrays.toString(result));

      double max = result[0];
      int maxIndex = 0;
      for (int i = 1; i < result.length; ++i) {
         if (result[i] > max) {
            maxIndex = i;
         }
      }

      String[] colorList = new String[] {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Purple"
      };

      return colorList[maxIndex];
   }

   public static void teach() {
      network.train();
   }
}
