package model.network.parameters;

import model.Registry;
import model.network.activation.*;
import model.network.memory.BasicMemoryModule;

import java.io.Serializable;
import java.util.*;

/**
 * Neural network parameters.
 */
public class Parameters implements Serializable {
   public static final String kLearningConstant = "Learning Constant";
   public static final String kHiddenLayerDepths = "Hidden Layer Depths";
   public static final String kRegressionThreshold = "Regression Threshold";
   public static final String kStaleThreshold = "Stale Threshold";
   public static final String kAcceptableTestError = "Acceptable Test Error";
   public static final String kAcceptablePercentCorrect = "Acceptable Test Percentage Correct";
   public static final String kIterationCap = "Training iteration cap";
   public static final String kLiveTraining = "Live Training";
   public static final String kMemoryModule = "Memory Module";
   public static final String kActivationFunction = "Activation Function";

   /** Parameters map. */
   private Map<String, Parameter> parameters;

   /**
    * Default constructor.
    */
   public Parameters() {
      parameters = new LinkedHashMap<String, Parameter>();
      /** Learning constant. */
      parameters.put(kLearningConstant,
            new BoundedParameter<Double>(kLearningConstant, 0.1, 0.0, 1.0));
      /**
       * The depths of the hidden layers.
       * The length corresponds to the number of hidden layers.
       */
      parameters.put(kHiddenLayerDepths,
            new ArrayParameter<Integer>(kHiddenLayerDepths,
                  new Integer[] { 3 }, 0, null));
      /**
       * The error threshold for network regression.
       * If the network's error increases by this much, it is reset.
       */
      parameters.put(kRegressionThreshold,
            new BoundedParameter<Double>(kRegressionThreshold, 50.0, 0.0, null));
      /** The number of iterations the learning process can go through without
       * making significant improvements before the network is reset. */
      parameters.put(kStaleThreshold,
            new BoundedParameter<Integer>(kStaleThreshold, 1000, 0, null));
      /** Acceptable test error for learning termination. */
      parameters.put(kAcceptableTestError,
            new BoundedParameter<Double>(kAcceptableTestError, 100.0, 1.0, null));
      /** Acceptable percentage correct for learning termination. */
      parameters.put(kAcceptablePercentCorrect,
            new BoundedParameter<Double>(kAcceptablePercentCorrect, 80.0, 0.0, 100.0));
      /** Acceptable percentage correct for learning termination. */
      parameters.put(kIterationCap,
            new BoundedParameter<Integer>(kIterationCap, 20000, 1, 1000000));
      /** Live training flag. */
      parameters.put(kLiveTraining,
            new BooleanParameter(kLiveTraining, true));

      /** Memory Module. */
      parameters.put(kMemoryModule,
            new ClassParameter(kAcceptablePercentCorrect,
                  BasicMemoryModule.class, Registry.memoryModuleClasses));

      /** Activation Function. */
      parameters.put(kActivationFunction,
            new ClassParameter(kActivationFunction,
                  SigmoidEstimate.class, Registry.activationFunctionClasses));
   }

   /**
    * Clone constructor.
    * @param map map to clone
    */
   private Parameters(Map<String, Parameter> map) {
      this.parameters = new LinkedHashMap<String, Parameter>();

      for (String key : map.keySet()) {
         this.parameters.put(key, map.get(key).clone());
      }
   }

   public Set<String> getParametersList() {
      return parameters.keySet();
   }

   /**
    * Clones the network parameters.
    * @return clone of parameters
    */
   public Parameters clone() {
      return new Parameters(parameters);
   }

   /**
    * Gets a parameter.
    * @param key parameter key
    * @return parameter object
    */
   public Parameter getParameter(String key) {
      return parameters.get(key);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("Parameters:\n");
      for (String key : parameters.keySet()) {
         sb.append(String.format("  { %s: %s }\n",
               key, parameters.get(key).getValueString()));
      }
      return sb.toString();
   }
}