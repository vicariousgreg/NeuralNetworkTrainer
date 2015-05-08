package model.network;

import model.network.memory.BasicMemoryModule;
import model.network.memory.Memory;
import model.network.memory.MemoryModule;
import model.network.schema.Schema;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a neural network.
 */
public class Network implements Serializable {
   /** Network schema. */
   public final Schema schema;

   /** Network learning parameters. */
   private Parameters parameters;

   /** Memory of inputs and output test cases. */
   private MemoryModule memoryModule;

   /** Network neuron layers. */
   private ArrayList<Neuron[]> layers;

   /** Neuron input layer. */
   private Neuron[] inputLayer;

   /** Neuron output layer. */
   private Neuron[] outputLayer;


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
      this.memoryModule = new BasicMemoryModule(schema);
      buildNetwork();
   }

   private void buildNetwork() {
      layers = new ArrayList<Neuron[]>();

      // Build input layer.
      inputLayer = new Neuron[schema.inputSize];
      for (int index = 0; index < inputLayer.length; ++index) {
         inputLayer[index] = new Neuron(parameters);
      }
      layers.add(inputLayer);

      Neuron[] prevLayer = inputLayer;

      // Build hidden layers.
      for (int layerIndex = 0; layerIndex < parameters.hiddenLayerDepths.length; ++layerIndex) {
         // Build a layer.
         Neuron[] currLayer = new Neuron[parameters.hiddenLayerDepths[layerIndex]];

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
    * Returns this network's memory module.
    * @return memory module
    */
   public MemoryModule getMemoryModule() {
      return memoryModule;
   }

   public void setMemoryModule(MemoryModule mem) {
      this.memoryModule = mem;
   }


   /**
    * Wipes the network's memory.
    */
   public void wipeMemory() {
      memoryModule.wipeShortTermMemory();
      memoryModule.wipeLongTermMemory();
   }

   /**
    * Adds a memory to the network's memory module using the network's schema.
    * @param in input object
    * @param result resulting classification
    */
   public void addMemory(Object in, Object result) throws Exception {
      memoryModule.add(in, result);
   }

   /**
    * Adds a new memory to the network's memory module.
    * @param mem memory to add
    */
   public void addMemory(Memory mem) throws Exception {
      memoryModule.add(mem);
   }

   /**
    * Adds new memories to the network's memory module.
    * @param newMemories new memories to add
    */
   public void addMemories(List<Memory> newMemories) throws Exception {
      memoryModule.add(newMemories);
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
         double[] errors = calcError(fire(test.inputVector),
               test.outputVector);

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
      // Uses expected - actual so that error represents direction of gradient descent.
      // Ommitting the extra output multiplication because the neuron does it for us.
      for (int i = 0; i < actual.length; ++i) {
         errors[i] = (expected[i] - actual[i]);
      }
      return errors;
   }

   /**
    * Teaches the network using a memory.
    * Uses backpropagation.
    * @param memory memory to learn from
    */
   private void learn(Memory memory) {
      // Fire network and gather output.
      double[] output = fire(memory.inputVector);
      double[] errors = calcBPError(output, memory.outputVector);

      // Backpropagate to output layer using calcBPError.
      for (int index = 0; index < outputLayer.length; ++index) {
         outputLayer[index].backPropagate(errors[index]);
      }
   }

   /**
    * Trains the network with its memories.
    */
   public void train() {
      final boolean print = false;

      // Split memory.
      List<List<Memory>> split = memoryModule.splitMemories();
      List<Memory> trainingMemory = split.get(0);
      List<Memory> testMemory = split.get(1);

      if (testMemory.size() == 0) {
         System.out.println("Insufficient memory for training!");
         return;
      }

      // Counter for stale networks.
      int staleCounter = 0;

      // Test Errors.
      double prevTestError = 1000;
      double testError = calcTotalTestError(testMemory);

      // Percentage of tests passed.
      double prevPercentCorrect = 0;
      double percentCorrect = calcPercentCorrect(testMemory);
      double bestPercent = percentCorrect;

      System.out.println("Total test error before learning: " + testError);
      System.out.println("Passing percentage: %" + percentCorrect);
      System.out.println("Training memory size: " + trainingMemory.size());
      System.out.println("Test memory size: " + testMemory.size());

      // Teach the network until the error is acceptable.
      // Loop is broken when conditions are met.
      while (testError > parameters.acceptableTestError ||
          percentCorrect < parameters.acceptablePercentCorrect) {
         // Set up previous values.
         prevTestError = testError;
         prevPercentCorrect = percentCorrect;

         // Teach the network using the tests.
         for (int i = 0; i < trainingMemory.size(); ++i) {
            learn(trainingMemory.get(i));
         }

         // Calculate error and percentage correct.
         testError = calcTotalTestError(testMemory);
         percentCorrect = calcPercentCorrect(testMemory);
         if (percentCorrect > bestPercent) {
            bestPercent = percentCorrect;
            System.out.println("Best: " + bestPercent);
         }


         // Determine if the network needs to be reset.
         // If it is unacceptable, and is either stale or has regressed
         //   significantly in error, it should be reset.
         if (staleCounter > parameters.staleThreshold ||
             testError - prevTestError > parameters.regressionThreshold) {
            if (print && staleCounter > parameters.staleThreshold) System.out.println("STALE");
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
            if (print) System.out.print(".");
            ++staleCounter;
         } else {
            if (print) System.out.printf("Percent Correct: %.6f%%  |  ", percentCorrect);
            if (print) System.out.printf("Test error: %.6f\n", testError);
            staleCounter = 0;
         }
      }

      System.out.println("Total test error after learning: " +
         calcTotalTestError(testMemory));
      System.out.println("Passing percentage: %" +
         calcPercentCorrect(testMemory));
      System.out.println();

      if (print) System.out.println(toString());

      // Commit short term memory.
      memoryModule.commitShortTermMemories();
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
            Object out = query(schema.encodeInput(test.inputVector));
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
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(this);

         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         return (Network) ois.readObject();
      } catch (IOException e) {
         return null;
      } catch (ClassNotFoundException e) {
         return null;
      }
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
