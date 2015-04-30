package network;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a neural network.
 */
public class Network implements Serializable {
   /** Layer sizes. */
   private int[] layerSizes;

   /** Network neuron layers. */
   private ArrayList<Neuron[]> layers;

   /** Number of inputs to the network. */
   private int numInputs;

   /** Network learning/testing parameters. */
   private NetworkParameters parameters = new NetworkParameters();

   /** Network schema. */
   public final Schema schema;

   /** Memory of inputs and output test cases. */
   private ArrayList<Experience> memory = new ArrayList<Experience>();

   /**
    * Constructor.
    * @param layerSizes number of neurons per layer (index 0 is input size)
    */
   public Network(Schema schema, int[] layerSizes) {
      this.schema = schema;
      this.layerSizes = layerSizes;
      this.numInputs = layerSizes[0];
      layers = new ArrayList<Neuron[]>();

      // Create each layer
      for (int i = 1; i < layerSizes.length; ++i) {
         Neuron[] layer = new Neuron[layerSizes[i]];

         // Initialize neurons using previous layer size.
         for (int j = 0; j < layer.length; ++j) {
            layer[j] = new Neuron(layerSizes[i-1]);
         }

         layers.add(layer);
      }
   }

   /**
    * Default unsafe constructor for cloning.
    * @param schema schema
    */
   private Network(Schema schema) {
      this.schema = schema;
   }

   /**
    * Resets the network.
    */
   public void reset() {
      layers = new ArrayList<Neuron[]>();

      // Create each layer
      for (int i = 1; i < layerSizes.length; ++i) {
         Neuron[] layer = new Neuron[layerSizes[i]];

         // Initialize neurons using previous layer size.
         for (int j = 0; j < layer.length; ++j) {
            layer[j] = new Neuron(layerSizes[i-1]);
         }

         layers.add(layer);
      }
   }

   /**
    * Returns this network's memory.
    * @return memory
    */
   public ArrayList<Experience> getMemory() {
      return memory;
   }

   /**
    * Wipes the network's memory.
    */
   public void wipeMemory() {
      memory = new ArrayList<Experience>();
   }

   /**
    * Creates an experience using the network's schema.
    * @param in input object
    * @param result resulting classification
    */
   public void addExperience(Object in, String result) throws Exception {
      memory.add(schema.createExperience(in, result));
   }

   /**
    * Adds an experience to this network's memory.
    * @param exp experience to add
    */
   public void addExperience(Experience exp) {
      memory.add(exp);
   }

   /**
    * Queries the network given an input object.
    * @param in input object
    * @return output string
    */
   public String query(Object in) throws Exception {
      return schema.translateOutput(fire(schema.convertInput(in)));
   }

   /**
    * Fires the neural network and returns output.
    * @param input input signals
    * @return output signals
    */
   public double[] fire(double[] input) {
      // Validate input size.
      if (input.length != numInputs)
         throw new RuntimeException("Network fired with improper input!");

      double[] output = null;

      // Thread input through network layers.
      for (Neuron[] layer : layers) {
         output = new double[layer.length];

         // Process input and catch output.
         for (int i = 0; i < layer.length; ++i) {
            output[i] = layer[i].fire(input);
         }

         // Set up input for next layer.
         input = output;
      }

      return output;
   }

   /**
    * Gets a table of weights for the neurons in a given layer.
    * @param layerIndex layer index
    * @return weights table
    */
   public double[][] getLayerWeights(int layerIndex) {
      Neuron[] previousLayer = layers.get(layerIndex - 1);
      Neuron[] currLayer = layers.get(layerIndex);
      double[][] weights = new double[previousLayer.length][currLayer.length];

      for (int c = 0; c < currLayer.length; ++c) {
         Neuron neuron = currLayer[c];
         double[] currWeights = neuron.getWeights();
         for (int p = 0; p < previousLayer.length; ++p) {
            weights[p][c] = currWeights[p];
         }
      }
      return weights;
   }

   /**
    * Fires the neural network and returns all neuron outputs.
    * @param input input signals
    * @return all neuron output signals
    */
   public ArrayList<double[]> getOutputs(double[] input) {
      // Validate input size.
      if (input.length != numInputs)
         throw new RuntimeException("Network fired with improper input!");

      ArrayList<double[]> outputs = new ArrayList<double[]>();

      // Thread input through network layers.
      for (Neuron[] layer : layers) {
         double[] output = new double[layer.length];

         // Process input and catch output.
         for (int i = 0; i < layer.length; ++i) {
            output[i] = layer[i].fire(input);
         }
         outputs.add(output);

         // Set up input for next layer.
         input = output;
      }
      return outputs;
   }

