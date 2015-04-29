package network;

import java.util.Arrays;
import java.util.Random;

/**
 * Represents a neural network node.
 */
public class Neuron {
   /** Number of inputs. */
   private int numInputs;
   /** Array of weight values. */
   private double[] weights;
   /** Bias weight. */
   private double bias;

   /** Delta of weight changes for backpropagation. */
   private double[] weightDeltas;
   /** Delta of bias change for backpropagation. */
   private double biasDelta;

   /**
    * Randomized constructor.
    * Randomizes weights and bias.
    *
    * @param numInputs number of node inputs.
    */
   public Neuron(int numInputs) {
      this.numInputs = numInputs;
      this.weights = new double[numInputs];
      this.weightDeltas = new double[numInputs];
      randomize();
   }

   /**
    * Explicit constructor.
    * Initializes weights and bias, deep copying weights.
    * Useful for copying.
    *
    * @param weights initial weights
    * @param bias initial bias
    */
   private Neuron(double[] weights, double bias) {
      this.numInputs = weights.length;
      this.bias = bias;
      this.weightDeltas = new double[weights.length];

      // Copy weights.
      this.weights = new double[weights.length];
      System.arraycopy(weights, 0, this.weights, 0, weights.length);
   }

   /**
    * Randomizes the weights and bias.
    */
   public void randomize() {
      Random rand = new Random();

      // Randomize weights.
      for (int i = 0; i < numInputs; ++i) {
         this.weights[i] = rand.nextDouble() * 2 - 1;
      }
      this.bias = rand.nextDouble() * 2 - 1;
   }

   /**
    * Fires the neuron.
    * @param inputs array of input signals
    * @return output signal
    */
   public double fire(double[] inputs) {
      // Ensure input is of proper length.
      if (inputs.length != numInputs)
         throw new RuntimeException("Neuron received invalid number of inputs!");

      double x = 0.0;

      // Calculate sigmoid input.
      for (int i = 0; i < numInputs; ++i) {
         x += inputs[i] * weights[i];
      }
      x += bias;

      // Calculate signal output.
      return Sigmoid.calculate(x);
   }

   /**
    * Sets the weight delta of a particular weight.
    * @param weightIndex weight index
    * @param offset weight offset
    */
   public void setWeightDelta(int weightIndex, double offset) {
      weightDeltas[weightIndex] = offset;
   }

   /**
    * Sets the bias delta.
    * @param offset bias offset
    */
   public void setBiasDelta(double offset) {
      biasDelta = offset;
   }

   /**
    * Commits any pending weight deltas.
    */
   public void commitDeltas() {
      for (int i = 0; i < numInputs; ++i) {
         weights[i] += weightDeltas[i];
         weightDeltas[i] = 0;
      }
      bias += biasDelta;
      biasDelta = 0;
   }

   /**
    * Getter for weights.
    * @return weights
    */
   public double[] getWeights() {
      return weights;
   }

   /**
    * Getter for bias.
    * @return bias
    */
   public double getBias() {
      return bias;
   }

   /**
    * Clones this neuron.
    * @return clone
    */
   public Neuron clone() {
      return new Neuron(this.weights, this.bias);
   }

   /**
    * Returns a string representation of this neuron.
    * @return string representation
    */
   public String toString() {
      StringBuilder sb = new StringBuilder("     NEURON:\n");
      sb.append("        WEIGHTS: " + Arrays.toString(weights) + "\n");
      sb.append("        BIAS: " + bias);
      return sb.toString();
   }
}
