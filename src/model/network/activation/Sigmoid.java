package model.network.activation;

import model.network.parameters.BoundedParameter;
import model.network.parameters.Parameter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class Sigmoid extends ActivationFunction {
   /** Sigmoid slope parameter. */
   protected int slopeParameter;

   public static String kSlopeParameter = "Slope Parameter";
   public static Map<String, Parameter> defaultParameters;
   static {
      defaultParameters = new LinkedHashMap<String, Parameter>();
      defaultParameters.put(kSlopeParameter,
            new BoundedParameter<Integer>(kSlopeParameter, 1, 1, null));
   }

   /**
    * Default constructor.
    * @param params function parameters
    */
   public Sigmoid(Map<String, Parameter> params) {
      this.slopeParameter = (Integer) params.get(kSlopeParameter).getValue();
   }

   /**
    * Performs the Sigmoid calculation.
    * @param sigma input value
    * @return sigmoid value
    */
   @Override
   public double calculate(double sigma) {
      return 1 / (1 + Math.exp(-sigma * slopeParameter));
   }

   /**
    * Calculates the derivative of the sigmoid function given the result
    * of the sigmoid calculation.
    * @param out sigmoid calculation
    * @return derivative
    */
   @Override
   public double calculateDerivative(double out) {
      return out * (1 - out) * slopeParameter;
   }
}
