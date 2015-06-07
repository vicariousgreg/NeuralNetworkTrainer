package model.network;

import model.network.memory.*;
import model.network.schema.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Represents a neural network.
 */
public class Network implements Serializable {
   /** Network neuron map. */
   private NeuronMap neuronMap;

   /** Network schema. */
   public final Schema schema;

   /** Network learning parameters. */
   private Parameters parameters;

   /** Memory of inputs and output test cases. */
   private MemoryModule memoryModule;

   /** Network trainer. */
   private NetworkTrainer networkTrainer;

   /** Network name; */
   public String name;


   /**
    * Constructor.
    * Uses default parameters.
    *
    * @param schema network schema
    */
   public Network(Schema schema) {
      this(schema, new Parameters());
   }

   /**
    * Constructor.
    *
    * @param schema network schema
    * @param params network parameters
    */
   public Network(Schema schema, Parameters params) {
      this.schema = schema;
      this.parameters = params;
      this.neuronMap = new NeuronMap(schema, params);
      this.networkTrainer = new NetworkTrainer(neuronMap, schema, params);

      buildMemoryModule((Class)
            params.getParameterValue(Parameters.kMemoryModule));
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
      this.networkTrainer.setParameters(params);
      this.neuronMap.build(schema, params);
      buildMemoryModule((Class)
            params.getParameterValue(Parameters.kMemoryModule));
   }

   /**
    * Builds a new memory module of the given class and moves memories.
    * @param moduleClass new memory module class
    */
   public void buildMemoryModule(Class moduleClass) {
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
      return schema.translateOutput(neuronMap.fire(schema.encodeInput(in)));
   }

   /**
    * Trains this network using its memory module.
    */
   public void train() {
      List<Memory> trainingMemory;
      List<Memory> testMemory;

      do {
         // Split memory.
         List<List<Memory>> split = memoryModule.splitMemories();
         trainingMemory = split.get(0);
         testMemory = split.get(1);
      } while (!networkTrainer.train(trainingMemory, testMemory));

      memoryModule.onTrain();
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
    * Returns this network's name.
    * @return network name
    */
   public String toString() {
      return name;
   }
}
