package model.network.memory;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public class MemoryModule implements Serializable {
   public final Schema schema;
   private Map<Object, List<Memory>> longTermMemory;
   private Map<Object, List<Memory>> shortTermMemory;

   public MemoryModule(Schema schema) {
      this.schema = schema;
      this.longTermMemory = new LinkedHashMap<Object, List<Memory>>();
      this.shortTermMemory = new LinkedHashMap<Object, List<Memory>>();
      for (Object classification : schema.getOutputClassifications()) {
         longTermMemory.put(classification, new ArrayList<Memory>());
         shortTermMemory.put(classification, new ArrayList<Memory>());
      }
   }

   public Map<Object, List<Memory>> getLongTermMemory() {
      return longTermMemory;
   }

   public Map<Object, List<Memory>> getShortTermMemory() {
      return shortTermMemory;
   }

   public List<List<Memory>> naiveSplitMemories() {
      List<Memory> shuffled = new ArrayList<Memory>();

      // Add long term memories.
      for (Object key : longTermMemory.keySet()) {
         shuffled.addAll(longTermMemory.get(key));
      }

      // Add short term memories.
      for (Object key : shortTermMemory.keySet()) {
         shuffled.addAll(shortTermMemory.get(key));
      }

      Collections.shuffle(shuffled);

      List<List<Memory>> splitMemories = new ArrayList<List<Memory>>();
      List<Memory> trainingMemory = new ArrayList<Memory>();
      List<Memory> testMemory = new ArrayList<Memory>();

      int cutoff = (int) (shuffled.size() * 2.0 / 3);
      trainingMemory.addAll(shuffled.subList(0, cutoff));
      testMemory.addAll(shuffled.subList(cutoff, shuffled.size()));

      splitMemories.add(trainingMemory);
      splitMemories.add(testMemory);
      return splitMemories;
   }

   public void wipeLongTermMemory() {
      for (Object key : longTermMemory.keySet()) {
         longTermMemory.get(key).clear();
      }
   }

   public void wipeShortTermMemory() {
      for (Object key : shortTermMemory.keySet()) {
         shortTermMemory.get(key).clear();
      }
   }

   public void add(Memory memory) throws Exception  {
      if (!schema.fits(memory))
         throw new Exception();

      Object classification = schema.translateOutput(memory.outputVector);
      shortTermMemory.get(classification).add(memory);

      System.out.println("Added memory to short term memory!");
   }

   public void add(List<Memory> memories) throws Exception {
      for (Memory mem : memories) add(mem);
   }

   public void add(Object in, Object out) throws Exception {
      add(schema.createMemory(in, out));
   }

   public void commitShortTermMemories() {
      for (Object key : shortTermMemory.keySet()) {
         longTermMemory.get(key).addAll(shortTermMemory.get(key));
         shortTermMemory.get(key).clear();
      }
   }

   public List<List<Memory>> splitMemories() {
      final double kCutoffPoint = 2.0 / 3;

      List<List<Memory>> split = new ArrayList<List<Memory>>();
      List<Memory> trainingMemory = new ArrayList<Memory>();
      List<Memory> testMemory = new ArrayList<Memory>();

      // Split long term memories.
      for (Object key : longTermMemory.keySet()) {
         ArrayList<Memory> shuffled = new ArrayList<Memory>(longTermMemory.get(key));
         Collections.shuffle(shuffled);
         int cutoff = (int) (shuffled.size() * kCutoffPoint);

         trainingMemory.addAll(shuffled.subList(0, cutoff));
         testMemory.addAll(shuffled.subList(cutoff, shuffled.size()));
      }

      // Split short term memories.
      for (Object key : shortTermMemory.keySet()) {
         ArrayList<Memory> shuffled = new ArrayList<Memory>(shortTermMemory.get(key));
         Collections.shuffle(shuffled);
         int cutoff = (int) (shuffled.size() * kCutoffPoint);

         trainingMemory.addAll(shuffled.subList(0, cutoff));
         testMemory.addAll(shuffled.subList(cutoff, shuffled.size()));
      }

      split.add(trainingMemory);
      split.add(testMemory);
      return split;
   }

   public MemoryModule clone() {
      MemoryModule cloned = new MemoryModule(schema);
      try {
         for (Object key : longTermMemory.keySet()) {
            cloned.add(longTermMemory.get(key));
         }
         cloned.commitShortTermMemories();

         for (Object key : shortTermMemory.keySet()) {
            cloned.add(shortTermMemory.get(key));
         }
      } catch (Exception e) {
         System.out.println("Error cloning memory module!");
      }
      return cloned;
   }
}
