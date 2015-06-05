package gui.controller;

import application.DialogFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Registry;
import model.WorkSpace;
import model.network.Network;
import model.network.Parameters;
import model.network.activation.ActivationFunction;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ParametersController extends NetworkController implements Initializable {
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

   public void initialize(URL location, ResourceBundle resources) {
      parameterTextFields = new ArrayList<TextField>();

      // Set up activation functions dropdown.
      for (Class function : Registry.activationFunctionClasses) {
         activationFunctionsField.getItems().add(function.getSimpleName());
      }
   }

   public void display() {
      setFields(network.getParameters());
   }

   public void clearFields() {
      learningConstantField.clear();
      hiddenLayersField.clear();
      regressionThresholdField.clear();
      staleThresholdField.clear();
      acceptableErrorField.clear();
      acceptablePercentField.clear();
      acceptablePercentField.clear();

      for (TextField field : parameterTextFields)  {
         field.clear();
      }
   }

   public void setFields(Parameters params) {
      // Set up standard parameters.
      learningConstantField.setText(params.getParameter(Parameters.kLearningConstant).getValueString());
      regressionThresholdField.setText(params.getParameter(Parameters.kRegressionThreshold).getValueString());
      staleThresholdField.setText(params.getParameter(Parameters.kStaleThreshold).getValueString());
      acceptableErrorField.setText(params.getParameter(Parameters.kAcceptableTestError).getValueString());
      acceptablePercentField.setText(params.getParameter(Parameters.kAcceptablePercentCorrect).getValueString());

      // Set up hidden layers string.
      // String is formatted as a space delimited list of integers.
      StringBuilder sb = new StringBuilder();
      Integer[] layers = (Integer[])params.getParameterValue(Parameters.kHiddenLayerDepths);
      for (int i = 0; i < layers.length; ++i)
         sb.append(layers[i] + " ");
      hiddenLayersField.setText(sb.toString());

      currentActivationFunction = params.getActivationFunction();

      // Set up activation function dropdown.
      activationFunctionsField.setValue(
            params.getActivationFunction().getClass().getSimpleName());

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
         for (Class clazz : Registry.activationFunctionClasses) {
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
      setFields(network.getParameters());
   }

   public void save() {
      System.out.println("Save");
      String invalidField = "";
      try {
         /* Extract standard fields. */
         final double learning = Double.parseDouble(learningConstantField.getText());

         final double regression = Double.parseDouble(regressionThresholdField.getText());

         final int stale = Integer.parseInt(staleThresholdField.getText());

         final double error = Double.parseDouble(acceptableErrorField.getText());

         final double percent = Double.parseDouble(acceptablePercentField.getText());


         /* Extract hidden layers. */
         invalidField = "Hidden Layers";
         String[] tokens = hiddenLayersField.getText().split("\\s+");
         final Integer[] hidden = new Integer[tokens.length];
         for (int i = 0; i < hidden.length; ++i) {
            hidden[i] = Integer.parseInt(tokens[i]);
         }


         /* Identify activation function. */
         Class functionClass = null;

         // Identify class and extract parameter list.
         String functionName = (String) activationFunctionsField.getValue();
         for (Class clazz : Registry.activationFunctionClasses) {
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

         Parameters newParams = new Parameters();
         if (!newParams.setParameter(Parameters.kLearningConstant, learning))
            DialogFactory.displayErrorDialog(newParams.getParameter(Parameters.kLearningConstant).toString());
         if (!newParams.setParameter(Parameters.kHiddenLayerDepths, hidden))
            DialogFactory.displayErrorDialog(newParams.getParameter(Parameters.kHiddenLayerDepths).toString());
         if (!newParams.setParameter(Parameters.kStaleThreshold, stale))
            DialogFactory.displayErrorDialog(newParams.getParameter(Parameters.kStaleThreshold).toString());
         if (!newParams.setParameter(Parameters.kRegressionThreshold, regression))
            DialogFactory.displayErrorDialog(newParams.getParameter(Parameters.kRegressionThreshold).toString());
         if (!newParams.setParameter(Parameters.kAcceptableTestError, error))
            DialogFactory.displayErrorDialog(newParams.getParameter(Parameters.kAcceptableTestError).toString());
         if (!newParams.setParameter(Parameters.kAcceptablePercentCorrect, percent))
            DialogFactory.displayErrorDialog(newParams.getParameter(Parameters.kAcceptablePercentCorrect).toString());
         newParams.setActivationFunction(activ);

         network.setParameters(newParams);
         NetworkControllerStack.instance.pop();
      } catch (Exception e) {
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Invalid Parameters!");
         alert.setHeaderText("Invalid parameter: " + invalidField);
         alert.setContentText(null);
         alert.showAndWait();
      }
   }
}