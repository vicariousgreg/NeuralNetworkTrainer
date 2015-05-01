package network;

import javafx.scene.Node;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a neural network experience, which consists of an input and
 * output vector.
 */
public class Experience implements Serializable {
   /** Experience input. */
   public final NetworkInput input;
   /** Input vector. */
   public final double[] inputVector;
   /** Experience output. */
   public final Object output;
   /** Output vector. */
   public final double[] outputVector;

   /**
    * Constructor.
    * @param in experience input object
    * @param out experience output object
    */
   public Experience(Schema schema, NetworkInput in, Object out) throws Exception {
      this.input = in;
      this.inputVector = in.inputVector;
      this.output = out;
      this.outputVector = schema.convertOutput(out);
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
    * Converts this memory to a JavaFX Node for rendering.
    * @param width node width
    * @param height node height
    * @return node
    */
   public Node toFXNode(double width, double height) {
      return input.toFXNode(width, height);
   }

   /**
    * Returns a string representation of this test case.
    * @return string representation
    */
   public String toString() {
      return "Input: " + input.toString() + "\n" +
         "Expected output: " + output.toString();
   }
}
