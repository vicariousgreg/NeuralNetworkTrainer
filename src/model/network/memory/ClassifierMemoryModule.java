package model.network.memory;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public class ClassifierMemoryModule extends MemoryModule {
   public ClassifierMemoryModule(Schema schema) {
      super(schema);
   }
   public List<List<Memory>> splitMemories() {
      final double kCutoffPoint = 2 / 3;

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
}
