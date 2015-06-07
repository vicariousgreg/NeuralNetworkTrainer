package gui.controller.dialog;

import application.DialogFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import model.network.Network;
import model.network.Parameters;
import model.network.schema.ColorInputAdapter;
import model.network.schema.Schema;

import java.net.URL;
import java.util.ResourceBundle;

public class NetworkDialogController extends DialogController implements Initializable {
   @FXML TextField nameTextField;
   @FXML Label schemaLabel;
   @FXML Label parametersLabel;

   private Network network;
   private Schema schema;
   private Parameters params;

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      this.schema = new Schema(new ColorInputAdapter(),
            new String[] {
                  "Red",
                  "Orange",
                  "Yellow",
                  "Green",
                  "Blue",
                  "Purple"
            });

      this.params = new Parameters();
      render();
   }

   public void render() {
      this.schemaLabel.setText(schema.inputAdapter.toString());
   }

   public void editSchema() {
      Schema newSchema = DialogFactory.displaySchemaDialog(schema);
      if (newSchema != null)
         this.schema = newSchema;
      render();
   }

   public void editParameters() {
      Parameters newParams = DialogFactory.displayParametersDialog(params);
      if (newParams != null)
         this.params = newParams;
   }

   @Override
   public void confirm() {
      setResponseValue(new Network(nameTextField.getText(), schema, params));
      close();
   }
}
