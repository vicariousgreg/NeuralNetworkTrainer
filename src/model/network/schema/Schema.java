package model.network.schema;

import javafx.scene.Node;
import javafx.scene.text.Text;
import model.network.memory.Memory;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by gpdavis on 4/29/15.
 */
public class Schema implements Serializable {
   /** Input adapter. */
   public final InputAdapter inputAdapter;

   /** Size of input vector. */
   public final int inputSize;

   /** Size of output vector. */
   public final int outputSize;

   /** Output classifications. */
   public final Object[] classifications;

   /**
    * Default constructor.
    * Assumes double[] inputs.
    * @param classifications
    */
   public Schema(Object[] classifications) {
      this(null, classifications);
   }

   /**
    * Constructor.
    * @param inputAdapter input adapter
    * @param classifications output classifications
    */
   public Schema(InputAdapter inputAdapter, Object[] classifications) {
      this.inputAdapter = inputAdapter;
      this.inputSize = inputAdapter.inputSize;
      this.outputSize = classifications.length;
      this.classifications = classifications;
   }

   /**
    * Returns a copy of this schema's classification list.
    * @return classification list
    */
   public final Object[] getOutputClassifications() {
      Object[] copy = new Object[classifications.length];
      System.arraycopy(classifications, 0, copy, 0, copy.length);
      return copy;
   }

   /**
    * Checks whether this schema supports the given object's class.
    * @param obj input object
    * @return whether the object class is supported
    */
   public final boolean validInput(Object obj) {
      if (inputAdapter == null)
         return obj instanceof double[] && ((double[])obj).length == inputSize;
      else
         return inputAdapter.validInput(obj);
   }

   /**
    * Converts an input object to an input vector.
    * Wrapper to handle double[] passing.
    * @param in input object
    * @return input vector
    */
   public final double[] encodeInput(Object in) throws Exception {
      if (in instanceof double[] && ((double[])in).length == inputSize) return (double[]) in;
      else return encode(in);
   }

   /**
    * Recreates an input object given an input vector.
    * @param inputVector input vector
    * @return recreated input object
    * @throws Exception if input vector is improper length
    */
   public Object recreateInput(double[] inputVector) throws Exception {
      if (inputVector.length != inputSize)
         throw new Exception ("Invalid input vector length!");

      if (inputAdapter == null)
         return inputVector;
      else
         return inputAdapter.recreateInput(inputVector);
   }

   /**
    * Converts an input object to an input vector.
    * @param in input object
    * @return input vector
    */

   public double[] encode(Object in) throws Exception {
      return inputAdapter.encode(in);
   }

   /**
    * Converts an output string to an output vector.
    * @param out output string
    * @return output vector
    */
   public final double[] encodeOutput(Object out) throws Exception {
      double[] outputVector = new double[classifications.length];

      for (int i = 0; i < classifications.length; ++i)
         if (out.equals(classifications[i])) {
            outputVector[i] = 1.0;
            return outputVector;
         }

      throw new Exception("Output object is not recognized by this schema.");
   }

   /**
    * Translate an output vector to a meaningful output.
    * @param out output vector
    * @return Object output result
    */
   public final Object translateOutput(double[] out) throws Exception {
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
    * Checks whether a memory fits this schema.
    * @param mem memory to check
    * @return whether the memory fits the schema
    */
   public final boolean fits(Memory mem) {
      return (mem.inputVector.length == inputSize &&
         mem.outputVector.length == outputSize);
   }

   /**
    * Creates a memory from an input object and output result.
    * @param in input object
    * @param out output result
    * @return memory
    */
   public final Memory createMemory(Object in, Object out) throws Exception {
      // Ensure valid input and output objects.
      if (!validInput(in))
         throw new Exception("Input object is not recognized by this schema.");
      if (!Arrays.asList(classifications).contains(out))
         throw new Exception("Output object is not recognized by this schema.");

      return new Memory(this, in, out);
   }

   /**
    * Converts a memory to a JavaFX Node for rendering.
    * @param in input object
    * @param width width of node
    * @param height height of node
    * @return javaFX node
    */
   public Node toFXNode(Object in, double width, double height) throws Exception {
      if (inputAdapter == null) {
         if (!(in instanceof double[]) || ((double[])in).length != inputSize)
            throw new Exception ("Invalid input vector length!");

         StringBuilder sb = new StringBuilder();
         for (double inValue : (double[])in)
            sb.append(inValue + "\n");
         return new Text(sb.toString());
      } else {
         return inputAdapter.toFXNode(in, width, height);
      }
   }
}
