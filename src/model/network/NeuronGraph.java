package model.network;

import model.network.memory.Memory;
import model.network.schema.Schema;

import java.io.*;
import java.util.ArrayList;

/**
 * Graph of neural network neurons.
 */
public class NeuronGraph implements Serializable {
   /** Network neuron layers. */
   private ArrayList<Neuron[]> layers;

   /** Neuron input layer. */
   private Neuron[] inputLayer;

   /** Neuron output layer. */
   private Neuron[] outputLayer;

   /**
    * Constructor.
    * @param schema input/output schema
    * @param parameters network parameters
    */
   public NeuronGraph(Schema schema, Parameters parameters) {
      build(schema, parameters);
   }

   /**
    * Builds the neuron graph.
    * @param schema input/output schema
    * @param parameters network parameters
    */
   public void build(Schema schema, Parameters parameters) {
      layers = new ArrayList<Neuron[]>();

      // Build input layer.
      inputLayer = new Neuron[schema.inputSize];
      for (int index = 0; index < inputLayer.length; ++index) {
         inputLayer[index] = new Neuron(parameters);
      }
      layers.add(inputLayer);

      Neuron[] prevLayer = inputLayer;

      Integer[] hiddenLayerDepths = (Integer[])
            parameters.getParameterValue(Parameters.kHiddenLayerDepths);

      // Build hidden layers.
      for (int layerIndex = 0; layerIndex < hiddenLayerDepths.length; ++layerIndex) {
         // Build a layer.
         Neuron[] currLayer = new Neuron[hiddenLayerDepths[layerIndex]];

         // Hook layer up to previous layer.
         for (int currIndex = 0; currIndex < currLayer.length; ++currIndex) {
            currLayer[currIndex] = new Neuron(parameters);
            for (int prevIndex = 0; prevIndex < prevLayer.length; ++prevIndex) {
               // Hook up input from previous layer.
               // Tell current neuron to expect input from previous.
               currLayer[currIndex].addInputNeuron(prevLayer[prevIndex]);
               // Tell previous neuron to notify current neuron.
               prevLayer[prevIndex].addOutputNeuron(currLayer[currIndex]);
            }
            // Randomize neuron.
            currLayer[currIndex].randomize();
         }
         layers.add(currLayer);

         prevLayer = currLayer;
      }

      // Build output layer.
      outputLayer = new Neuron[schema.outputSize];

      // Hook layer up to previous layer.
      for (int outIndex = 0; outIndex < outputLayer.length; ++outIndex) {
         outputLayer[outIndex] = new Neuron(parameters);
         for (int prevIndex = 0; prevIndex < prevLayer.length; ++prevIndex) {
            // Hook up input from previous layer.
            // Tell current neuron to expect input from previous.
            outputLayer[outIndex].addInputNeuron(prevLayer[prevIndex]);
            // Tell previous neuron to notify current neuron.
            prevLayer[prevIndex].addOutputNeuron(outputLayer[outIndex]);
         }
         // Randomize neuron.
         outputLayer[outIndex].randomize();
      }
      layers.add(outputLayer);
   }

   /**
    * Resets the network by randomizing each neuron's weights.
    */
   public void reset() {
      for (Neuron[] layer : layers) {
         for (Neuron neuron : layer)
            neuron.randomize();
      }
   }

   /**
    * Fires the neural network and returns output.
    * @param input input signals
    * @return output signals
    */
   public double[] fire(double[] input) {
      // Validate input size.
      if (input.length != inputLayer.length)
         throw new RuntimeException("Network fired with improper input!");

      double[] output = new double[outputLayer.length];

      for (int inputIndex = 0; inputIndex < inputLayer.length; ++inputIndex) {
         inputLayer[inputIndex].fire(input[inputIndex]);
      }

      for (int outputIndex = 0; outputIndex < outputLayer.length; ++outputIndex) {
         output[outputIndex] = outputLayer[outputIndex].getOutput();
      }

      return output;
   }

   /**
    * Teaches the network a memory using backpropagation.
    * @param memory memory to learn from
    */
   public void backpropagate(Memory memory) {
      // Fire network and gather output.
      double[] output = fire(memory.inputVector);
      double[] errors = calcBPError(output, memory.outputVector);

      // Backpropagate to output layer using calcBPError.
      for (int index = 0; index < outputLayer.length; ++index) {
         outputLayer[index].backPropagate(errors[index]);
      }
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
      // Uses expected - actual so that error represents direction of gradient descent.
      // Ommitting the extra output multiplication because the neuron does it for us.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = (expected[i] - actual[i]);
      }
      return errors;
   }

   @Override
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