   /**
    * Calculates the total test error by summing up individual test
    * case errors.
    * @param tests test suite
    * @return total test error
    */
   public double calcTotalTestError(ArrayList<Experience> tests) {
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
   public double calcTestError(Experience test) {
      // Get quadratic deviations for each output neuron
      double[] errors = calcError(fire(test.inputs), test.outputs);

      // Total deviations.
      double totalError = 0.0;
      for (int i = 0; i < errors.length; ++i) {
         totalError += errors[i];
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
   public double[] calcError(double[] actual, double[] expected) {
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
    * Calculates backpropagation error, which is the derivative of the error.
    * @param actual network output
    * @param expected expected output
    * @return backpropagation error
    */
   private double[] calcBPError(double[] actual, double[] expected) {
      double[] errors = new double[actual.length];

      // Calculate backpropagated error for each output neuron.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = actual[i] *
                     (1 - actual[i]) *
                     (expected[i] - actual[i]);
      }
      return errors;
   }

   /**
    * Teaches the network using an experience.
    * Uses backpropagation.
    * @param exp experience to learn from
    */
   public void learn(Experience exp) {
      // Fire network and gather outputs.
      ArrayList<double[]> outputs = getOutputs(exp.inputs);

      // Calculate error for output layer
      // The output layer derives its error from the error function.
      // Backpropagation requires calculation of the derivative.
      double[] output = outputs.get(outputs.size() - 1);
      double[] errors = calcBPError(output, exp.outputs);

      // Backpropagate through layers.
      for (int layerIndex = layers.size() - 1; layerIndex >= 0; --layerIndex) {
         // Get output from previous layer.
         // For input layer, this is the experience input.
         output = (layerIndex > 0)
            ? outputs.get(layerIndex - 1)
            : exp.inputs;

         // Get current layer.
         Neuron[] currLayer = layers.get(layerIndex);

         // Set deltas for each neuron.
         for (int currIndex = 0; currIndex < currLayer.length; ++currIndex) {
            Neuron neuron = currLayer[currIndex];

            // Set weight deltas.
            // delta[p][c] = learningConstant * errors[c] * output[p]
            for (int prevIndex = 0; prevIndex < output.length; ++prevIndex) {
               neuron.setWeightDelta(prevIndex,
                  parameters.learningConstant * errors[currIndex] *
                       output[prevIndex]);
            }
            // Set bias delta.
            // dB = learningConstant * errors[c]
            neuron.setBiasDelta(parameters.learningConstant * errors[currIndex]);
         }

         // Stop at input layer.
         if (layerIndex == 0) break;

         // Create weights table for the current layer.
         double[][] weights = getLayerWeights(layerIndex);

         // Calculate hidden layer backpropagated errors.
         // newErrors[p] = out[p] * (1 - out[p]) * sigma(errors[c] * weights[p][c])
         double[] newErrors = new double[output.length];
         for (int prevIndex = 0; prevIndex < output.length; ++prevIndex) {
            double sigma = 0.0;

            // Calculate error sigma.
            for (int currIndex = 0; currIndex < currLayer.length; ++currIndex) {
               sigma += errors[currIndex] * weights[prevIndex][currIndex];
            }

            // Set the new error.
            newErrors[prevIndex] = output[prevIndex] *
                                  (1 - output[prevIndex]) *
                                  sigma;
         }

         // Move new errors over for next iteration.
         errors = newErrors;
      }

      // Commit deltas.
      for (Neuron[] layer : layers) {
         for (int i = 0; i < layer.length; ++i) {
            layer[i].commitDeltas();
         }
      }
   }

   /**
    * Trains the network with its memory.
    */
   public void train() {
      if (memory.size() == 0) {
         System.out.println("No memory!");
         return;
      }

      // Counter for stale networks.
      int staleCounter = 0;

      // Test Errors.
      double testError = calcTotalTestError(memory);
      double prevTestError = 10000.0;

      // Percentage of tests passed.
      double percentCorrect = calcPercentCorrect(memory);
      double prevPercentCorrect = 0;

      System.out.println("Total test error before learning: " + testError);
      System.out.println("Passing percentage: %" + percentCorrect);

      // Teach the network until the error is acceptable.
      // Loop is broken when conditions are met.
      while (true) {
         // Teach the network using the tests.
         for (int i = 0; i < memory.size(); ++i) {
            learn(memory.get(i));
         }

         // Calculate error and percentage correct.
         testError = calcTotalTestError(memory);
         percentCorrect = calcPercentCorrect(memory);

         // Break out of the loop if we've hit an acceptable state.
         if (testError < parameters.acceptableTestError &&
             percentCorrect > parameters.acceptablePercentCorrect) break;

         // Determine if the network needs to be reset.
         // If it is unacceptable, and is either stale or has regressed
         //   significantly in error, it should be reset.
         if (staleCounter > parameters.staleThreshold ||
             testError - prevTestError > parameters.regressionThreshold) {
            reset();
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
         calcTotalTestError(memory));
      System.out.println("Passing percentage: %" +
         calcPercentCorrect(memory));
      System.out.println();

      System.out.println(toString());
   }

   /**
    * Calculates the percentage of test cases passed.
    * @return percenage of tests passed
    */
   public double calcPercentCorrect(ArrayList<Experience> tests) {
      int correct = 0;

      // Run each test.
      for (int i = 0; i < tests.size(); ++i) {
         Experience test = tests.get(i);
         double[] output = fire(test.inputs);

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

   /**
    * Clones this network.
    * @return cloned network
    */
   public Network clone() {
      ArrayList<Neuron[]> newLayers = new ArrayList<Neuron[]>();

      // Iterate through layers.
      for (Neuron[] layer : layers) {
         Neuron[] newLayer = new Neuron[layer.length];

         // Iterate through neurons in layer.
         for (int i = 0; i < layer.length; ++i) {
            newLayer[i] = layer[i].clone();
         }
         newLayers.add(newLayer);
      }

      Network cloned = new Network(this.schema);
      cloned.layers = newLayers;
      cloned.memory = new ArrayList<Experience>();
      for (Experience exp : memory) {
         cloned.memory.add(exp);
      }
      return cloned;
   }

   /**
    * Returns a string representation of this network.
    * @return string representation
    */
   public String toString() {
      StringBuilder sb = new StringBuilder("Network\n");

      for (Neuron[] layer : layers) {
         sb.append("  LAYER\n");
         for (int i = 0; i < layer.length; ++i) {
            sb.append(layer[i] + "\n");
         }
      }
      return sb.toString();
   }
}
