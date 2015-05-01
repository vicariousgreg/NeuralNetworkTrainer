package network.activation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

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
    * @param slopeParameter slope parameter
    */
   public SigmoidClip(int slopeParameter) {
      super(slopeParameter);
      this.cutoff = (double) 8 / slopeParameter;
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
