package model.network.activation;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Interface for neuron activation function.
 * Supports calculation and derivative calculation with respect to calculation
 * results.  This only really makes sense for logistic functions.
 */
public abstract class ActivationFunction implements Serializable {
   /**
    * Returns a list of parameter names.
    * To be overridden by subclasses (cannot be enforced by Java).
    * @return parameter names
    */
   public static List<String> getParameters() {
      return new ArrayList<String>();
   }

   /**
    * Gets the value of a parameter by name.
    * @param param activation parameter
    * @return value of parameter
    */
   public abstract String getValue(String param);

   /**
    * Sets the value of a parameter by name.
    * @param param activation parameter
    * @param value value
    */
   public abstract void setValue(String param, String value) throws Exception;

   /**
    * Performs calculation of activation function.
    * @param sigma input value
    * @return activation value
    */
   public abstract double calculate(double sigma);

   /**
    * Calculates the derivative of the activation function given the result
    * of the activation function calculation.
    * @param out result of activation function calculation
    * @return derivative value
    */
   public abstract double calculateDerivative(double out);
}
