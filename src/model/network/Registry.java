package model.network;

import model.network.memory.*;
import model.network.schema.*;
import model.network.activation.*;

/**
 * Created by gpdavis on 6/4/15.
 */
public class Registry {
   public static final Class<? extends Schema>[] inputAdapterClasses =
         new Class[]{
               ColorInputAdapter.class,
         };

   public static final Class<? extends ActivationFunction>[] activationFunctionClasses =
         new Class[]{
               Sigmoid.class,
               SigmoidEstimate.class,
               SigmoidClip.class
         };

   public static final Class<? extends ActivationFunction>[] memoryModuleClasses =
         new Class[]{
               BasicMemoryModule.class,
               ShortTermMemoryModule.class,
               ClassifierMemoryModule.class
         };
}
