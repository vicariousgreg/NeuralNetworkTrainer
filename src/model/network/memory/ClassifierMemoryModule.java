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

   @Override
   public void onTrain() { }

   /**
    * Splits memories, ensuring that both the training and test sets include
    * memories from each classification.
    */
   @Override
   public List<List<Memory>> splitMemories() {
      List<List<Memory>> split = new ArrayList<List<Memory>>();
      List<Memory> trainingMemory = new ArrayList<Memory>();
      List<Memory> testMemory = new ArrayList<Memory>();

      // Split memories by classification.
      for (Object key : memoryMap.keySet()) {
         ArrayList<Memory> shuffled = new ArrayList<Memory>(memoryMap.get(key));
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
