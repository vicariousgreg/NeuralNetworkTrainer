package gui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class TextDialogController extends DialogController {
   @FXML Text textBox;
   @FXML TextField textField;

   public void setText(String text) {
      this.textBox.setText(text);
   }

   @Override
   public void confirm() {
      setResponseValue(textField.getText());
      close();
   }
}
