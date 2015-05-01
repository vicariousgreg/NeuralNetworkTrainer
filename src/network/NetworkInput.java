package network;

import javafx.scene.Node;

import java.io.Serializable;

public abstract class NetworkInput implements Serializable {
   /** Input vector. */
   public final double[] inputVector;

   /**
    * Constructor.
    * @param inputVector input vector
    */
   public NetworkInput(double[] inputVector) throws Exception {
      if (inputVector.length != getInputSize())
         throw new Exception("Invalid input vector length!");
      this.inputVector = inputVector;
   }

   /**
    * Gets this input's vector size
    * @return input vector size
    */
   public abstract int getInputSize();

   /**
    * Converts this input to a JavaFX Node for rendering.
    * @param width width of node
    * @param height height of node
    * @return javaFX node
    */
   public abstract Node toFXNode(double width, double height);
}
