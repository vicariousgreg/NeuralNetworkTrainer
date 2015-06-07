package model.network.memory;

import model.network.schema.Schema;

import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public class ShortTermMemoryModule extends MemoryModule {
   protected Map<Object, List<Memory>> longTermMemoryMap;
   protected Map<Object, List<Memory>> shortTermMemoryMap;

   public ShortTermMemoryModule(Schema schema) {
      super(schema);
      this.longTermMemoryMap = new LinkedHashMap<Object, List<Memory>>();
      this.shortTermMemoryMap = new LinkedHashMap<Object, List<Memory>>();
      for (Object classification : schema.getOutputClassifications()) {
         longTermMemoryMap.put(classification, new ArrayList<Memory>());
         shortTermMemoryMap.put(classification, new ArrayList<Memory>());
      }

   }

   public Map<Object, List<Memory>> getLongTermMemoryMap() {
      return longTermMemoryMap;
   }

   public Map<Object, List<Memory>> getShortTermMemoryMap() {
      return shortTermMemoryMap;
   }

   @Override
   public void wipeMemory() {
      super.wipeMemory();
      for (Object key : longTermMemoryMap.keySet()) {
         longTermMemoryMap.get(key).clear();
         shortTermMemoryMap.get(key).clear();
      }

   }

   @Override
   public void add(Memory memory) throws Exception  {
      super.add(memory);

      Object classification = schema.translateOutput(memory.outputVector);
      shortTermMemoryMap.get(classification).add(memory);
   }

   @Override
   public void onTrain() {
      // Commit short term memories
      for (Object key : shortTermMemoryMap.keySet()) {
         longTermMemoryMap.get(key).addAll(shortTermMemoryMap.get(key));
         shortTermMemoryMap.get(key).clear();
      }
   }

   /**
    * Splits memories, ensuring that both the training and test sets include
    * memories from short and long term memory sets.
    */
   @Override
   public List<List<Memory>> splitMemories() {
      List<Memory> shuffled = new ArrayList<Memory>();

      // Add long term memories.
      for (Object key : longTermMemoryMap.keySet()) {
         shuffled.addAll(longTermMemoryMap.get(key));
      }

      // Add short term memories.
      for (Object key : shortTermMemoryMap.keySet()) {
         shuffled.addAll(shortTermMemoryMap.get(key));
      }

      Collections.shuffle(shuffled);

      List<List<Memory>> splitMemories = new ArrayList<List<Memory>>();
      List<Memory> trainingMemory = new ArrayList<Memory>();
      List<Memory> testMemory = new ArrayList<Memory>();

      int cutoff = (int) (shuffled.size() * kCutoffPoint);
      trainingMemory.addAll(shuffled.subList(0, cutoff));
      testMemory.addAll(shuffled.subList(cutoff, shuffled.size()));

      splitMemories.add(trainingMemory);
      splitMemories.add(testMemory);
      return splitMemories;
   }

   @Override
   public MemoryModule clone() {
      ShortTermMemoryModule copy = (ShortTermMemoryModule) super.clone();

      // Copy short and long term memories.
      for (Object key : shortTermMemoryMap.keySet()) {
         copy.shortTermMemoryMap.get(key).addAll(shortTermMemoryMap.get(key));
         copy.longTermMemoryMap.get(key).addAll(longTermMemoryMap.get(key));
      }
      return copy;
   }
}
