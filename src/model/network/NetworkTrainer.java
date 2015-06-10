package model.network;

import model.network.memory.Memory;
import model.network.parameters.Parameters;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Neural network trainer.
 */
public class NetworkTrainer implements Serializable {
   private Network network;
   private List<Memory> trainingMemory;
   private List<Memory> testMemory;
   private Map<Object, double[]> outputVectors;

   public NetworkTrainer(Network network, List<Memory> trainingMemory,
                         List<Memory> testMemory) throws Exception {
      this.trainingMemory = trainingMemory;
      this.testMemory = testMemory;
      this.network = network;

      this.outputVectors = new HashMap<Object, double[]>();

      for (Object classification : network.schema.getOutputClassifications())
         outputVectors.put(classification, network.schema.encodeOutput(classification));
   }

   /**
    * Trains the network with its memories.
    */
   public void train() throws Exception {
      final boolean debug = false;

      if (trainingMemory.size() == 0 || testMemory.size() == 0) {
         System.err.println("Insufficient memory for training!");
         return;
      }

      NeuronGraph bestGraph = network.neuronGraph.clone();

      // Counter for master reset.
      int masterCounter = 0;
      int staleCounter = 0;

      // Test Error.
      double prevError;
      double testError = calcTotalTestError(testMemory);

      // Percentage of tests passed.
      double prevPercent;
      double percentCorrect = calcPercentCorrect(testMemory);
      double bestPercent = percentCorrect;

      if (true) {
         System.out.println("Total test error before learning: " + testError);
         System.out.println("Passing percentage: %" + percentCorrect);
         System.out.println("Training memory size: " + trainingMemory.size());
         System.out.println("Test memory size: " + testMemory.size());
      }

      // Extract relevant parameters
      Double acceptableTestError = (Double)
            network.parameters.getParameter(Parameters.kAcceptableTestError).getValue();
      Double acceptablePercentCorrect = (Double)
            network.parameters.getParameter(Parameters.kAcceptablePercentCorrect).getValue();
      Integer iterationCap = (Integer)
            network.parameters.getParameter(Parameters.kIterationCap).getValue();
      Integer staleThreshold = (Integer)
            network.parameters.getParameter(Parameters.kStaleThreshold).getValue();

      // Teach the network until the error is acceptable.
      // Loop is broken when conditions are met or when we
      //   pass the iteration cap.
      while (++masterCounter < iterationCap &&
            (testError > acceptableTestError ||
            percentCorrect < acceptablePercentCorrect)) {
         // Teach the network using the tests.
         for (int i = 0; i < trainingMemory.size(); ++i) {
            network.neuronGraph.backpropagate(trainingMemory.get(i));
         }

         // Calculate error and percentage correct.
         prevError = testError;
         prevPercent = percentCorrect;

         testError = calcTotalTestError(testMemory);
         percentCorrect = calcPercentCorrect(testMemory);

         // Keep track of best.
         if (percentCorrect > bestPercent) {
            bestPercent = percentCorrect;
            bestGraph = network.neuronGraph.clone();
            if (debug) System.out.println("Best: " + bestPercent);
         } else if (Double.compare(bestPercent, percentCorrect) != 0) {
            //if (debug) System.out.print(".");
         }

         // Reset if stale
         if (Double.compare(prevError, testError) == 0 &&
               Double.compare(prevPercent, percentCorrect) == 0) {
            if (debug) System.out.print(".");

            if (++staleCounter == staleThreshold) {
               staleCounter = 0;
               network.neuronGraph.reset();
               testError = calcTotalTestError(testMemory);
               percentCorrect = calcPercentCorrect(testMemory);
               if (debug) System.out.println("\n===RESET");
            }
         } else if (debug && Double.compare(prevPercent, percentCorrect) > 0) {
            System.out.print("\n- ");
            System.out.printf("%f", percentCorrect);
         } else if (debug && Double.compare(prevPercent, percentCorrect) < 0) {
            System.out.print("\n+ ");
            System.out.printf("%f", percentCorrect);
         }

         if (debug) System.out.printf("Percent Correct: %.6f%%  |  ", percentCorrect);
         if (debug) System.out.printf("Test error: %.6f\n", testError);
      }

      if (masterCounter > iterationCap) {
         if (debug) System.out.printf("Passed %d iterations...\n", iterationCap);
      }

      network.neuronGraph = bestGraph;

      if (true) {
         System.out.println("Total test error after learning: " +
               calcTotalTestError(testMemory));
         System.out.println("Passing percentage: %" +
               calcPercentCorrect(testMemory));
         System.out.println();
      }
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
         double[] errors = calcError(network.neuronGraph.fire(test.inputVector),
               test.output);

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
   private double[] calcError(double[] actual, Object expected) throws Exception {
      double[] errors = new double[actual.length];
      double[] expectedVector = outputVectors.get(expected);

      // Calculate test error for each output neuron.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = 0.5 *
               (expectedVector[i] - actual[i]) *
               (expectedVector[i] - actual[i]);
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
            double[] outVector = network.neuronGraph.fire(test.inputVector);

            Object out = network.schema.translateOutput(outVector);
            if (out.equals(test.output)) ++correct;
         } catch (Exception e) { e.printStackTrace(); }
      }
      return 100.0 * (double) correct / tests.size();
   }
}
