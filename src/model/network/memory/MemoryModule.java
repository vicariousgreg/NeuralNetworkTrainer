package model.network.memory;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.*;

/**
 * Created by gpdavis on 5/2/15.
 */
public abstract class MemoryModule implements Serializable {
   public final Schema schema;
   protected Map<Object, List<Memory>> longTermMemory;
   protected Map<Object, List<Memory>> shortTermMemory;

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

   public abstract List<List<Memory>> splitMemories();

   public MemoryModule clone() {
      try {
         Class subclass = this.getClass();
         MemoryModule cloned = (MemoryModule)
            subclass.getConstructor(Schema.class).newInstance(schema);
         for (Object key : longTermMemory.keySet()) {
            cloned.add(longTermMemory.get(key));
         }
         cloned.commitShortTermMemories();

         for (Object key : shortTermMemory.keySet()) {
            cloned.add(shortTermMemory.get(key));
         }
         return cloned;
      } catch (Exception e) {
         System.out.println("Error cloning memory module!");
      }
      return null;
   }
}
