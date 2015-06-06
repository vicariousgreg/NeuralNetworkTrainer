package gui.controller.dialog;

import application.DialogFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Registry;
import model.network.Parameters;
import model.network.activation.ActivationFunction;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ParametersDialogController extends DialogController implements Initializable {
   @FXML GridPane grid;
   @FXML ComboBox activationFunctionsField;
   @FXML GridPane activationParametersGrid;

   private ArrayList<TextField> parameterTextFields;
   private ActivationFunction currentActivationFunction;
   private Map<String, Label> labels;
   private Map<String, TextField> fields;

   private Parameters params = new Parameters();

   public void initialize(URL location, ResourceBundle resources) {
      labels = new HashMap<String, Label>();
      fields = new HashMap<String, TextField>();
      parameterTextFields = new ArrayList<TextField>();

      // Set up activation functions dropdown.
      for (Class function : Registry.activationFunctionClasses) {
         activationFunctionsField.getItems().add(function.getSimpleName());
      }
   }

   public void setParameters(Parameters params) {
      this.params = params;
      reset();
   }

   public void clear() {
      for (String key : fields.keySet())
         fields.get(key).clear();
      for (TextField field : parameterTextFields)  {
         field.clear();
      }
   }

   public void reset() {
      if (params != null)
         setFields();
   }

   @Override
   public void confirm() {
      Parameters newParams = new Parameters();

      try {
         // Instantiate activation function.
         ActivationFunction activ;
         try {
            activ = buildActivationFunction();
         } catch (Exception e) {
            DialogFactory.displayErrorDialog(e.getMessage());
            return;
         }

         /* Extract regular parameters. */
         for (String key : fields.keySet()) {
            Object value = extractField(key);
            if (value == null || !newParams.setParameter(key, value))
               DialogFactory.displayErrorDialog(params.getParameter(key).toString());
         }

         newParams.setActivationFunction(activ);

         setResponseValue(newParams);
         close();
      } catch (Exception e) {
         DialogFactory.displayErrorDialog("Invalid Parameters!");
      }
   }

   private ActivationFunction buildActivationFunction() throws Exception {
      /* Identify activation function. */
      Class functionClass = null;

      // Identify class and extract parameter list.
      String functionName = (String) activationFunctionsField.getValue();
      for (Class clazz : Registry.activationFunctionClasses) {
         if (clazz.getSimpleName().equals(functionName)) {
            functionClass = clazz;
         }
      }

      ActivationFunction activ = (ActivationFunction) functionClass.newInstance();

      // Set activation function parameters.
      for (TextField text : parameterTextFields) {
         activ.setValue(text.getPromptText(), text.getText());
      }
      return activ;
   }

   private Object extractField(String key) {
      String valueString = fields.get(key).getText();
      Object value = null;
      Class clazz = params.getParameter(key).getValue().getClass();
      try {
         if (clazz.equals(Integer.class)) {
            value = Integer.parseInt(valueString);
         } else if (clazz.equals(Integer[].class)) {
            String[] tokens = fields.get(key).getText().split("\\s+");
            Integer[] arr = new Integer[tokens.length];
            for (int i = 0; i < arr.length; ++i) {
               arr[i] = Integer.parseInt(tokens[i]);
            }
            value = arr;
         } else if (clazz.equals(Double.class)) {
            value = Double.parseDouble(valueString);
         } else if (clazz.equals(Double[].class)) {
            String[] tokens = fields.get(key).getText().split("\\s+");
            Double[] arr = new Double[tokens.length];
            for (int i = 0; i < arr.length; ++i) {
               arr[i] = Double.parseDouble(tokens[i]);
            }
            value = arr;
         }
      } catch (Exception e) { }
      return value;
   }

   private void setFields() {
      labels.clear();
      fields.clear();

      grid.getChildren().clear();
      int ctr = 0;

      // Set up standard parameters.
      for (String key : Parameters.parametersList) {
         labels.put(key, new Label());
         labels.get(key).setText(key);
         fields.put(key, new TextField());
         fields.get(key).setText(params.getParameter(key).getValueString());
         grid.addRow(ctr++, labels.get(key), fields.get(key));
      }

      currentActivationFunction = params.getActivationFunction();

      // Set up activation function dropdown.
      activationFunctionsField.setValue(
            params.getActivationFunction().getClass().getSimpleName());

      // Set up activation function parameters.
      try {
         initializeActivationParameters();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void initializeActivationParameters() throws Exception{
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
   }
}
