package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.SetParameters;
import model.WorkSpace;
import model.network.Network;
import model.network.Parameters;
import model.network.activation.ActivationFunction;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ParametersController implements Initializable {
   private SetParameters setParameters;
   private ArrayList<TextField> parameterTextFields;
   private ActivationFunction currentActivationFunction;

   @FXML Pane pane;
   @FXML TextField learningConstantField;
   @FXML TextField hiddenLayersField;
   @FXML ComboBox activationFunctionsField;
   @FXML GridPane activationParametersGrid;
   @FXML TextField regressionThresholdField;
   @FXML TextField staleThresholdField;
   @FXML TextField acceptableErrorField;
   @FXML TextField acceptablePercentField;

   @FXML ProgressIndicator progress;

   public void initialize(URL location, ResourceBundle resources) {
      this.setParameters = WorkSpace.instance.setParameters;
      setParameters.setController(this);

      parameterTextFields = new ArrayList<TextField>();

      // Set up activation functions dropdown.
      for (Class function : Parameters.activationFunctions) {
         activationFunctionsField.getItems().add(function.getSimpleName());
      }

      if (WorkSpace.instance.openNetwork())
         setFields(WorkSpace.instance.getNetwork().getParameters());
   }

   public void clearFields() {
      learningConstantField.clear();
      hiddenLayersField.clear();
      regressionThresholdField.clear();
      staleThresholdField.clear();
      acceptableErrorField.clear();
      acceptablePercentField.clear();

      for (TextField field : parameterTextFields)  {
         field.clear();
      }
   }

   public void setFields(Parameters params) {
      // Set up standard parameters.
      learningConstantField.setText(Double.toString(params.learningConstant));
      regressionThresholdField.setText(Double.toString(params.regressionThreshold));
      staleThresholdField.setText(Integer.toString(params.staleThreshold));
      acceptableErrorField.setText(Double.toString(params.acceptableTestError));
      acceptablePercentField.setText(Double.toString(params.acceptablePercentCorrect));

      // Set up hidden layers string.
      // String is formatted as a space delimited list of integers.
      StringBuilder sb = new StringBuilder();
      int[] layers = params.hiddenLayerDepths;
      for (int i = 0; i < layers.length; ++i)
         sb.append(layers[i] + " ");
      hiddenLayersField.setText(sb.toString());

      currentActivationFunction = params.activationFunction;

      // Set up activation function dropdown.
      activationFunctionsField.setValue(
            params.activationFunction.getClass().getSimpleName());

      // Set up activation function parameters.
      initializeActivationParameters();
   }

   public void initializeActivationParameters() {
      try {
         parameterTextFields = new ArrayList<TextField>();
         activationParametersGrid.getChildren().clear();

         // Get list of parameter names.
         List<String> activationFunctionParameters = null;
         String functionName = (String) activationFunctionsField.getValue();
         for (Class clazz : Parameters.activationFunctions) {
            if (clazz.getSimpleName().equals(functionName)) {
               Method m = clazz.getMethod("getParameters");
               activationFunctionParameters = (List<String>) m.invoke(new Object[0]);
            }
         }

         // Create labels and text boxes for each parameter.
         int i = 0;
         for (String param : activationFunctionParameters) {
            Label label = new Label(param + ":");
            TextField input = new TextField();
            input.setPromptText(param);

            // For the currently selected activation function in the
            // network's parameters, set the values to the current ones.
            if (currentActivationFunction != null &&
                  functionName.equals(
                        currentActivationFunction.getClass().getSimpleName()))
               input.setText(currentActivationFunction.getValue(param));

            activationParametersGrid.addRow(i, label, input);
            parameterTextFields.add(input);
            ++i;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void reset() {
      setFields(setParameters.getParameters());
   }

   public void save() {
      System.out.println("Save");
      String invalidField = "";
      try {
         /* Extract standard fields. */
         invalidField = "Learning Constant";
         final double learning = Double.parseDouble(learningConstantField.getText());
         if (learning < 0.0 || learning > 1.0) throw new Exception();

         invalidField = "Regression Error Threshold";
         final double regression = Double.parseDouble(regressionThresholdField.getText());
         if (regression < 0.0) throw new Exception();

         invalidField = "Stale Threshold";
         final int stale = Integer.parseInt(staleThresholdField.getText());
         if (stale < 0) throw new Exception();

         invalidField = "Acceptable Test Error";
         final double error = Double.parseDouble(acceptableErrorField.getText());
         if (error < 1.0) throw new Exception();

         invalidField = "Acceptable Percentage Correct";
         final double percent = Double.parseDouble(acceptablePercentField.getText());
         if (percent < 0.0 || percent > 100.0) throw new Exception();


         /* Extract hidden layers. */
         invalidField = "Hidden Layers";
         String[] tokens = hiddenLayersField.getText().split("\\s+");
         final int[] hidden = new int[tokens.length];
         for (int i = 0; i < hidden.length; ++i) {
            hidden[i] = Integer.parseInt(tokens[i]);
         }


         /* Identify activation function. */
         Class functionClass = null;

         // Identify class and extract parameter list.
         String functionName = (String) activationFunctionsField.getValue();
         for (Class clazz : Parameters.activationFunctions) {
            if (clazz.getSimpleName().equals(functionName)) {
               functionClass = clazz;
            }
         }

         // Instantiate activation function.
         final ActivationFunction activ = (ActivationFunction) functionClass.newInstance();

         // Set parameters.
         invalidField = "Activation Function Parameters";
         for (TextField text : parameterTextFields) {
            activ.setValue(text.getPromptText(), text.getText());
         }

         setParameters.setParameters(
               new Parameters(learning,
                     hidden,
                     activ,
                     stale,
                     regression,
                     error,
                     percent));
         Stage stage = (Stage) pane.getScene().getWindow();
         stage.close();
      } catch (Exception e) {
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Invalid Parameters!");
         alert.setHeaderText("Invalid parameter: " + invalidField);
         alert.setContentText(null);
         alert.showAndWait();
      }
   }
}
