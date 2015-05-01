package network;

import java.io.Serializable;

/**
 * Neural network parameters.
 */
public class NetworkParameters implements Serializable {
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

   /**
    * Constructor.
    */
   public NetworkParameters(double learningConstant, int[] hidden,
         ActivationFunction activation, int staleThresh,
         double regressionThresh, double acceptableError,
         double acceptablePercentage) {
      this.learningConstant = learningConstant;
      this.hiddenLayerDepths = hidden;
      this.activationFunction = new SigmoidEstimate(2, 100, 10);
      this.regressionThreshold = regressionThresh;
      this.staleThreshold = staleThresh;
      this.acceptableTestError = acceptableError;
      this.acceptablePercentCorrect = acceptablePercentage;
   }

   /**
    * Default constructor.
    */
   public NetworkParameters() {
      this(0.1,
         new int[] { 5 },
         new SigmoidEstimate(1, 1000, 10),
         50,
         1000,
         100,
         80);
   }
}
