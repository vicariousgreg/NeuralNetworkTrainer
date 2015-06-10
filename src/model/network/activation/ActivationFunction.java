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
