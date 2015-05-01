package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.network.Experience;
import model.network.Network;
import model.network.Parameters;
import model.network.activation.ActivationFunction;
import model.network.schema.Schema;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ParametersController implements Initializable {
   private Network network;
   private Parameters parameters;

   @FXML TextField learningConstant;
   @FXML TextField hiddenLayers;
   @FXML ComboBox activationFunctions;
   @FXML TextField regressionThreshold;
   @FXML TextField staleThreshold;
   @FXML TextField acceptableError;
   @FXML TextField acceptablePercent;

   public void initialize(URL location, ResourceBundle resources) {
      for (String function : Parameters.activationFunctionNames.keySet()) {
         activationFunctions.getItems().add(function);
      }
   }


   public void setNetwork(Network network) {
      this.network = network;
      this.parameters = network.getParameters();
      setFields();
   }

   public void setFields() {
      learningConstant.setText(Double.toString(parameters.learningConstant));

      StringBuilder sb = new StringBuilder();
      int[] layers = parameters.hiddenLayerDepths;
      for (int i = 0; i < layers.length; ++i)
         sb.append(layers[i] + " ");

      hiddenLayers.setText(sb.toString());
      activationFunctions.setValue(parameters.activationFunctionName);

      regressionThreshold.setText(Double.toString(parameters.regressionThreshold));
      staleThreshold.setText(Integer.toString(parameters.staleThreshold));
      acceptableError.setText(Double.toString(parameters.acceptableTestError));
      acceptablePercent.setText(Double.toString(parameters.acceptablePercentCorrect));
   }

   public void reset() {
      setFields();
   }

   public void save() {
      try {
         double learning = Double.parseDouble(learningConstant.getText());

         String[] tokens = hiddenLayers.getText().split("\\s+");

         int[] hidden = new int[tokens.length];
         for (int i = 0; i < hidden.length; ++i) {
            hidden[i] = Integer.parseInt(tokens[i]);
         }

         String function = (String) activationFunctions.getValue();
         Class functionClass = Parameters.activationFunctionNames.get(function);
         ActivationFunction activ = (ActivationFunction) functionClass.newInstance();

         double regression = Double.parseDouble(regressionThreshold.getText());
         int stale = Integer.parseInt(staleThreshold.getText());
         double error = Double.parseDouble(acceptableError.getText());
         double percent = Double.parseDouble(acceptablePercent.getText());

         if (learning < 0.0 || learning > 1.0 ||
               regression < 0.0 ||
               stale < 0 ||
               error < 0.0 ||
               percent < 0.0 || percent > 100.0)
            throw new Exception();

         Parameters newParams = new Parameters(learning, hidden, activ, stale, regression, error, percent);
         network.setParameters(newParams);
      } catch (Exception e) {
         e.printStackTrace();
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Invalid Parameters!");
         alert.setHeaderText("Invalid Parameters!");
         alert.setContentText(null);

         alert.showAndWait();
         // popup
      }
   }
}
