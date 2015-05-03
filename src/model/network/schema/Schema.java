package model.network.schema;

import javafx.scene.Node;
import model.network.memory.Memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gpdavis on 4/29/15.
 */
public abstract class Schema implements Serializable {
   /** Classes supported by this schema. */
   public final List<Class> inputClasses;

   /** Size of input vector. */
   public final int inputSize;

   /** Size of output vector. */
   public final int outputSize;

   /** Output classifications. */
   public final Object[] classifications;

   /**
    * Constructor.
    * @param inputClasses objects recognized as input
    * @param inputSize the number of doubles that inputs are converted into
    * @param classifications output classifications
    */
   public Schema(Class[] inputClasses, int inputSize, Object[] classifications) {
      this.inputClasses = new ArrayList<Class>();
      this.inputClasses.addAll(Arrays.asList(inputClasses));
      this.inputClasses.add(double[].class);
      this.inputSize = inputSize;
      this.outputSize = classifications.length;
      this.classifications = classifications;
   }

   /**
    * Returns a list of classes supported by this schema.
    * @return supported class list
    */
   public final Class[] getSupportedClasses() {
      return inputClasses.toArray(new Class[inputClasses.size()]);
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
      return inputClasses.contains(obj.getClass());
   }

   /**
    * Converts an input object to an input vector.
    * Wrapper to handle double[] passing.
    * @param in input object
    * @return input vector
    */
   public final double[] encodeInput(Object in) throws Exception {
      if (in instanceof double[]) return (double[]) in;
      else return encode(in);
   }

   /**
    * Recreates an input object given an input vector.
    * @param inputVector input vector
    * @return recreated input object
    */
   public abstract Object recreateInput(double[] inputVector);

   /**
    * Converts an input object to an input vector.
    * @param in input object
    * @return input vector
    */

   public abstract double[] encode(Object in) throws Exception;

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
    * @param memory memory
    * @param width width of node
    * @param height height of node
    * @return javaFX node
    */
   public abstract Node toFXNode(Memory memory, double width, double height) throws Exception;
}
