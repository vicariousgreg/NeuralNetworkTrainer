package model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gpdavis on 6/7/15.
 */
public abstract class GeneticAdapter<T> {
   /**
    * Generates a population of individuals of a given size.
    * @param size population size
    * @return population
    */
   public List<T> generatePopulation(int size) {
      List<T> population = new ArrayList<T>();
      while (size-- > 0)
         population.add(generateIndividual());
      return population;
   }

   /**
    * Mutates a population.
    * @param mutationRate rate of mutation
    * @param population population to mutate
    */
   public void mutatePopulation(double mutationRate, List<T> population) {
      Random rand = new Random();
      for (T indiv : population)
         if (Double.compare(rand.nextDouble(), mutationRate) < 0) mutate(indiv);
   }

   /**
    * Gets the best member of a population.
    * @param population population
    * @return best member
    */
   public T getBest(List<T> population) {
      T best = population.get(0);
      double bestFitness = calcFitness(best);
      for (T indiv : population) {
         double indivFitness = calcFitness(indiv);
         if (Double.compare(indivFitness, bestFitness) > 0) {
            best = indiv;
            bestFitness = indivFitness;
         }
      }
      return best;
   }

   /**
    * Gets a random member of a population.
    * @param population population
    * @return random member
    */
   public T getRandom(List<T> population) {
      Random rand = new Random();
      return population.get(rand.nextInt(population.size()));
   }

   /**
    * Randomly generates an individual.
    * @return randomly generated individual.
    */
   public abstract T generateIndividual();

   /**
    * Calculates the fitness of an individual.
    * Lower fitness is better.
    *
    * @param individual individual to evaluate
    * @return fitness
    */
   public abstract double calcFitness(T individual);

   /**
    * Crosses over two individuals to produce a child.
    * @param left left parent
    * @param right right parent
    * @return child individual
    */
   public abstract T crossover(T left, T right);

   /**
    * Mutates an individual.
    * @param individual individual to mutate.
    */
   public abstract void mutate(T individual);
}
