package gui.controller.dialog;

import application.DialogFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import model.network.parameters.ClassParameter;
import model.network.parameters.EnumeratedParameter;
import model.network.parameters.Parameter;
import model.network.parameters.Parameters;

import java.net.URL;
import java.util.*;

public class ParametersDialogController extends DialogController implements Initializable {
   @FXML GridPane grid;

   private Map<Parameter, Control> controls;

   private Parameters params = new Parameters();

   public void initialize(URL location, ResourceBundle resources) {
      controls = new HashMap<Parameter, Control>();
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
   }

   public void reset() {
      if (params != null)
         setFields();
   }

   @Override
   public void confirm() {
      Parameters newParams = new Parameters();

      try {
         // Set up standard parameters.
         for (String key : params.getParametersList()) {
            Parameter param = params.getParameter(key);
            Object value = extractField(param);

            try {
               if (param instanceof ClassParameter) {
                  ClassParameter classParam = (ClassParameter) param;
                  Map<String, Parameter> newSubParams = new LinkedHashMap<String, Parameter>();

                  for (Parameter subParam : classParam.getSubParameters().values()) {
                     Parameter newSubParam = subParam.clone();
                     newSubParam.setValue(extractField(subParam));
                     newSubParams.put(subParam.name, newSubParam);
                  }

                  ((ClassParameter)newParams.getParameter(key)).setValue((Class)value, newSubParams);
               } else {
                  newParams.getParameter(key).setValue(value);
               }
            } catch (Exception e) {
               DialogFactory.displayErrorDialog(param.toString());
            }
         }

         setResponseValue(newParams);
         close();
      } catch (Exception e) {
         e.printStackTrace();
         DialogFactory.displayErrorDialog("Invalid Parameters!");
      }
   }

   private void setFields() {
      controls.clear();
      grid.getChildren().clear();

      // Set up standard parameters.
      for (String key : params.getParametersList()) {
         addParameter(grid, params.getParameter(key));
      }
   }

   private void addParameter(GridPane gridPane, Parameter param) {
      Object value = param.getValue();
      Label paramLabel = new Label(param.name);

      Parent parent;

      // Create combo boxes for enumerations, and text fields for all else.
      if (param instanceof EnumeratedParameter) {
         Object[] enumerations = ((EnumeratedParameter)param).getEnumerations();
         final ComboBox comboBox = new ComboBox();
         comboBox.setConverter(new StringConverter() {
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

         // Populate combo box with enumerated values.
         for (Object poss : enumerations) {
            (comboBox).getItems().add(poss);
         }
         comboBox.getSelectionModel().select(value);

         controls.put(param, comboBox);

         // Recursively handle class parameters with sub parameters.
         if (param instanceof ClassParameter) {
            final ClassParameter classParam = (ClassParameter) param;
            final GridPane subGrid = new GridPane();
            subGrid.add(comboBox, 0, 0, 2, 1);

            // Handle sub parameters.
            Map<String, Parameter> subParams = classParam.getSubParameters();
            for (Parameter subParam : subParams.values()) {
               addParameter(subGrid, subParam);
            }

            // Set up combo box listener to change sub parameter controls.
            comboBox.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent event) {
                  // Remove old sub parameters...
                  subGrid.getChildren().remove(1, subGrid.getChildren().size());
                  for (Parameter subParam : classParam.getSubParameters().values()) {
                     controls.remove(subParam);
                  }

                  // Change class parameter value...
                  classParam.setValue((Class)comboBox.getValue());

                  // Add new sub parameters...
                  for (Parameter subParam : classParam.getSubParameters().values()) {
                     addParameter(subGrid, subParam);
                  }
               }
            });

            parent = subGrid;
         } else {
            parent = comboBox;
         }
      } else {
         TextField textField = new TextField(param.getValueString());
         controls.put(param, textField);
         parent = textField;
      }

      gridPane.addRow(gridPane.getChildren().size(), paramLabel, parent);
   }

   private Object extractField(Parameter param) {
      String valueString = "";

      Control paramControl = controls.get(param);
      if (paramControl instanceof TextField)
         valueString = ((TextField)paramControl).getText();
      else if (paramControl instanceof ComboBox) {
         valueString = ((ComboBox)paramControl).getValue().toString();
      }

      Object value = null;
      Class parameterClass = param.getValue().getClass();
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
