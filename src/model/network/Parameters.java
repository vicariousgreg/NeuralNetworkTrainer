package model.network;

import model.network.activation.*;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Neural network parameters.
 */
public class Parameters implements Serializable {
   /** Learning constant. */
   public double learningConstant;

   /**
    * The depths of the hidden layers.
    * The length corresponds to the number of hidden layers.
    */
   public int[] hiddenLayerDepths;

   /** Neuron activation function. */
   public ActivationFunction activationFunction;

   /**
    * The error threshold for network regression.
    * If the network's error increases by this much, it is reset.
    */
   public double regressionThreshold;

   /** The number of iterations the learning process can go through without
    * making significant improvements before the network is reset. */
   public int staleThreshold;

   /** Acceptable test error for learning termination. */
   public double acceptableTestError;

   /** Acceptable percentage correct for learning termination. */
   public double acceptablePercentCorrect;

   /** The name of the set activation function. */
   public String activationFunctionName;

   /** Activation function names to class map. */
   public static final Map<String, Class> activationFunctionNames =
      new HashMap<String, Class>();
   static {
      activationFunctionNames.put("Sigmoid", Sigmoid.class);
      activationFunctionNames.put("SigmoidClip", SigmoidClip.class);
      activationFunctionNames.put("SigmoidEstimate", SigmoidEstimate.class);
   }

   /**
    * Constructor.
    */
   public Parameters(double learningConstant, int[] hidden,
                     ActivationFunction activation, int staleThresh,
                     double regressionThresh, double acceptableError,
                     double acceptablePercentage) {
      this.learningConstant = learningConstant;
      this.hiddenLayerDepths = hidden;
      this.activationFunction = activation;
      this.regressionThreshold = regressionThresh;
      this.staleThreshold = staleThresh;
      this.acceptableTestError = acceptableError;
      this.acceptablePercentCorrect = acceptablePercentage;

      for (String key : activationFunctionNames.keySet()) {
         Class activClass = activationFunctionNames.get(key);
         if (activation.getClass().equals(activClass))
            this.activationFunctionName = key;
      }
   }

   /**
    * Default constructor.
    */
   public Parameters() {
      this(0.1,
         new int[] { 5 },
         new SigmoidEstimate(2, 1000),
         50,
         1000,
         100,
         80);
   }

   /**
    * Clones the network parameters.
    * @return clone of parameters
    */
   public Parameters clone() {
      return new Parameters(
         this.learningConstant,
         this.hiddenLayerDepths,
         this.activationFunction,
         this.staleThreshold,
         this.regressionThreshold,
         this.acceptableTestError,
         this.acceptablePercentCorrect);
   }
}
