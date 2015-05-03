package model.network.memory;

import model.network.schema.Schema;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a neural network memory, which consists of an input and
 * output vector.
 */
public class Memory implements Serializable {
   /** Input vector. */
   public final double[] inputVector;
   /** Output vector. */
   public final double[] outputVector;

   /**
    * Constructor.
    * @param schema schema of memory
    * @param in experience input object
    * @param out experience output object
    */
   public Memory(Schema schema, Object in, Object out) throws Exception {
      this.inputVector = schema.encodeInput(in);
      this.outputVector = schema.encodeOutput(out);
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
