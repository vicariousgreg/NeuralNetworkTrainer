package gui.controller.widget;

import javafx.scene.layout.FlowPane;
import model.network.memory.Memory;
import model.network.schema.Schema;

import java.util.List;

/**
 * Created by gpdavis on 6/5/15.
 */
public class MemoryBox {
   private FlowPane flowPane;

   public MemoryBox(FlowPane flowPane) {
      this.flowPane = flowPane;
   }

   public void add(Schema schema, List<Memory> memories) throws Exception {
      for (Memory memory : memories)
         add(schema, memory);
   }

   public void add(Schema schema, Memory mem) throws Exception {
      flowPane.getChildren().add(
            schema.toFXNode(mem, 25, 25));
   }

   public void clear() {
      flowPane.getChildren().clear();
   }
}
