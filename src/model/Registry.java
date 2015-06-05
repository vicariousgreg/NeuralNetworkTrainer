package model;

import model.network.schema.*;
import model.network.activation.*;

/**
 * Created by gpdavis on 6/4/15.
 */
public class Registry {
   public static final Class<? extends Schema>[] schemaClasses =
         new Class[] {
               ColorSchema.class,
               AdvancedColorSchema.class
         };

   public static final Class<? extends ActivationFunction>[] activationFunctionClasses =
         new Class[] {
               Sigmoid.class,
               SigmoidEstimate.class,
               SigmoidClip.class
         };

   public static final Class[] inputClasses =
         new Class[] {
         };
}
