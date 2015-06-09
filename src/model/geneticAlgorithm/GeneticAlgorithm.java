package model.geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpdavis on 6/7/15.
 */
public class GeneticAlgorithm<T> {
   /** Individual adapter. */
   private GeneticAdapter<T> adapter;
   /** Population size. */
   private int populationSize = 25;
   /** Tournament size. */
   private int tournamentSize = 5;
   /** Individual mutation rate. */
   private double mutationRate = 0.015;
   /** Elitism flag. */
   private boolean elitism = true;
   /** Cap for generations.
    * 0 runs the algorithm until an ideal solution is found. */
   private int generationCap = 0;
   /** Acceptable fitness for algorithm termination. */
   private double acceptableFitness = 1.0;

   public GeneticAlgorithm(GeneticAdapter<T> adapter) {
      this.adapter = adapter;
   }

   public void setPopulationSize(int size) {
      this.populationSize = size;
   }

   public void setTournamentSize(int size) {
      this.tournamentSize = size;
   }

   public void setMutationRate(double rate) {
      this.mutationRate = rate;
   }

   public void setElitism(boolean elitism) {
      this.elitism  = elitism;
   }

   public void setGenerationCap(int cap) {
      this.generationCap = cap;
   }

   public void setAcceptableFitness(double fitness) {
      this.acceptableFitness = fitness;
   }

   /**
    * Runs the genetic algorithm.
    * @return winner
    */
   public T run() {
      System.out.println("RUNNING");
      List<T> population = adapter.generatePopulation(populationSize);
      System.out.println("Generated population...");
      T best = adapter.getBest(population);
      double bestFitness = adapter.calcFitness(best);
      boolean terminate = Double.compare(bestFitness, acceptableFitness) == 0;
      int generationCounter = 0;

      while (!terminate) {
         List<T> newPopulation = new ArrayList<T>();

         // Generate a new population using tournament and crossover.
         for (int index = (elitism) ? 1 : 0; index < populationSize; ++index) {
            T left = runTournament(population);
            T right = runTournament(population);
            while (left.equals(right)) right = runTournament(population);
            newPopulation.add(adapter.crossover(left, right));
         }

         // Mutate population.
         adapter.mutatePopulation(mutationRate, newPopulation);

         // Keep best if elitism is true.
         if (elitism) {
            newPopulation.add(best);
         }

         // Get the best from the new population.
         best = adapter.getBest(newPopulation);
         bestFitness = adapter.calcFitness(best);

         // Move population over.
         population = newPopulation;

         // Terminate if best is good enough or if we have exceeded
         // the population cap.
         ++generationCounter;
         terminate = generationCounter == generationCap ||
               Double.compare(bestFitness, acceptableFitness) == 0;

         System.out.println("GenAlg best fitness: " + bestFitness);
      }

      return best;
   }

   /**
    * Runs a tournament on a population.
    * @param population source population
    * @return tournament winner
    */
   private T runTournament(List<T> population)  {
      List<T> tournamentPopulation = new ArrayList<T>();
      for (int i = 0; i < tournamentSize; ++i) {
         tournamentPopulation.add(adapter.getRandom(population));
      }
      return adapter.getBest(tournamentPopulation);
   }
}
