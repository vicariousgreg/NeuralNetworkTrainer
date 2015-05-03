package model.network.memory;

import model.network.schema.Schema;

import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public class BasicMemoryModule extends MemoryModule {
   public BasicMemoryModule(Schema schema) {
      super(schema);
   }

   public List<List<Memory>> splitMemories() {
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
}
