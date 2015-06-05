package gui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import model.network.Parameters;
import model.network.schema.Schema;

public class SchemaDialogController extends DialogController {
   private Schema schema;

   @Override
   public void confirm() {
      setResponseValue(schema);
      close();
   }
}
