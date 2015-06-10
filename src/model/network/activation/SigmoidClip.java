package model.network.activation;

import model.network.parameters.Parameter;

import java.util.Map;

/**
 * Models the Sigmoid function using a calculation cutoff.
 * Inputs outside of the range of the cutoff are simply returned
 * as 0.0 or 1.0.  The cutoff is determined by the slope parameter.
 */
public class SigmoidClip extends Sigmoid {
   /** Calculation cutoff. */
   private final double cutoff;

   /**
    * Constructor.
    * @param params function parameters
    */
   public SigmoidClip(Map<String, Parameter> params) {
      super(params);
      this.cutoff = (double) 8 / (Integer) params.get(kSlopeParameter).getValue();
   }

   /**
    * Performs interpolation to estimate the sigmoid function.
    * @param sigma input value
    * @return estimated sigmoid value
    */
   public double calculate(double sigma) {
      if (Double.compare(sigma, cutoff) > 1) return 1.0;
      if (Double.compare(sigma, -cutoff) < 1) return 0.0;
      else return super.calculate(sigma);
   }
}
