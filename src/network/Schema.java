package network;

import java.io.Serializable;

/**
 * Created by gpdavis on 4/29/15.
 */
public abstract class Schema implements Serializable {
   /** Size of input vector. */
   public final int inputSize;

   /** Size of coutput vector. */
   public final int outputSize;

   /** Output classifications. */
   public final Object[] classifications;

   /**
    * Cosntructor.
    * @param classifications output classifications
    */
   public Schema(int inputSize, Object[] classifications) {
      this.inputSize = inputSize;
      this.outputSize = classifications.length;
      this.classifications = classifications;
   }

   /**
    * Converts an input object to a vector.
    * @param in input object
    * @return network input vector
    */
   public abstract double[] convertInput(Object in) throws Exception;

   /**
    * Converts an output string to an output vector.
    * @param out output string
    * @return output vector
    */
   public abstract double[] convertOutput(Object out) throws Exception;

   /**
    * Converts an input vector to an input object.
    * @param in input vector
    * @return input object
    */
   public abstract Object translateInput(double[] in) throws Exception;

   /**
    * Translate an output vector to a meaningful output.
    * @param out output vector
    * @return Object output result
    */
   public Object translateOutput(double[] out) throws Exception {
      if (out.length != classifications.length)
         throw new Exception ("Invalid output vector!");

      double max = out[0];
      int maxIndex = 0;

      // Find the output with the highest probability.
      for (int index = 1; index < out.length; ++index) {
         if (out[index] > max) {
            max = out[index];
            maxIndex = index;
         }
      }
      return classifications[maxIndex];
   }

   /**
    * Creates an experience from an input object and output result.
    * @param in input object
    * @param out output result
    * @return experience
    */
   public Experience createExperience(Object in, Object out) throws Exception {
      return new Experience(convertInput(in), convertOutput(out));
   }
}
