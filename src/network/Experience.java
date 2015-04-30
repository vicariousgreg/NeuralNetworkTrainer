package network;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a neural network experience, which consists of an input and
 * output vector.
 */
public class Experience implements Serializable {
   /** Test case inputs. */
   public final double[] inputs;
   /** Expected outputs. */
   public final double[] outputs;
   /** The schema of the experience. */
   public final Schema schema = null;

   /**
    * Constructor.
    * @param inputs test case inputs
    * @param outputs test case expected outputs
    */
   public Experience(double[] inputs, double[] outputs) {
      this.inputs = inputs;
      this.outputs = outputs;
   }

   /**
    * Returns a string representation of this test case.
    * @return string representation
    */
   public String toString() {
      return "Input: " + Arrays.toString(inputs) + "\n" +
         "Expected output: " + Arrays.toString(outputs);
   }
}
