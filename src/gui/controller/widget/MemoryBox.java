package gui.controller.widget;

import javafx.scene.layout.FlowPane;
import model.network.memory.Memory;
import model.network.schema.Schema;

/**
 * Created by gpdavis on 6/5/15.
 */
public class MemoryBox {
   private FlowPane flowPane;

   public MemoryBox(FlowPane flowPane) {
      this.flowPane = flowPane;
   }

   public void add(Memory mem, Schema schema) throws Exception {
      flowPane.getChildren().add(
            schema.toFXNode(mem, 25, 25));
   }

   public void clear() {
      flowPane.getChildren().clear();
   }
}
