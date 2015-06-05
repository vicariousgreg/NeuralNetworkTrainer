package gui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import model.network.Network;
import model.network.Parameters;
import model.network.schema.Schema;

public class NetworkDialogController extends DialogController {
   private Network network;
   private Schema schema;
   private Parameters params;

   @Override
   public void confirm() {
      setResponseValue(network);
      close();
   }
}
