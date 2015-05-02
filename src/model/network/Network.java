package model.network;

import model.network.schema.Schema;

import java.util.Collections;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a neural network.
 */
public class Network implements Serializable {
   /** Network schema. */
   public final Schema schema;

   /** Network learning parameters. */
   private Parameters parameters;

   /** Network neuron layers. */
   private ArrayList<Neuron[]> layers;

   /** Memory of inputs and output test cases. */
   private final ArrayList<Memory> memories;

   /**
    * Constructor.
    * Uses default parameters.
    * @param schema network schema
    */
   public Network(Schema schema) {
      this(schema, new Parameters());
   }

   /**
    * Constructor.
    * @param schema network schema
    * @param params network parameters
    */
   public Network(Schema schema, Parameters params) {
      this.schema = schema;
      this.parameters = params;
      this.memories = new ArrayList<Memory>();
      buildNetwork();
   }

   private void buildNetwork() {
      layers = new ArrayList<Neuron[]>();
      int[] layerSizes = new int[parameters.hiddenLayerDepths.length + 2];

      // Set up layer sizes array.
      layerSizes[0] = schema.inputSize;
      for (int i = 0; i < parameters.hiddenLayerDepths.length; ++i) {
         layerSizes[i+1] = parameters.hiddenLayerDepths[i];
      }
      layerSizes[layerSizes.length-1] = schema.outputSize;

      // Set up layers.
      for (int i = 1; i < layerSizes.length; ++i) {
         Neuron[] layer = new Neuron[layerSizes[i]];

         // Initialize neurons using previous layer size.
         for (int j = 0; j < layer.length; ++j) {
            layer[j] = new Neuron(parameters.activationFunction, layerSizes[i-1]);
         }

         layers.add(layer);
      }
   }

   /**
    * Resets the network by randomizing each neuron's weights.
    * Retains memories and parameters.
    */
   public void reset() {
      for (Neuron[] layer : layers) {
         for (int index = 0; index < layer.length; ++index) {
            layer[index].randomize();
         }
      }
   }

   /**
    * Returns the network parameters.
    * Returns a copy.
    * @return network parameters
    */
   public Parameters getParameters() {
      return parameters.clone();
   }

   /**
    * Sets the network parameters.
    * @param params network parameters
    */
   public void setParameters(Parameters params) {
      this.parameters = params;
      buildNetwork();
   }

   /**
    * Returns this network's memories.
    * @return memories
    */
   public ArrayList<Memory> getMemories() {
      return memories;
   }

   /**
    * Adds new memories to the network's memories.
    * @param newMemories new memories to add
    */
   public void addMemories(ArrayList<Memory> newMemories) {
      memories.addAll(newMemories);
   }

   /**
    * Wipes the network's memories.
    */
   public void wipeMemory() {
      memories.clear();
   }

   /**
    * Creates a memory using the network's schema.
    * @param in input object
    * @param result resulting classification
    * @throws Exception if the memory does not fit the network schema
    */
   public void addMemory(Object in, Object result) throws Exception {
      memories.add(schema.createMemory(in, result));
   }

   /**
    * Queries the network given an input object.
    * @param in input object
    * @return output object
    * @throws Exception if the input does not fit the network schema
    */
   public Object query(Object in) throws Exception {
      return schema.translateOutput(fire(schema.encodeInput(in)));
   }

