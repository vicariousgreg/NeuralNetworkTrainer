package network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class SigmoidEstimate implements ActivationFunction, Serializable {
   /** Estimation granularity in points per integer. */
   private final int granularity;

   /** Estimation bounds. */
   private final int estimationBounds;

   /** Sigmoid function being estimated. */
   private final Sigmoid sigmoid;

   /**
    * Map of values to Sigmoid outputs.
    * Inputs are multiplied by granularity so they are discrete.
    */
   private HashMap<Integer, Double> precalculated;

   /**
    * Constructor.
    * @param slopeParameter slope parameter
    * @param granularity granularity of estimation
    * @param bounds bounds for estimation
    */
   public SigmoidEstimate(int slopeParameter, int granularity, int bounds) {
      this.granularity = granularity;
      this.estimationBounds = (int) Math.ceil((double) 7 / slopeParameter);
      this.precalculated = new HashMap<Integer, Double>();

      sigmoid = new Sigmoid(slopeParameter);

      // Precalculate estimation values.
      for (int i = -estimationBounds; i <= estimationBounds; ++i) {
         for (int tick = 0; tick < granularity; ++tick) {
            this.precalculated.put(i * granularity + tick,
               sigmoid.calculate((double) i + (double)tick / granularity));
         }
      }
   }

   /**
    * Performs interpolation to estimate the sigmoid function.
    * @param sigma input value
    * @return estimated sigmoid value
    */
   public double calculate(double sigma) {
      try {
         if (Double.compare(sigma, estimationBounds) > 0) return 1.0;
         if (Double.compare(sigma, -estimationBounds) < 0) return -1.0;

         // Calculate interpolation bounds.
         int x0 = (int) Math.floor(sigma * granularity);
         int x1 = (int) Math.ceil(sigma * granularity);

         // Calculate interpolation values.
         double y0 = precalculated.get(x0);
         double y1 = precalculated.get(x1);
         return interpolate(sigma, y0, y1,
            (double)x0 / granularity, (double)x1 / granularity);
      } catch (Exception e) {
         System.out.println("Sigmoid failed on input " + sigma);
         System.exit(0);
         return 0.0;
      }
   }

   /**
    * Calculates the derivative of the sigmoid function given the result
    * of the sigmoid calculation.
    * @param out sigmoid calculation
    * @return derivative
    */
   public double calculateDerivative(double out) {
      return sigmoid.calculateDerivative(out);
   }

   /**
    * Calculates a linear interpolation.
    * @param x input
    * @param y0 left y value
    * @param y1 right y value
    * @param x0 left x value
    * @param x1 right x value
    * @return interpolated result
    */
   private double interpolate(double x, double y0, double y1,
                                     double x0, double x1) {
      return y0 + ((y1 - y0) * ((x - x0) / (x1 - x0)));
   }
}
