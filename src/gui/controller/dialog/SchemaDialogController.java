package gui.controller.dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.network.Registry;
import model.network.schema.InputAdapter;
import model.network.schema.Schema;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SchemaDialogController extends DialogController implements Initializable {
   @FXML TextField nameTextField;
   @FXML ComboBox adapterComboBox;
   @FXML VBox classificationBox;
   @FXML ScrollPane scrollPane;

   private List<TextField> classificationFields = new ArrayList<TextField>();

   public void initialize(URL location, ResourceBundle resources) {
      // Set up activation functions dropdown.
      for (Class adapterClass : Registry.inputAdapterClasses) {
         adapterComboBox.getItems().add(adapterClass.getSimpleName());
      }
      adapterComboBox.getSelectionModel().select(
            Registry.inputAdapterClasses[0].getSimpleName());
   }

   public void setSchema(Schema schema) {
      nameTextField.setText(schema.name);
      adapterComboBox.getSelectionModel().select(
            schema.inputAdapter.getClass().getSimpleName());
      for (Object classification : schema.getOutputClassifications()) {
         addClassification();
         classificationFields.get(classificationFields.size()-1).setText(classification.toString());
      }
   }

   public void addClassification() {
      final HBox hbox = new HBox();
      final TextField newField = new TextField();
      final Button deleteButton = new Button("x");

      newField.setOnKeyPressed(new EventHandler<KeyEvent>() {
         @Override
         public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
               addClassification();
            }
         }
      });

      deleteButton.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent event) {
            classificationFields.remove(newField);
            classificationBox.getChildren().remove(hbox);
         }
      });

      hbox.getChildren().addAll(newField, deleteButton);
      hbox.setSpacing(20);
      hbox.setAlignment(Pos.CENTER);
      classificationBox.getChildren().add(hbox);

      classificationFields.add(newField);

      newField.requestFocus();
      scrollPane.setVvalue(scrollPane.getVmax());
   }

   @Override
   public void confirm() {
      InputAdapter adapter = null;

      for (Class adapterClass : Registry.inputAdapterClasses) {
         if (adapterComboBox.getSelectionModel().getSelectedItem().equals(adapterClass.getSimpleName())) {
            try {
               adapter = (InputAdapter) adapterClass.newInstance();
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      List<String> classifications = new ArrayList<String>();
      for (TextField field : classificationFields)
         classifications.add(field.getText());

      setResponseValue(new Schema(nameTextField.getText(),
            adapter,
            classifications.toArray()));
      close();
   }
}
