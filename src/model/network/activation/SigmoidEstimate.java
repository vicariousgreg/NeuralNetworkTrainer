package model.network.activation;

import model.network.parameters.BoundedParameter;
import model.network.parameters.Parameter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Models the Sigmoid function using precalculated values to speed up computation.
 */
public class SigmoidEstimate extends Sigmoid {
   /** Estimation granularity in points per integer. */
   private int granularity;

   /** Estimation bounds. */
   private int estimationBounds;

   private static String kSlopeParameter = "Slope Parameter";
   private static String kGranularity = "Granularity";
   public static Map<String, Parameter> defaultParameters;
   static {
      defaultParameters = new LinkedHashMap<String, Parameter>();
      defaultParameters.put(kSlopeParameter,
            new BoundedParameter<Integer>(kSlopeParameter, 1, 1, null));
      defaultParameters.put(kGranularity,
            new BoundedParameter<Integer>(kGranularity, 1000, 1, null));
   }

   /**
    * Map of values to Sigmoid outputs.
    * Inputs are multiplied by granularity so they are discrete.
    */
   private HashMap<Integer, Double> precalculated;

   /**
    * Constructor.
    * @param params function parameters
    */
   public SigmoidEstimate(Map<String, Parameter> params) {
      super(params);
      this.granularity = (Integer) params.get(kGranularity).getValue();
      this.estimationBounds = (int) Math.ceil((double) 7 / slopeParameter);
      initializeEstimations();
   }

   private void initializeEstimations() {
      this.precalculated = new HashMap<Integer, Double>();

      // Precalculate estimation values.
      for (int i = -estimationBounds; i <= estimationBounds; ++i) {
         for (int tick = 0; tick < granularity; ++tick) {
            this.precalculated.put(i * granularity + tick,
               super.calculate((double) i + (double)tick / granularity));
         }
      }
   }

   /**
    * Performs interpolation to estimate the sigmoid function.
    * @param sigma input value
    * @return estimated sigmoid value
    */
   @Override
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
