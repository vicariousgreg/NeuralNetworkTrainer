package gui.controller.widget;

import javafx.scene.layout.FlowPane;
import model.network.memory.Memory;
import model.network.schema.Schema;

import java.util.List;

/**
 * Created by gpdavis on 6/5/15.
 */
public class MemoryBox {
   /** GUI FlowPane component. */
   private FlowPane flowPane;

   /**
    * Constructor.
    * @param flowPane flow pane to hook into
    */
   public MemoryBox(FlowPane flowPane) {
      this.flowPane = flowPane;
   }

   /**
    * Adds a list of memories to this memory box.
    * @param schema schema of memories
    * @param memories memories to add
    * @throws Exception if adding the memories failed
    */
   public void add(Schema schema, List<Memory> memories) throws Exception {
      for (Memory memory : memories)
         add(schema, memory);
   }

   /**
    * Adds a memory to this memory box.
    * @param schema schema of memory
    * @param memory memory to add
    * @throws Exception if adding the memory failed
    */
   public void add(Schema schema, Memory memory) throws Exception {
      flowPane.getChildren().add(
            schema.toFXNode(memory, 25, 25));
   }

   /**
    * Clears the memory box.
    */
   public void clear() {
      flowPane.getChildren().clear();
   }
}
