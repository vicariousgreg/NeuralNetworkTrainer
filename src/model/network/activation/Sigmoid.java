package model.network.activation;

import java.io.Serializable;
import java.util.List;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class Sigmoid extends ActivationFunction {
   /** Sigmoid slope parameter. */
   private int slopeParameter;

   /**
    * Default constructor.
    */
   public Sigmoid() {
      this(1);
   }

   /**
    * Constructor.
    * @param slopeParameter sigmoid slope parameter
    */
   public Sigmoid(int slopeParameter) {
      this.slopeParameter = slopeParameter;
   }

   public static List<String> getParameters() {
      List<String> params = ActivationFunction.getParameters();
      params.add("Slope Parameter");
      return params;
   }

   @Override
   public String getValue(String param) {
      if (param.equals("Slope Parameter"))
         return Integer.toString(slopeParameter);
      else return "";
   }

   @Override
   public void setValue(String param, String value) throws Exception {
      if (param.equals("Slope Parameter"))
         slopeParameter = Integer.parseInt(value);
      else
         throw new Exception("Unrecognized parameter!");
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
