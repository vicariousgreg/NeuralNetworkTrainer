package gui.controller.widget;

import javafx.scene.layout.FlowPane;
import model.network.memory.Memory;
import model.network.schema.Schema;

import java.util.List;
import java.util.Map;

/**
 * Created by gpdavis on 6/5/15.
 */
public class MemoryBox {
   private FlowPane flowPane;

   public MemoryBox(FlowPane flowPane) {
      this.flowPane = flowPane;
   }

   public void addAll(Schema schema, Map<Object, List<Memory>> memories) throws Exception {
      for (Object key : memories.keySet())
         add(schema, memories, key);
   }

   public void add(Schema schema, Map<Object, List<Memory>> memories, Object key) throws Exception {
      for (Memory memory : memories.get(key))
         add(memory, schema);
   }

   public void add(Memory mem, Schema schema) throws Exception {
      flowPane.getChildren().add(
            schema.toFXNode(mem, 25, 25));
   }

   public void clear() {
      flowPane.getChildren().clear();
   }
}
