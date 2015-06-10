package model.network;

import model.network.activation.ActivationFunction;
import model.network.parameters.ClassParameter;
import model.network.parameters.Parameters;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a neural network neuron.
 */
public class Neuron implements Serializable {
   /** Learning constant. */
   private double learningConstant;

   /** Activation function. */
   private ActivationFunction activationFunction;
   /** Number of input neurons. */
   private int numInputs;
   /** Number of output neurons. */
   private int numOutputs;

   /** Bias weight. */
   private double bias;

   /** Map of input neurons to weights. */
   private Map<Neuron, Double> weights;
   /** Map of last inputs observed. */
   private Map<Neuron, Double> inputs;

   /** Output neurons. */
   private List<Neuron> outputNeurons;

   /** Counter for input. */
   private int inputCounter;
   /** Counter for backpropagation of error. */
   private int errorCounter;
   /** Last output. */
   private double output;
   /** Error sigma for backpropagation. */
   private double errorSigma;

   /**
    * Randomized constructor.
    * Randomizes weights and bias.
    *
    * @param params network parameters
    */
   public Neuron(Parameters params) {
      this.numInputs = 0;
      this.learningConstant = (Double)params.getParameter(Parameters.kLearningConstant).getValue();
      this.activationFunction = (ActivationFunction)
            ((ClassParameter)params.getParameter(Parameters.kActivationFunction)).instantiate();
      this.weights = new LinkedHashMap<Neuron, Double>();
      this.inputs = new LinkedHashMap<Neuron, Double>();
      this.outputNeurons = new ArrayList<Neuron>();
   }

   public void addOutputNeuron(Neuron neuron) {
      outputNeurons.add(neuron);
      ++numOutputs;
   }

   public void addInputNeuron(Neuron neuron) {
      ++numInputs;
      weights.put(neuron, new Double(0.0));
      inputs.put(neuron, new Double(0.0));
   }

   /**
    * Randomizes the weights and bias.
    */
   public void randomize() {
      Random rand = new Random();
      for (Neuron key : weights.keySet()) {
         weights.put(key, rand.nextDouble() * 2 - 1);
      }

      this.bias = rand.nextDouble() * 2 - 1;
   }

   /**
    * Fires the neuron.
    * @return output signal
    */
   public void fire(double out) {
      output = out;
      for (Neuron neuron : outputNeurons) {
         neuron.update(this, out);
      }
   }

   public void backPropagate(double bpError) {
      if (numInputs == 0) return;
      ++errorCounter;

      errorSigma += bpError;

      // Once we receive all errors...
      if (numOutputs == 0 || errorCounter == numOutputs) {
         errorCounter = 0;
         double error = errorSigma *
            activationFunction.calculateDerivative(output);

         // Backpropagate error to input neurons.
         for (Neuron key : inputs.keySet()) {
            key.backPropagate(error * weights.get(key));
         }

         // Update weights.
         for (Neuron key : inputs.keySet()) {
            double delta = learningConstant * error * inputs.get(key);
            weights.put(key, weights.get(key) + delta);
         }
         bias += learningConstant * error;

         errorSigma = 0;
      }
   }

   /**
    * Getter for weights.
    * @return weights
    */
   public Map<Neuron, Double> getWeights() {
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
    * Gets the last fired output.
    * @return output
    */
   public double getOutput() {
      return output;
   }

   /**
    * Mutates this neuron.
    * Used for genetic algorithm.
    */
   public void mutate() {
      // Likelihood of mutating weight.
      final double kWeightMutationRate = 0.5;
      // Bounds of weight mutation.
      final double kMutationBounds = 0.1;

      Random rand = new Random();

      // Mutate neuron.
      for (Neuron key : weights.keySet()) {
         // Mutate weight.
         if (Double.compare(rand.nextDouble(), kWeightMutationRate) < 0) {
            // Generate a sigma between -kMutationBounds and +kMutationBounds.
            double weightSigma =
                  rand.nextDouble() * (2 * kMutationBounds) - kMutationBounds;
            weights.put(key, weights.get(key) + weightSigma);
         }
      }

      // Mutate bias.
      // Generate a sigma between -kMutationBounds and +kMutationBounds.
      double weightSigma =
            rand.nextDouble() * (2 * kMutationBounds) - kMutationBounds;
      bias += weightSigma;
   }

   public void update(Neuron o, Object arg) {
      ++inputCounter;
      inputs.put(o, (Double) arg);

      // Once we receive all inputs...
      if (inputCounter == numInputs) {
         inputCounter = 0;
         double x = 0.0;

         // Calculate net for activation function.
         for (Neuron key : weights.keySet()) {
            x += inputs.get(key) * weights.get(key);
         }
         x += bias;

         // Calculate and fire signal output.
         fire(activationFunction.calculate(x));
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      for (Neuron key : weights.keySet()) {
         sb.append(weights.get(key) + "\n");
      }
      return sb.toString();
   }
}
