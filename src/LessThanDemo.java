import javafx.scene.Node;
import model.network.memory.Memory;
import model.network.Network;
import model.network.Parameters;
import model.network.schema.Schema;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class LessThanDemo {
   /** The number of standard tests generated in a suite. */
   private static final int kNumTests = 90;
   /** The number of fringe tests generated in a suite. */
   private static final int kNumFringeTests = 5;
   /** The offset for fringe tests. */
   private static final double kFringeFactor = 0.01;

   /**
    * Main driver for demo of less-than neural network.
    */
   public static void main(String[] args) {
      ////////////////////
      /* Initialization */
      ////////////////////

      // Create schema.
      Schema schema =  new Schema(new Class[0], 2, new String[] {"less", "greater"}) {
         @Override
         public Object recreateInput(double[] inputVector) {
            return inputVector;
         }

         @Override
         public double[] encode(Object in) throws Exception {
            return new double[0];
         }

         @Override
         public Node toFXNode(Memory exp, double width, double height) throws Exception {
            return null;
         }
      };

      // Create the network.
      Network network = new Network(schema);
      Parameters params = network.getParameters();
      params.hiddenLayerDepths = new int[] { 3 };
      params.learningConstant = 0.05;
      network.setParameters(params);

      //////////////
      /* Training */
      //////////////

      try {
         // Generate test cases.
         for (Memory test : generateTests(schema)) {
            network.addMemory(test.inputVector, test.outputVector);
         }
      } catch (Exception e) {
         System.out.println("This file is jenky.  Could not generate tests.");
         e.printStackTrace();
      }

      network.train();

      /////////////////
      /* INTERACTION */
      /////////////////

      Scanner in = new Scanner(System.in);
      System.out.println("Test me!");

      // Ask for input until the user quits.
      // Loop is broken when "quit" is entered.
      while (true) {
         System.out.println();
         System.out.println("Enter x and y (or quit, random, fringe): ");
         String line = in.nextLine();

         if (line.contains("quit")) break;

         double x = 0.0;
         double y = 0.0;

         Random rand = new Random();
         if (line.contains("random")) {
            x = rand.nextDouble();
            y = rand.nextDouble();
            System.out.printf("Random x and y: %.6f %.6f\n", x, y);
         } else if (line.contains("fringe")){
            try {
               x = rand.nextDouble();

               System.out.printf("Enter fringe offset: ");
               double fringe = Double.parseDouble(in.nextLine());

               y = (rand.nextDouble() > 0.5) ? x + fringe : x - fringe;
               System.out.printf("Random x and y: %.6f %.6f\n", x, y);
            } catch (Exception e) {
               System.out.println("Invalid input!");
               continue;
            }
         } else {
            try {
               String[] tokens = line.trim().split("\\s+");
               x = Double.parseDouble(tokens[0]);
               y = Double.parseDouble(tokens[1]);
            } catch (Exception e) {
               System.out.println("Invalid input!");
               continue;
            }
         }

         try {
            String answer = (Double.compare(x,y) < 0) ? "less": "greater";
            double[] output = network.fire(new double[] {x, y});
            String guess = (String) schema.translateOutput(output);

            // Check network's guess.
            if (guess.equals(answer)) {
               System.out.println("Successful guess!");
            } else {
               System.out.println("Unsuccessful guess!");
               System.out.println("  Output vector: " + Arrays.toString(output));
            }
         } catch (Exception e) {
            System.out.println("This file is jenky.  Could not query network.");
         }
      }
   }

   /**
    * Generates a test suite.
    * Uses constants for number of tests and fringe tests, and the fringe
    * factor.
    * @return test suite
    */
   public static ArrayList<Memory> generateTests(Schema schema) throws Exception {
      Random rand = new Random();
      ArrayList<Memory> tests = new ArrayList<Memory>();

      // Generate test cases.
      for (int i = 0; i < kNumTests; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = rand.nextDouble() * 2 - 1.0;
         String answer = (Double.compare(x, y) < 0) ? "less" : "greater";
         tests.add(schema.createMemory(new double[]{x, y}, answer));
      }
      // Add in fringe cases
      for (int i = 0; i < kNumFringeTests; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = x - kFringeFactor;
         tests.add(schema.createMemory(new double[]{x, y}, "greater"));
         y = x + kFringeFactor;
         tests.add(schema.createMemory(new double[]{x, y}, "less"));
      }
      return tests;
   }
}
