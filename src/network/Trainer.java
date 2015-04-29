package network;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Trainer {
   /** The number of iterations the learning process can go through without
    * making significant improvements before the network is reset. */
   private static final int kStaleThreshold = 1000;

   /**
    * The error threshold for network regression.
    * If the network's error increases by this much, it is reset.
    */
   private static final int kErrorRegressionThreshold = 100;

   /** Acceptable test error for learning termination. */
   private static final double kAcceptableTestError = 1000000;
   /** Acceptable percentage correct for learning termination. */
   private static final double kAcceptablePercentCorrect = 60;

   /** Test suite. */
   private ArrayList<TestCase> tests;

   /**
    * Constructor.
    */
   public Trainer() {
      tests = new ArrayList<TestCase>();
   }

   /**
    * Adds a test case to the trainer's suite.
    * @param test test to add
    */
   public void addTest(TestCase test) {
      tests.add(test);
   }

   /**
    * Trains a network with the test suite.
    * @param network network to train
    */
   public void train(Network network) {
      if (tests.size() == 0) {
         System.out.println("No tests!");
         return;
      }
      //////////////
      /* LEARNING */
      //////////////

      // Counter for stale networks.
      int staleCounter = 0;

      // Test Errors.
      double testError = network.calcTotalTestError(tests);
      double prevTestError = 10000.0;

      // Percentage of tests passed.
      double percentCorrect = calcPercentCorrect(tests, network);
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
         testError = network.calcTotalTestError(tests);
         percentCorrect = calcPercentCorrect(tests, network);

         // Break out of the loop if we've hit an acceptable state.
         if (testError < kAcceptableTestError &&
             percentCorrect > kAcceptablePercentCorrect) break;

         // Determine if the network needs to be reset.
         // If it is unacceptable, and is either stale or has regressed
         //   significantly in error, it should be reset.
         if (staleCounter > kStaleThreshold ||
             testError - prevTestError > kErrorRegressionThreshold) {
            network.reset();
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
         network.calcTotalTestError(tests));
      System.out.println("Passing percentage: %" +
         calcPercentCorrect(tests, network));
      System.out.println();

      System.out.println(network);
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

         int outputMaxIndex = 0;
         double outputMax = output[0];

         int answerMaxIndex = 0;
         double answerMax = test.outputs[0];

         // Determine if network guessed correctly.
         for (int j = 0; j < output.length; ++j) {
            double out = output[j];
            double ans = test.outputs[j];

            if (out > outputMax) outputMaxIndex = j;
            if (ans > answerMax) answerMaxIndex = j;
         }
         boolean passed = outputMaxIndex == answerMaxIndex;
         if (passed) ++correct;
      }
      return 100.0 * (double) correct / tests.size();
   }
}
