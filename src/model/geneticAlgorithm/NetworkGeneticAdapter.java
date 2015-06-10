package model.geneticAlgorithm;

import model.network.Network;
import model.network.parameters.Parameters;
import model.network.memory.Memory;
import model.network.schema.Schema;

import java.util.List;

/**
 * Created by gpdavis on 6/7/15.
 */
public class NetworkGeneticAdapter extends GeneticAdapter<Network> {
   private Schema schema;
   private Parameters parameters;
   private List<Memory> trainingSet;
   private List<Memory> testSet;
   private List<Memory> fitnessSet;

   public NetworkGeneticAdapter(Schema schema, Parameters params,
                                List<Memory> trainingSet,
                                List<Memory> testSet,
                                List<Memory> fitnessSet) {
      this.schema = schema;
      this.parameters = params;
      this.trainingSet = trainingSet;
      this.testSet = testSet;
      this.fitnessSet = fitnessSet;
   }

   @Override
   public Network generateIndividual() {
      try {
         Network individual = new Network(null, schema, parameters);
         individual.train(trainingSet, testSet);
         return individual;
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Could not generate individual!");
      }
      return null;
   }

   @Override
   public double calcFitness(Network individual) {
      int correct = 0;
      int size = fitnessSet.size();
      try {
         for (Memory mem : fitnessSet) {
            if (mem.output.equals(individual.query(mem.inputVector))) ++correct;
         }
         /*
         for (Memory mem : trainingSet) {
            if (mem.output.equals(individual.query(mem.inputVector))) ++correct;
         }
         for (Memory mem : testSet) {
            if (mem.output.equals(individual.query(mem.inputVector))) ++correct;
         }
         */
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Corrupt fitness memory set!");
      }

      return ((double)correct / size);
   }

   @Override
   public Network crossover(Network left, Network right) {
      Network child = Network.crossover(left, right);
      try {
         child.train(trainingSet, testSet);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return child;
   }

   @Override
   public void mutate(Network individual) {
      individual.mutate();
   }
}
