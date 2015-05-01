package model.network;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a neural network experience, which consists of an input and
 * output vector.
 */
public class Experience implements Serializable {
   /** Input vector. */
   public final double[] inputVector;
   /** Output vector. */
   public final double[] outputVector;

   /**
    * Constructor.
    * @param in experience input object
    * @param out experience output object
    */
   public Experience(Schema schema, Object in, Object out) throws Exception {
      this.inputVector = schema.encodeInput(in);
      this.outputVector = schema.encodeOutput(out);
   }

   /**
    * Gets the input vector for this experience.
    * @return input vector
    */
   public double[] getInputVector() {
      return inputVector;
   }

   /**
    * Gets the output vector for this experience.
    * @return output vector
    */
   public double[] getOutputVector() {
      return outputVector;
   }

   /**
    * Returns a string representation of this test case.
    * @return string representation
    */
   public String toString() {
      return "Input: " + Arrays.toString(inputVector) + "\n" +
         "Expected output: " + Arrays.toString(outputVector);
   }
}
