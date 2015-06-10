package gui.controller.dialog;

import gui.controller.DialogFactory.Response;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by gpdavis on 6/4/15.
 */
public abstract class DialogController {
   /** Top level pane. */
   @FXML Pane pane;

   /** Dialog response object. */
   protected Response response = null;

   /**
    * Sets up the response object.
    * @param response response object
    */
   public void setResponse(Response response) {
      this.response = response;
   }

   /**
    * Sets the response value.
    * @param value response value
    */
   public void setResponseValue(Object value) {
      if (response != null) response.setValue(value);
   }

   /**
    * Closes the dialog.
    */
   public void close() {
      ((Stage)pane.getScene().getWindow()).close();
   }

   /**
    * Confirms the dialog, setting the response as appropriate.
    */
   public abstract void confirm();
}
