package model.geneticAlgorithm;

import model.network.Network;
import model.network.Parameters;
import model.network.memory.Memory;
import model.network.schema.Schema;

import java.util.List;
import java.util.Random;

/**
 * Created by gpdavis on 6/7/15.
 */
public class PrototypeGeneticAdapter extends GeneticAdapter<double[]> {
   private Network network;
   private Object classification;
   private double[] expectedOutput;
   private Random rand;

   public PrototypeGeneticAdapter(Network network, Object classification) throws Exception {
      this.network = network;
      this.classification = classification;
      this.expectedOutput = network.schema.encodeOutput(classification);
      this.rand = new Random();
   }

   @Override
   public double[] generateIndividual() {
      try {
         double[] inputVector = new double[network.schema.inputSize];

         for (int i = 0; i < inputVector.length; ++i) {
            inputVector[i] = rand.nextDouble();
         }
         return inputVector;
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Could not generate individual!");
      }
      return null;
   }

   @Override
   public double calcFitness(double[] individual) {
      double error = 0.0;
      try {
         // Calculate test error for each output neuron.
         for (int i = 0; i < individual.length; ++i) {
            if (Double.compare(expectedOutput[i], 1.0) == 0) {
               error += 0.5 *
                     (expectedOutput[i] - individual[i]) *
                     (expectedOutput[i] - individual[i]);
            }
         }
         if (!network.query(individual).equals(classification))
            return -100;
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Corrupt fitness memory set!");
      }
      return -error;
   }

   @Override
   public double[] crossover(double[] left, double[] right) {
      double[] child = new double[left.length];
      for (int i = 0; i < left.length; ++i) {
         child[i] = (left[i] + right[i]) / 2;
      }
      return child;
   }

   @Override
   public void mutate(double[] individual) {
      final double kMutationSigma = 0.3;

      for (int i = 0; i < individual.length; ++i) {
         double sigma = (2 * kMutationSigma * rand.nextDouble()) - kMutationSigma;
         double newVal = individual[i] + sigma;
         while (Double.compare(newVal, 1.0) > 0 ||
               Double.compare(newVal, 0.0) < 0) {
            sigma = (2 * kMutationSigma * rand.nextDouble()) - kMutationSigma;
            newVal = individual[i] + sigma;
         }
         individual[i] = newVal;
      }
   }
}
