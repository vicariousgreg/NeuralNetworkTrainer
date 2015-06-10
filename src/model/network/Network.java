package model.network;

import model.geneticAlgorithm.GeneticAlgorithm;
import model.geneticAlgorithm.PrototypeGeneticAdapter;
import model.network.memory.*;
import model.network.parameters.BooleanParameter;
import model.network.parameters.Parameters;
import model.network.schema.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Represents a neural network.
 */
public class Network implements Serializable {
   /** Network neuron graph. */
   NeuronGraph neuronGraph;

   /** Network schema. */
   public final Schema schema;

   /** Network learning parameters. */
   Parameters parameters;

   /** Memory of inputs and output test cases. */
   MemoryModule memoryModule;

   /** Prototype map. */
   private Map<Object, Memory> prototypes;

   /** Network name; */
   public String name;


   /**
    * Constructor.
    * Uses default parameters.
    * @param schema network schema
    */
   public Network(String name, Schema schema) {
      this(name, schema, new Parameters());
   }

   /**
    * Constructor.
    * @param schema network schema
    * @param params network parameters
    */
   public Network(String name, Schema schema, Parameters params) {
      this.name = name;
      this.schema = schema;
      this.parameters = params;
      this.neuronGraph = new NeuronGraph(schema, params);
      this.prototypes = new LinkedHashMap<Object, Memory>();
      generatePrototypes();

      buildMemoryModule((Class)
            params.getParameter(Parameters.kMemoryModule).getValue());
   }

   /**
    * Returns a copy of this network's parameters.
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
      this.neuronGraph.build(schema, params);
      buildMemoryModule((Class)
            params.getParameter(Parameters.kMemoryModule).getValue());
   }

   /**
    * Builds a new memory module of the given class and moves memories.
    * @param moduleClass new memory module class
    */
   private void buildMemoryModule(Class moduleClass) {
      try {
         MemoryModule oldMemory = this.memoryModule;

         Constructor constructor = moduleClass.getConstructor(Schema.class);
         this.memoryModule = (MemoryModule) constructor.newInstance(schema);

         if (oldMemory != null) {
            List<Memory> memories = oldMemory.getAllMemories();
            memoryModule.add(memories);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Gets memories of the given classification.
    * @param classification memory classification
    * @return memories
    */
   public List<Memory> getMemories(Object classification) {
      return memoryModule.getMemories(classification);
   }

   /**
    * Gets all of this network's memories.
    * @return memories
    */
   public List<Memory> getAllMemories() {
      return memoryModule.getAllMemories();
   }

   /**
    * Wipes the network's memory.
    */
   public void wipeMemory() {
      memoryModule.wipeMemory();
   }

   /**
    * Adds a memory to the network's memory module using the network's schema.
    * @param in     input object
    * @param result resulting classification
    */
   public void addMemory(Object in, Object result) throws Exception {
      memoryModule.add(schema.createMemory(in, result));
      if (((BooleanParameter) parameters.getParameter(Parameters.kLiveTraining)).getValue())
         train();
   }

   /**
    * Adds a new memory to the network's memory module.
    * @param mem memory to add
    */
   public void addMemory(Memory mem) throws Exception {
      memoryModule.add(mem);
      if (((BooleanParameter) parameters.getParameter(Parameters.kLiveTraining)).getValue())
         train();
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
      return schema.translateOutput(neuronGraph.fire(schema.encodeInput(in)));
   }

   /**
    * Trains this network using its memory module.
    */
   public void train() {
      try {
         // Split memory.
         List<List<Memory>> split = memoryModule.splitMemories();
         List<Memory> trainingMemory = split.get(0);
         List<Memory> testMemory = split.get(1);
         this.train(trainingMemory, testMemory);
      } catch (Exception e) {
         System.err.println("Network has corrupt memories!");
      }
   }

   /**
    * Trains this network once with a given training and test set.
    * @param trainingSet training set
    * @param testSet test set
    */
   public void train(List<Memory> trainingSet, List<Memory> testSet) throws Exception {
      try {
         new NetworkTrainer(this, trainingSet, testSet).train();
         memoryModule.onTrain();
         generatePrototypes();
      } catch (Exception e) {
         e.printStackTrace();
         System.err.println("Network has corrupt memories!");
      }
   }

   /**
    * Uses a genetic algorithm to generate prototypes for
    * each output classification.
    */
   private void generatePrototypes() {
      for (Object classification : schema.getOutputClassifications()) {
         try {
            GeneticAlgorithm<double[]> alg = new GeneticAlgorithm<double[]>(
                  new PrototypeGeneticAdapter(this, classification));
            alg.setPopulationSize(1000);
            alg.setGenerationCap(25);
            alg.setMutationRate(0.1);
            alg.setAcceptableFitness(-0.001);
            prototypes.put(classification, schema.createMemory(alg.run(), classification));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * Gets a collection of classification prototypes.
    * @return prototypes
    */
   public Collection<Memory> getPrototypes() {
      return prototypes.values();
   }

   /**
    * Crosses over two networks to produce a child network.
    * @param left left parent
    * @param right right parent
    * @return child network
    */
   public static Network crossover(Network left, Network right) {
      // Schemas should be equal
      // Cross over parameters by randomly selecting differing ones
      // Memory crossover?  Probably not.
      Parameters crossoverParameters = left.parameters;

      Network child = new Network("Child", left.schema, left.parameters);
      child.neuronGraph =
         NeuronGraph.crossover(left.neuronGraph, right.neuronGraph, crossoverParameters);
      return child;
   }

   /**
    * Mutates the network.
    */
   public void mutate() {
      neuronGraph.mutate();
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
    * Gets the string representation of this network's neuron graph.
    * @return neuron graph string
    */
   public String getNeuronGraphString() {
      return neuronGraph.toString();
   }

   /**
    * Returns this network's name.
    * @return network name
    */
   public String toString() {
      return name;
   }
}
