package model.network;

import model.network.activation.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

   public static final List<String> parametersList = new ArrayList<String>();
   static {
      parametersList.add(kLearningConstant);
      parametersList.add(kHiddenLayerDepths);
      parametersList.add(kRegressionThreshold);
      parametersList.add(kStaleThreshold);
      parametersList.add(kAcceptableTestError);
      parametersList.add(kAcceptablePercentCorrect);
   }

   /** Neuron activation function. */
   private ActivationFunction activationFunction;

   /** Parameters map. */
   private Map<String, Parameter> parameters;

   /**
    * Default constructor.
    */
   public Parameters() {
      parameters = new HashMap<String, Parameter>();
      /** Learning constant. */
      parameters.put(kLearningConstant,
            new Parameter<Double>(kLearningConstant, 0.1, 0.0, 1.0));
      /**
       * The depths of the hidden layers.
       * The length corresponds to the number of hidden layers.
       */
      parameters.put(kHiddenLayerDepths,
            new Parameter<Integer[]>(kHiddenLayerDepths,
                  new Integer[] { 3 }, new Integer[] { 0 }, null));
      /**
       * The error threshold for network regression.
       * If the network's error increases by this much, it is reset.
       */
      parameters.put(kRegressionThreshold,
            new Parameter<Double>(kRegressionThreshold, 50.0, 0.0, null));
      /** The number of iterations the learning process can go through without
       * making significant improvements before the network is reset. */
      parameters.put(kStaleThreshold,
            new Parameter<Integer>(kStaleThreshold, 1000, 0, null));
      /** Acceptable test error for learning termination. */
      parameters.put(kAcceptableTestError,
            new Parameter<Double>(kAcceptableTestError, 100.0, 1.0, null));
      /** Acceptable percentage correct for learning termination. */
      parameters.put(kAcceptablePercentCorrect,
            new Parameter<Double>(kAcceptablePercentCorrect, 80.0, 0.0, 99.9));

      activationFunction = new SigmoidEstimate(1, 1000);
   }

   /**
    * Clone constructor.
    * @param map map to clone
    */
   private Parameters(Map<String, Parameter> map, ActivationFunction activ) {
      this.parameters = new HashMap<String, Parameter>();
      this.activationFunction = activ;

      for (String key : map.keySet()) {
         this.parameters.put(key, map.get(key).clone());
      }
   }

   /**
    * Clones the network parameters.
    * @return clone of parameters
    */
   public Parameters clone() {
      return new Parameters(parameters, activationFunction);
   }

   /**
    * Sets the activation function.
    * @param activ activation function
    */
   public void setActivationFunction(ActivationFunction activ) {
      this.activationFunction = activ;
   }

   public ActivationFunction getActivationFunction() {
      return activationFunction;
   }

   /**
    * Sets a parameter.
    * @param key parameter key
    * @param value new parameter value
    * @return whether the set was successful
    */
   public boolean setParameter(String key, Object value) {
      if (parametersList.contains(key)) {
         return parameters.get(key).setValue(value);
      }
      return false;
   }

   /**
    * Gets a parameter.
    * @param key parameter key
    * @return parameter object
    */
   public Parameter getParameter(String key) {
      if (parametersList.contains(key)) {
         return parameters.get(key);
      }
      return null;
   }

   /**
    * Gets the value of a parameter.
    * @param key parameter key
    * @return parameter value
    */
   public Object getParameterValue(String key){
      if (parametersList.contains(key)) {
         return parameters.get(key).getValue();
      }
      return null;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("Parameters:\n");
      for (String key : parametersList) {
         sb.append(String.format("  { %s: %s }\n",
               key, parameters.get(key).getValueString()));
      }

      try {
         Class activationClass = activationFunction.getClass();
         Method m = activationClass.getMethod("getParameters");
         sb.append(String.format("\n  { ActivationFunction: %s }\n",
               activationClass.getSimpleName()));
         for (String key : (List<String>) m.invoke(new Object[0])) {
            sb.append(String.format("  { %s: %s }\n",
                  key, activationFunction.getValue(key)));
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return sb.toString();
   }

   public class Parameter<T> implements Serializable {
      public final String name;
      private T value;
      public final T minimum;
      public final T maximum;
      public final T[] enumerations;

      public Parameter(String name, T value) {
         this.name = name;
         this.value = value;
         this.minimum = null;
         this.maximum = null;
         this.enumerations = null;
      }

      public Parameter(String name, T value, T[] enumerations) {
         this.name = name;
         this.value = value;
         this.minimum = null;
         this.maximum = null;
         this.enumerations = enumerations;
      }

      public Parameter(String name, T value, T minimum, T maximum) {
         this.name = name;
         this.value = value;
         this.minimum = minimum;
         this.maximum = maximum;
         this.enumerations = null;
      }

      public T getValue() {
         return value;
      }

      public boolean setValue(Object newValue) {
         if (newValue.getClass().equals(value.getClass())) {
            if (checkBounds((T)newValue) && checkEnumerations((T)newValue)) {
               this.value = (T)newValue;
               return true;
            }
         }
         return false;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder(name + ":");
         if (minimum != null) sb.append("\n  Minimum: " + minimum.toString());
         if (maximum != null) sb.append("\n  Maximum: " + maximum.toString());
         if (enumerations != null) {
            sb.append("\n  Enumeration: ");
            for (int i = 0; i < enumerations.length; ++i)
               sb.append(enumerations[i].toString() + " ");
         }

         return sb.toString();
      }

      public String getValueString() {
         if (value instanceof Object[]) {
            StringBuilder sb = new StringBuilder();
            for (Object o : (Object[]) value) {
               sb.append(o.toString() + " ");
            }
            return sb.toString().trim();
         }
         return value.toString();
      }

      public Parameter<T> clone() {
         Parameter<T> clone = null;
         if (enumerations != null)
            clone = new Parameter(name, value, enumerations);
         else
            clone = new Parameter(name, value, minimum, maximum);
         return clone;
      }

      private boolean checkBounds(T newValue) {
         if (newValue instanceof Object[]) {
            Object[] arr = (Object[]) newValue;

            for (int i = 0; i < arr.length; ++i) {
               Comparable compNew = (Comparable) arr[i];

               if (minimum != null) {
                  Comparable compMin = ((Comparable[]) minimum)[0];
                  if (compNew.compareTo(compMin) < 0) return false;
               }
               if (maximum != null) {
                  Comparable compMax = ((Comparable[]) maximum)[0];
                  if (compNew.compareTo(compMax) > 0) return false;
               }
            }
         } else {
            Comparable compNew = (Comparable) newValue;

            if (minimum != null) {
               Comparable compMin = (Comparable) minimum;
               if (compNew.compareTo(compMin) < 0) return false;
            }
            if (maximum != null) {
               Comparable compMax = (Comparable) maximum;
               if (compNew.compareTo(compMax) > 0) return false;
            }
         }

         return true;
      }

      private boolean checkEnumerations(T newValue) {
         if (enumerations != null) {
            boolean valid = false;

            for (int i = 0; i < enumerations.length; ++i) {
               if (enumerations[i].equals(newValue))
                  valid = true;
            }
            if (!valid) return false;
         }
         return true;
      }
   }
}
