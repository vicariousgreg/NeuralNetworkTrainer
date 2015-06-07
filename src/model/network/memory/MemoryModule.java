package model.network.memory;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public abstract class MemoryModule implements Serializable {
   /** Cutoff point for splitting training/test memory sets. */
   public static final double kCutoffPoint = 2.0/3.0;

   /** Network schema. */
   public final Schema schema;

   /** Map of memories by classification. */
   protected Map<Object, List<Memory>> memoryMap;

   /**
    * Constructor.
    * @param schema network schema
    */
   public MemoryModule(Schema schema) {
      this.schema = schema;
      this.memoryMap = new LinkedHashMap<Object, List<Memory>>();
      for (Object classification : schema.getOutputClassifications()) {
         memoryMap.put(classification, new ArrayList<Memory>());
      }
   }

   /**
    * Gets a list of memories of the given classification.
    * @param classification memory classification
    * @return memory list
    */
   public List<Memory> getMemories(Object classification) {
      return memoryMap.get(classification);
   }

   /**
    * Gets a list of all memories regardless of classification.
    * @return memory list
    */
   public List<Memory> getAllMemories()  {
      List<Memory> toReturn = new ArrayList<Memory>();
      for (Object key : memoryMap.keySet())
         toReturn.addAll(getMemories(key));
      return toReturn;
   }

   /**
    * Wipes memory.
    */
   public void wipeMemory() {
      for (Object key : memoryMap.keySet()) {
         memoryMap.get(key).clear();
      }
   }

   /**
    * Adds a memory.
    * @param memory memory to add
    * @throws Exception if memory does not fit the schema
    */
   public void add(Memory memory) throws Exception  {
      if (!schema.fits(memory))
         throw new Exception("Memory does not fit network schema!");

      Object classification = schema.translateOutput(memory.outputVector);
      memoryMap.get(classification).add(memory);
   }

   /**
    * Adds a list of memories.
    * @param memories list of memories
    * @throws Exception if a memory does not fit the schema
    */
   public void add(List<Memory> memories) throws Exception {
      for (Memory mem : memories) add(mem);
   }

   /**
    * Clones this memory module.
    * @return clone
    */
   public MemoryModule clone() {
      MemoryModule copy = null;

      try {
         copy = this.getClass().getConstructor(Schema.class).newInstance(schema);

         for (Object key : memoryMap.keySet()) {
            copy.add(memoryMap.get(key));
         }
      } catch (Exception e) {
         System.err.println("Error cloning memory module!");
      }
      return copy;
   }

   /**
    * Method to be called after network completes training.
    */
   public abstract void onTrain();

   /**
    * Splits memories into training and test sets.
    * @return list containing training and test sets
    */
   public abstract List<List<Memory>> splitMemories();
}
