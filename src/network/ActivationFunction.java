package network;

import java.util.HashMap;
import java.util.Random;

/**
 * Interface for neuron activation function.
 * Supports calculation and derivative calculation with respect to calculation
 * results.  This only really makes sense for logistic functions.
 */
public interface ActivationFunction {
   /**
    * Performs calculation of activation function.
    * @param sigma input value
    * @return activation value
    */
   public double calculate(double sigma);

   /**
    * Calculates the derivative of the activation function given the result
    * of the activation function calculation.
    * @param out result of activation function calculation
    * @return derivative value
    */
   public double calculateDerivative(double out);
}
