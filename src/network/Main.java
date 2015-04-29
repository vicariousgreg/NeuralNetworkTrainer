package network;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {
   /** The number of standard tests generated in a suite. */
   private static final int kNumTests = 25;
   /** The number of fringe tests generated in a suite. */
   private static final int kNumFringeTests = 5;
   /** The offset for fringe tests. */
   private static final double kFringeFactor = 0.01;

   /** The number of iterations the learning process can go through without
    * making significant improvements before the network is reset. */
   private static final int kStaleThreshold = 100;

   /**
    * The error threshold for network regression.
    * If the network's error increases by this much, it is reset.
    */
   private static final int kErrorRegressionThreshold = 100;

   /** Acceptable test error for learning termination. */
   private static final double kAcceptableTestError = 5;
   /** Acceptable percentage correct for learning termination. */
   private static final double kAcceptablePercentCorrect = 95;

   /**
    * Main driver.
    */
   public static void main(String[] args) {
      ////////////////
      /* TEST CASES */
      ////////////////

      // Generate test cases.
      ArrayList<TestCase> tests = generateTests();
      // Generate validation test cases.
      ArrayList<TestCase> validation = generateTests();


      //////////////
      /* LEARNING */
      //////////////

      // Create the network.
      int[] layerSizes = new int[] {2, 3, 3, 1};
      Network network = new Network(layerSizes);

      // Counter for stale networks.
      int staleCounter = 0;

      // Test Errors.
      double testError = network.calcTotalTestError(validation);
      double prevTestError = 10000.0;

      // Percentage of tests passed.
      double percentCorrect = calcPercentCorrect(validation, network);
      double prevPercentCorrect = 0;

      System.out.println("Total test error before learning: " + testError);
      System.out.println("Passing percentage: %" + percentCorrect);

      // Teach the network until the error is acceptable.
      // Loop is broken when conditions are met.
      while (true) {
         // Teach the network using the tests.
         for (int i = 0; i < tests.size(); ++i) {
            network.learn(tests.get(i));
         }

         // Calculate error and percentage correct.
         testError = network.calcTotalTestError(validation);
         percentCorrect = calcPercentCorrect(validation, network);

         // Break out of the loop if we've hit an acceptable state.
         if (testError < kAcceptableTestError &&
             percentCorrect > kAcceptablePercentCorrect) break;

         // Determine if the network needs to be reset.
         // If it is unacceptable, and is either stale or has regressed
         //   significantly in error, it should be reset.
         if (staleCounter > kStaleThreshold ||
             testError - prevTestError > kErrorRegressionThreshold) {
            network = new Network(layerSizes);
            staleCounter = 0;
            System.out.println("====================");
            System.out.println("Resetting network...");
            System.out.println("====================");
         // If the error and percentage correct have not changed significantly,
         //   increase the stale counter.
         } else if ((Double.compare(testError, 100) != 0 && 
                     Double.compare(testError, prevTestError) == 0) ||
                    Double.compare(percentCorrect, prevPercentCorrect) == 0) {
            ++staleCounter;
         } else {
            System.out.printf("Percent Correct: %.6f%%  |  ", percentCorrect);
            System.out.printf("Test error: %.6f\n", testError);
            staleCounter = 0;
         }
         prevTestError = testError;
         prevPercentCorrect = percentCorrect;
      }

      System.out.println("Total test error after learning: " +
         network.calcTotalTestError(validation));
      System.out.println("Passing percentage: %" +
         calcPercentCorrect(validation, network));
      System.out.println();

      System.out.println(network);


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

         boolean answer = Double.compare(x,y) < 0;
         double guessValue = 
            network.fire(new double[] {x, y})[0];
         boolean guess = Double.compare(guessValue, 0.5) > 0;

         // Check network's guess.
         if (guess == answer) {
            System.out.println("Successful guess!");
         } else {
            System.out.println("Unsuccessful guess!");
            System.out.println("  Got: " + guessValue);
            System.out.println("  Expected: " + (answer ? "1.0" : 0.0));
         }
      }
   }

   /**
    * Calculates the percentage of test cases passed.
    * @param tests test suite
    * @param network network to test
    * @return percenage of tests passed
    */
   public static double calcPercentCorrect(ArrayList<TestCase> tests,
                                           Network network) {
      int correct = 0;

      // Run each test.
      for (int i = 0; i < tests.size(); ++i) {
         TestCase test = tests.get(i);
         double[] output = network.fire(test.inputs);
         boolean passed = true;

         // Determine if network guessed correctly.
         for (int j = 0; j < output.length; ++j) {
            boolean outLow = Double.compare(output[j], 0.5) < 0;
            boolean expLow = Double.compare(test.outputs[j], 0.5) < 0;

            if (outLow != expLow) passed = false;
         }
         if (passed) ++correct;
      }
      return 100.0 * (double) correct / tests.size();
   }

   /**
    * Generates a test suite.
    * Uses constants for number of tests and fringe tests, and the fringe
    * factor.
    * @return test suite
    */
   public static ArrayList<TestCase> generateTests() {
      Random rand = new Random();

      // Generate test cases.
      ArrayList<TestCase> tests = new ArrayList<TestCase>();
      for (int i = 0; i < kNumTests; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = rand.nextDouble() * 2 - 1.0;
         double answer = (Double.compare(x, y) < 0) ? 1.0 : 0.0;
         tests.add(new TestCase(new double[] { x, y }, new double[] { answer }));
      }
      // Add in fringe cases
      for (int i = 0; i < kNumFringeTests; ++i) {
         double x = rand.nextDouble() * 2 - 1.0;
         double y = x - kFringeFactor;
         tests.add(new TestCase(new double[] { x, y }, new double[] { 0.0 }));
         y = x + kFringeFactor;
         tests.add(new TestCase(new double[] { x, y }, new double[] { 1.0 }));
      }
      return tests;
   }
}
