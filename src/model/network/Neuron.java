package model.network;

import model.network.activation.ActivationFunction;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a neural network neuron.
 */
public class Neuron extends Observable implements Serializable, Observer {
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
   private Map<Observable, Double> weights;
   /** Map of last inputs observed. */
   private Map<Observable, Double> inputs;

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
      this.learningConstant = params.learningConstant;
      this.activationFunction = params.activationFunction;
      this.weights = new HashMap<Observable, Double>();
      this.inputs = new HashMap<Observable, Double>();
   }

   public void addObserver(Observer obs) {
      super.addObserver(obs);
      ++numOutputs;
   }

   public void addInputNeuron(Observable obs) {
      ++numInputs;
      weights.put(obs, new Double(0.0));
      inputs.put(obs, new Double(0.0));
   }

   /**
    * Randomizes the weights and bias.
    */
   public void randomize() {
      Random rand = new Random();
      for (Observable key : weights.keySet()) {
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
      setChanged();
      notifyObservers(out);
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
         for (Observable key : inputs.keySet()) {
            ((Neuron) key).backPropagate(error * weights.get(key));
         }

         // Update weights.
         for (Observable key : inputs.keySet()) {
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
   public Map<Observable, Double> getWeights() {
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

   @Override
   public void update(Observable o, Object arg) {
      ++inputCounter;
      inputs.put(o, (Double) arg);

      // Once we receive all inputs...
      if (inputCounter == numInputs) {
         inputCounter = 0;
         double x = 0.0;

         // Calculate net for activation function.
         for (Observable key : weights.keySet()) {
            x += inputs.get(key) * weights.get(key);
         }
         x += bias;

         // Calculate and fire signal output.
         fire(activationFunction.calculate(x));
      }
   }
}
