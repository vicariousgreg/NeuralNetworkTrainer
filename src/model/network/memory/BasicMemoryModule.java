package model.network.memory;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public class BasicMemoryModule extends MemoryModule {
   public BasicMemoryModule(Schema schema) {
      super(schema);
   }

   @Override
   public void onTrain() { }

   @Override
   public List<List<Memory>> splitMemories() {
      List<Memory> shuffled = new ArrayList<Memory>(getAllMemories());
      Collections.shuffle(shuffled);

      List<List<Memory>> splitMemories = new ArrayList<List<Memory>>();
      List<Memory> trainingMemory = new ArrayList<Memory>();
      List<Memory> testMemory = new ArrayList<Memory>();
      splitMemories.add(trainingMemory);
      splitMemories.add(testMemory);

      int cutoff = (int) (shuffled.size() * kCutoffPoint);
      trainingMemory.addAll(shuffled.subList(0, cutoff));
      testMemory.addAll(shuffled.subList(cutoff, shuffled.size()));

      return splitMemories;
   }
}
