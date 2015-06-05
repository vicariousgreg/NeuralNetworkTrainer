package gui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import model.network.Parameters;

public class ParametersDialogController extends DialogController {
   private Parameters params = new Parameters();

   public void setParameters(Parameters params) {
      this.params = params;
   }

   @Override
   public void confirm() {
      setResponseValue(params);
      close();
   }
}
