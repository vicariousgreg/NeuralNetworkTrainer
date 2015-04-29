package network;

import java.util.HashMap;
import java.util.Random;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class Sigmoid {
   /** Estimation granularity in points per integer. */
   private static final int kGranularity = 1000;

   /** Estimation bounds. */
   private static final int kEstimationBounds = 10;

   /**
    * Map of values to Sigmoid outputs.
    * Inputs are multiplied by granularity so they are discrete.
    */
   private static HashMap<Integer, Double> precalculated;
   static {
      precalculated = new HashMap<Integer, Double>();

      for (int i = -kEstimationBounds; i <= kEstimationBounds; ++i) {
         for (int tick = 0; tick < kGranularity; ++tick) {
            precalculated.put(i * kGranularity + tick,
               trueCalculate((double) i + (double)tick / kGranularity));
         }
      }
   }

   /**
    * Performs an actual Sigmoid calculation.
    * @param x input value
    * @return sigmoid value
    */
   public static double trueCalculate(double x) {
      return 1 / (1 + Math.exp(-x));
   }

   /**
    * Performs interpolation to estimate the sigmoid function.
    * @param x input value
    * @return estimated sigmoid value
    */
   public static double calculate(double x) {
      try {
         if (Double.compare(x, kEstimationBounds) > 0) return 1.0;
         if (Double.compare(x, -kEstimationBounds) < 0) return -1.0;

         // Calculate interpolation bounds.
         int x0 = (int) Math.floor(x * kGranularity);
         int x1 = (int) Math.ceil(x * kGranularity);

         // Calculate interpolation values.
         double y0 = precalculated.get(x0);
         double y1 = precalculated.get(x1);
         return interpolate(x, y0, y1,
            (double)x0 / kGranularity, (double)x1 / kGranularity);
      } catch (Exception e) {
         System.out.println("Sigmoid failed on input " + x);
         System.exit(0);
         return 0.0;
      }
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
   private static double interpolate(double x, double y0, double y1,
                                     double x0, double x1) {
      return y0 + ((y1 - y0) * ((x - x0) / (x1 - x0)));
   }
}
