package network;

/**
 * Created by gpdavis on 4/29/15.
 */
public abstract class Schema {
   /** Size of input vector. */
   protected final int inputSize;

   /** Output classifications. */
   protected final String[] classifications;

   /**
    * Cosntructor.
    * @param classifications output classifications
    */
   public Schema(int inputSize, String[] classifications) {
      this.inputSize = inputSize;
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
   public abstract double[] convertOutput(String out) throws Exception;

   /**
    * Converts an input vector to an input object.
    * @param in input vector
    * @return input object
    */
   public abstract Object translateInput(double[] in) throws Exception;

   /**
    * Translate an output vector to a meaningful output.
    * @param out output vector
    * @return String output result
    */
   public String translateOutput(double[] out) throws Exception {
      if (out.length != classifications.length)
         throw new Exception ("Invalid output vector!");

      double max = out[0];
      int maxIndex = 0;

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
   public Experience createExperience(Object in, String out) throws Exception {
      return new Experience(convertInput(in), convertOutput(out));
   }
}
