package model.network.activation;

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

   /**
    * Map of values to Sigmoid outputs.
    * Inputs are multiplied by granularity so they are discrete.
    */
   private HashMap<Integer, Double> precalculated;

   /**
    * Default constructor.
    */
   public SigmoidEstimate() {
      this(1, 1000);
   }

   /**
    * Constructor.
    * @param slopeParameter slope parameter
    */
   public SigmoidEstimate(int slopeParameter) {
      this(slopeParameter, 1000);
   }

   /**
    * Constructor.
    * @param slopeParameter slope parameter
    * @param granularity granularity of estimation
    */
   public SigmoidEstimate(int slopeParameter, int granularity) {
      super(slopeParameter);
      this.granularity = granularity;
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

   @Override
   public String getValue(String param) {
      if (param.equals("Granularity"))
         return Integer.toString(granularity);
      else return super.getValue(param);
   }

   @Override
   public void setValue(String param, String value) throws Exception {
      if (param.equals("Granularity")) {
         int val = Integer.parseInt(value);
         if (val < 1) throw new Exception(String.format("Invalid %s: %s!", param, value));
         granularity = Integer.parseInt(value);
      } else {
         super.setValue(param, value);
      }

      initializeEstimations();
   }

   public static List<String> getParameters() {
      List<String> params = Sigmoid.getParameters();
      params.add("Granularity");
      return params;
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
