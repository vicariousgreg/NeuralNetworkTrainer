package gui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ConfirmationDialogController extends DialogController {
   @FXML Text textBox;

   public void setText(String text) {
      this.textBox.setText(text);
   }

   @Override
   public void confirm() {
      setResponseValue(true);
      close();
   }
}
