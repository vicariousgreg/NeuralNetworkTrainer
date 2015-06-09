package gui.controller.dialog;

import application.DialogFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
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
   private Map<String, Control> controls;

   private Parameters params = new Parameters();

   public void initialize(URL location, ResourceBundle resources) {
      labels = new HashMap<String, Label>();
      controls = new HashMap<String, Control>();
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
      for (Control control : controls.values()) {
         if (control instanceof TextField)
            ((TextField) control).clear();
         else if (control instanceof ComboBox)
            ((ComboBox) control).getSelectionModel().select(null);
      }

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
         for (String key : controls.keySet()) {
            Object value = extractField(key);
            if (value == null || !newParams.setParameter(key, value))
               DialogFactory.displayErrorDialog(params.getParameter(key).toString());
         }

         newParams.setActivationFunction(activ);

         setResponseValue(newParams);
         close();
      } catch (Exception e) {
         e.printStackTrace();
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

   private void setFields() {
      labels.clear();
      controls.clear();

      grid.getChildren().clear();
      int ctr = 0;

      // Set up standard parameters.
      for (String key : params.getParametersList()) {
         Object value = params.getParameter(key).getValue();
         Object[] enumerations = params.getParameter(key).getEnumerations();
         Label paramLabel = new Label(key);
         labels.put(key, paramLabel);

         Control paramControl;

         // Create combo boxes for enumerations, and text fields for all else.
         if (enumerations != null) {
            paramControl = new ComboBox();
            ((ComboBox)paramControl).setConverter(new StringConverter() {
               @Override
               public String toString(Object o) {
                  if (o == null) {
                     return null;
                  } else if (o instanceof Class) {
                     return ((Class)o).getSimpleName();
                  } else {
                     return o.toString();
                  }
               }

               @Override
               public Object fromString(String string) {
                  return null;
               }
            });

            for (Object poss : enumerations) {
               ((ComboBox) paramControl).getItems().add(poss);
            }
            ((ComboBox) paramControl).getSelectionModel().select(value);
         } else {
            paramControl = new TextField(params.getParameter(key).getValueString());
         }

         controls.put(key, paramControl);
         grid.addRow(ctr++, paramLabel, paramControl);
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

   public void initializeActivationParameters() throws Exception{
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

   private Object extractField(String key) {
      String valueString = "";

      Control paramControl = controls.get(key);
      if (paramControl instanceof TextField)
         valueString = ((TextField)paramControl).getText();
      else if (paramControl instanceof ComboBox) {
         valueString = ((ComboBox)paramControl).getValue().toString();
      }

      Object value = null;
      Class parameterClass = params.getParameter(key).getValue().getClass();
      try {
         if (paramControl instanceof TextField) {
            if (parameterClass.equals(Integer.class)) {
               value = Integer.parseInt(valueString);
            } else if (parameterClass.equals(Integer[].class)) {
               String[] tokens = valueString.split("\\s+");
               Integer[] arr = new Integer[tokens.length];
               for (int i = 0; i < arr.length; ++i) {
                  arr[i] = Integer.parseInt(tokens[i]);
               }
               value = arr;
            } else if (parameterClass.equals(Double.class)) {
               value = Double.parseDouble(valueString);
            } else if (parameterClass.equals(Double[].class)) {
               String[] tokens = valueString.split("\\s+");
               Double[] arr = new Double[tokens.length];
               for (int i = 0; i < arr.length; ++i) {
                  arr[i] = Double.parseDouble(tokens[i]);
               }
               value = arr;
            }
         } else if (paramControl instanceof ComboBox) {
            value = ((ComboBox) paramControl).getValue();
         }
      } catch (Exception e) { }
      return value;
   }
}
