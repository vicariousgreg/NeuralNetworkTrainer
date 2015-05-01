package network.activation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class Sigmoid implements ActivationFunction, Serializable {
   /** Sigmoid slope parameter. */
   private final int slopeParameter;

   /**
    * Constructor.
    * @param slopeParameter sigmoid slope parameter
    */
   public Sigmoid(int slopeParameter) {
      this.slopeParameter = slopeParameter;
   }

   /**
    * Performs the Sigmoid calculation.
    * @param sigma input value
    * @return sigmoid value
    */
   public double calculate(double sigma) {
      return 1 / (1 + Math.exp(-sigma * slopeParameter));
   }

   /**
    * Calculates the derivative of the sigmoid function given the result
    * of the sigmoid calculation.
    * @param out sigmoid calculation
    * @return derivative
    */
   public double calculateDerivative(double out) {
      return out * (1 - out) * slopeParameter;
   }
}
