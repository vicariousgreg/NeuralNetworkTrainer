package network;

import java.util.Arrays;

/**
 * Represents a neural network test case.
 */
public class TestCase {
   /** Test case inputs. */
   public final double[] inputs;
   /** Expected outputs. */
   public final double[] outputs;

   /**
    * Constructor.
    * @param inputs test case inputs
    * @param outputs test case expected outputs
    */
   public TestCase(double[] inputs, double[] outputs) {
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
