package network;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a neural network.
 */
public class Network {
   /** Layer sizes. */
   private int[] layerSizes;

   /** Network neuron layers. */
   private ArrayList<Neuron[]> layers;

   /** Number of inputs to the network. */
   private int numInputs;

   /** Learning constant. */
   private double learningConstant = 0.1;

   /**
    * Constructor.
    * @param layerSizes number of neurons per layer (index 0 is input size)
    */
   public Network(int[] layerSizes) {
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
    * Explicit constructor.
    * Sets layers.
    * @param layers neuron layers
    */
   private Network(ArrayList<Neuron[]> layers) {
      this.layers = layers;
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
    * Sets the learning constant.
    * @param learningConstant new learning constant
    */
   public void setLearningConstant(double learningConstant) {
      this.learningConstant = learningConstant;
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
   public double calcTotalTestError(ArrayList<TestCase> tests) {
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
   public double calcTestError(TestCase test) {
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
    * Teaches the network using a test case.
    * @param test test case
    */
   public void learn(TestCase test) {
      // Fire network and gather outputs.
      ArrayList<double[]> outputs = getOutputs(test.inputs);

      // Calculate error for output layer
      // The output layer derives its error from the error function.
      // Backpropagation requires calculation of the derivative.
      double[] output = outputs.get(outputs.size() - 1);
      double[] errors = calcBPError(output, test.outputs);

      // Backpropagate through layers.
      for (int layerIndex = layers.size() - 1; layerIndex >= 0; --layerIndex) {
         // Get output from previous layer.
         // For input layer, this is the test input.
         output = (layerIndex > 0)
            ? outputs.get(layerIndex - 1)
            : test.inputs;

         // Get current layer.
         Neuron[] currLayer = layers.get(layerIndex);

         // Set deltas for each neuron.
         for (int currIndex = 0; currIndex < currLayer.length; ++currIndex) {
            Neuron neuron = currLayer[currIndex];

            // Set weight deltas.
            // delta[p][c] = learningConstant * errors[c] * output[p]
            for (int prevIndex = 0; prevIndex < output.length; ++prevIndex) {
               neuron.setWeightDelta(prevIndex,
                  learningConstant * errors[currIndex] *
                       output[prevIndex]);
            }
            // Set bias delta.
            // dB = learningConstant * errors[c]
            neuron.setBiasDelta(learningConstant * errors[currIndex]);
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

      return new Network(newLayers);
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
