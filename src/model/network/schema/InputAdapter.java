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
public abstract class InputAdapter implements Serializable {
   /** Input vector size. */
   public final int inputSize;

   /**
    * Constructor.
    * @param inputSize input vector size
    */
   public InputAdapter(int inputSize) {
      this.inputSize = inputSize;
   }

   /**
    * Checks if an object can be converted by this adapter.
    * @param in input object
    * @return if the object is a valid input
    */
   public abstract boolean validInput(Object in);

   /**
    * Recreates an input object given an input vector.
    * @param inputVector input vector
    * @return recreated input object
    * @throws Exception if input vector is improper length
    */
   public abstract Object recreateInput(double[] inputVector) throws Exception;

   /**
    * Converts an input object to an input vector.
    * @param in input object
    * @return input vector
    * @throws Exception if input is invalid
    */

   public abstract double[] encode(Object in) throws Exception;

   /**
    * Converts an input object to a JavaFX Node for rendering.
    * @param in input object
    * @param width width of node
    * @param height height of node
    * @return javaFX node
    */
   public abstract Node toFXNode(Object in, double width, double height) throws Exception;

   @Override
   public String toString() {
      return getClass().getSimpleName();
   }
}
