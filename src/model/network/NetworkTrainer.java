package model.network;

import model.network.memory.Memory;
import model.network.schema.Schema;

import java.io.*;
import java.util.List;

/**
 * Neural network trainer.
 */
public class NetworkTrainer implements Serializable {
   private NeuronGraph neuronGraph;
   private Schema schema;
   private Parameters parameters;

   public NetworkTrainer(NeuronGraph neuronGraph, Schema schema, Parameters params) {
      this.neuronGraph = neuronGraph;
      this.schema = schema;
      this.parameters = params;
   }

   public void setParameters(Parameters params) {
      this.parameters = params;
   }

   /**
    * Trains the network with its memories.
    */
   public boolean train(List<Memory> trainingMemory, List<Memory> testMemory) {
      final boolean debug = false;

      if (trainingMemory.size() == 0 || testMemory.size() == 0) {
         System.err.println("Insufficient memory for training!");
         return true;
      }

      // Counter for master reset.
      int masterCounter = 0;

      // Counter for stale networks.
      int staleCounter = 0;

      // Test Errors.
      double prevTestError;
      double testError = calcTotalTestError(testMemory);

      // Percentage of tests passed.
      double prevPercentCorrect;
      double percentCorrect = calcPercentCorrect(testMemory);
      double bestPercent = percentCorrect;

      if (debug) System.out.println("Total test error before learning: " + testError);
      if (debug) System.out.println("Passing percentage: %" + percentCorrect);
      if (debug) System.out.println("Training memory size: " + trainingMemory.size());
      if (debug) System.out.println("Test memory size: " + testMemory.size());

      // Extract relevant parameters
      Double acceptableTestError = (Double)
            parameters.getParameterValue(Parameters.kAcceptableTestError);
      Double acceptablePercentCorrect = (Double)
            parameters.getParameterValue(Parameters.kAcceptablePercentCorrect);
      Double regressionThreshold = (Double)
            parameters.getParameterValue(Parameters.kRegressionThreshold);
      Integer staleThreshold = (Integer)
            parameters.getParameterValue(Parameters.kStaleThreshold);

      // Teach the network until the error is acceptable.
      // Loop is broken when conditions are met.
      while (testError > acceptableTestError ||
            percentCorrect < acceptablePercentCorrect) {
         // Set up previous values.
         prevTestError = testError;
         prevPercentCorrect = percentCorrect;

         // Teach the network using the tests.
         for (int i = 0; i < trainingMemory.size(); ++i) {
            neuronGraph.backpropagate(trainingMemory.get(i));
         }

         // Calculate error and percentage correct.
         testError = calcTotalTestError(testMemory);
         percentCorrect = calcPercentCorrect(testMemory);
         if (percentCorrect > bestPercent) {
            bestPercent = percentCorrect;
            if (debug) System.out.println("Best: " + bestPercent);
         }


         // Determine if the network needs to be reset.
         // If it is unacceptable, and is either stale or has regressed
         //   significantly in error, it should be reset.
         if (staleCounter > staleThreshold ||
               testError - prevTestError > regressionThreshold) {
            if (debug && staleCounter > staleThreshold) System.out.println("STALE");
            neuronGraph.reset();
            staleCounter = 0;
            if (debug) System.out.println("====================");
            if (debug) System.out.println("Resetting network...");
            if (debug) System.out.println("====================");
            // If the error and percentage correct have not changed significantly,
            //   increase the stale counter.
         } else if ((Double.compare(testError, 100) != 0 &&
               Double.compare(testError, prevTestError) == 0) ||
               Double.compare(percentCorrect, prevPercentCorrect) == 0) {
            if (debug) System.out.print(".");
            ++staleCounter;
         } else {
            if (debug) System.out.printf("Percent Correct: %.6f%%  |  ", percentCorrect);
            if (debug) System.out.printf("Test error: %.6f\n", testError);
            staleCounter = 0;
         }

         if(++masterCounter > 100000) {
            if (debug) System.out.println("Passed 100,000 iterations.  Reshuffling memories...");
            return false;
         } else if (masterCounter % 10000 == 0) {
            if (debug) System.out.print(".");
         }
      }

      if (debug) System.out.println("Total test error after learning: " +
            calcTotalTestError(testMemory));
      if (debug) System.out.println("Passing percentage: %" +
            calcPercentCorrect(testMemory));
      if (debug) System.out.println();

      return true;
   }

   /**
    * Calculates the total test error by summing up individual test
    * case errors.
    * @param tests test suite
    * @return total test error
    */
   private double calcTotalTestError(List<Memory> tests) {
      double totalTestError = 0.0;
      for (int i = 0; i < tests.size(); ++i) {
         totalTestError += calcTestError(tests.get(i));
      }
      return totalTestError;
   }

   /**
    * Runs a test and calculates the total error.
    * Uses sum of quadratic deviations.
    * @param test test to calculate error for
    * @return total error
    */
   private double calcTestError(Memory test) {
      double totalError = 0.0;
      try {
         // Get quadratic deviations for each output neuron
         double[] errors = calcError(neuronGraph.fire(test.inputVector),
               test.outputVector);

         // Total deviations.
         for (int i = 0; i < errors.length; ++i) {
            totalError += errors[i];
         }
      } catch (Exception e) {
         System.err.println("Memory does not match network schema!");
         e.printStackTrace();
      }
      return totalError;
   }

   /**
    * Calculates the error of the network given actual and expected output.
    * Uses quadratic deviation.
    * @param actual network output
    * @param expected expected output
    * @return errors
    */
   private double[] calcError(double[] actual, double[] expected) {
      double[] errors = new double[actual.length];

      // Calculate test error for each output neuron.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = 0.5 *
               (expected[i] - actual[i]) *
               (expected[i] - actual[i]);
      }
      return errors;
   }


   /**
    * Calculates the percentage of test cases passed.
    * @return percentage of tests passed
    */
   private double calcPercentCorrect(List<Memory> tests) {
      int correct = 0;

      // Run each test.
      for (int i = 0; i < tests.size(); ++i) {
         try {
            Memory test = tests.get(i);
            double[] outVector = neuronGraph.fire(test.inputVector);

            Object out = schema.translateOutput(outVector);
            Object testOut = schema.translateOutput(test.outputVector);
            if (out.equals(testOut)) ++correct;
         } catch (Exception e) { e.printStackTrace(); }
      }
      return 100.0 * (double) correct / tests.size();
   }
}