   /**
    * Fires the neural network and returns output.
    * @param input input signals
    * @return output signals
    */
   public double[] fire(double[] input) {
      // Validate input size.
      if (input.length != schema.inputSize)
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
   private double[][] getLayerWeights(int layerIndex) {
      Neuron[] previousLayer = layers.get(layerIndex - 1);
      Neuron[] currLayer = layers.get(layerIndex);
      double[][] weights = new double[previousLayer.length][currLayer.length];

      // For each neuron in the layer, extract its weight for each
      // neuron in the previous layer.
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
   private ArrayList<double[]> getOutputs(double[] input) {
      // Validate input size.
      if (input.length != schema.inputSize)
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
   private double calcTotalTestError(ArrayList<Memory> tests) {
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
         double[] errors = calcError(fire(test.getInputVector()),
               test.getOutputVector());

         // Total deviations.
         for (int i = 0; i < errors.length; ++i) {
            totalError += errors[i];
         }
      } catch (Exception e) {
         System.out.println("Memory does not match network schema!");
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
    * Calculates backpropagation error, which is the derivative of the error.
    * @param actual network output
    * @param expected expected output
    * @return backpropagation error
    */
   private double[] calcBPError(double[] actual, double[] expected) {
      double[] errors = new double[actual.length];

      // Calculate backpropagated error for each output neuron.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = (expected[i] - actual[i]) *
            parameters.activationFunction.calculateDerivative(actual[i]);
      }
      return errors;
   }

   /**
    * Teaches the network using a memory.
    * Uses backpropagation.
    * @param memory memory to learn from
    */
   private void learn(Memory memory) {
      // Fire network and gather outputs.
      ArrayList<double[]> outputs = getOutputs(memory.getInputVector());

      // Calculate error for output layer
      // The output layer derives its error from the error function.
      // Backpropagation requires calculation of the derivative.
      double[] output = outputs.get(outputs.size() - 1);
      double[] errors = calcBPError(output, memory.getOutputVector());

      // Backpropagate through layers.
      for (int layerIndex = layers.size() - 1; layerIndex >= 0; --layerIndex) {
         // Get output from previous layer.
         // For input layer, this is the memory input.
         output = (layerIndex > 0)
            ? outputs.get(layerIndex - 1)
            : memory.getInputVector();

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
            newErrors[prevIndex] = sigma *
               parameters.activationFunction.calculateDerivative(output[prevIndex]);
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
    * Trains the network with its memories.
    */
   public void train() {
      final boolean print = false;

      if (memories.size() == 0) {
         System.out.println("No memory!");
         return;
      }

      // Shuffle memories.
      ArrayList<Memory> shuffled = new ArrayList<Memory>(memories);
      Collections.shuffle(shuffled);

      // Split up memories into training and test sets.
      ArrayList<Memory> trainingMemory = new ArrayList<Memory>();
      ArrayList<Memory> testMemory = new ArrayList<Memory>();

      // Use a 2/3rds cutoff.
      int cutoffIndex = (int) (shuffled.size() * 2 / 3);
      for (int index = 0; index < cutoffIndex; ++index) {
         trainingMemory.add(shuffled.get(index));
      }
      for (int index = cutoffIndex; index < shuffled.size(); ++index) {
         testMemory.add(shuffled.get(index));
      }

      // Counter for stale networks.
      int staleCounter = 0;

      // Test Errors.
      double testError = 0.0;
      double prevTestError = calcTotalTestError(testMemory);

      // Percentage of tests passed.
      double percentCorrect = 0;
      double prevPercentCorrect = calcPercentCorrect(testMemory);

      System.out.println("Total test error before learning: " + prevTestError);
      System.out.println("Passing percentage: %" + prevPercentCorrect);

      // Teach the network until the error is acceptable.
      // Loop is broken when conditions are met.
      while (true) {
         // Teach the network using the tests.
         for (int i = 0; i < trainingMemory.size(); ++i) {
            learn(trainingMemory.get(i));
         }

         // Calculate error and percentage correct.
         testError = calcTotalTestError(testMemory);
         percentCorrect = calcPercentCorrect(testMemory);

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
            if (print) System.out.println("====================");
            if (print) System.out.println("Resetting network...");
            if (print) System.out.println("====================");
         // If the error and percentage correct have not changed significantly,
         //   increase the stale counter.
         } else if ((Double.compare(testError, 100) != 0 && 
                     Double.compare(testError, prevTestError) == 0) ||
                    Double.compare(percentCorrect, prevPercentCorrect) == 0) {
            ++staleCounter;
         } else {
            if (print) System.out.printf("Percent Correct: %.6f%%  |  ", percentCorrect);
            if (print) System.out.printf("Test error: %.6f\n", testError);
            staleCounter = 0;
         }
         prevTestError = testError;
         prevPercentCorrect = percentCorrect;
      }

      System.out.println("Total test error after learning: " +
         calcTotalTestError(testMemory));
      System.out.println("Passing percentage: %" +
         calcPercentCorrect(testMemory));
      System.out.println();

      if (print) System.out.println(toString());
   }

   /**
    * Calculates the percentage of test cases passed.
    * @return percentage of tests passed
    */
   private double calcPercentCorrect(ArrayList<Memory> tests) {
      int correct = 0;

      // Run each test.
      for (int i = 0; i < tests.size(); ++i) {
         try {
            Memory test = tests.get(i);
            Object out = query(schema.encodeInput(test.getInputVector()));
            if (out.equals(schema.translateOutput(test.outputVector))) ++correct;
         } catch (Exception e) { e.printStackTrace(); }
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

      Network cloned = new Network(this.schema, this.parameters);
      cloned.layers = newLayers;
      for (Memory exp : memories) {
         cloned.memories.add(exp);
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
