package network;

import java.io.Serializable;

/**
 * Neural network parameters.
 */
public class NetworkParameters implements Serializable {
   /** Learning constant. */
   public double learningConstant;

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
   public NetworkParameters(double learningConstant, int staleThresh,
         double regressionThresh, double acceptableError, double acceptablePercentage) {
      this.learningConstant = learningConstant;
      this.regressionThreshold = regressionThresh;
      this.staleThreshold = staleThresh;
      this.acceptableTestError = acceptableError;
      this.acceptablePercentCorrect = acceptablePercentage;
   }

   /**
    * Default constructor.
    */
   public NetworkParameters() {
      this.learningConstant = 0.1;
      this.regressionThreshold = 50;
      this.staleThreshold = 1000;
      this.acceptableTestError = 100;
      this.acceptablePercentCorrect = 60;
   }
}
