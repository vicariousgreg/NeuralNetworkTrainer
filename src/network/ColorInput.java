package network;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

/**
 * Color input class.
 */
public class ColorInput extends NetworkInput {
   /** Red channel. */
   public final double red;
   /** Green channel. */
   public final double green;
   /** Blue channel. */
   public final double blue;

   /**
    * Constructor.
    * Uses input vector.
    * @param inputVector input vector
    */
   public ColorInput(double[] inputVector) throws Exception {
      super(inputVector);

      for (int i = 0; i < inputVector.length; ++i) {
         if (Double.compare(inputVector[i], 1.0) > 0 ||
             Double.compare(inputVector[i], 0.0) < 0)
            throw new Exception("Invalid color parameters!");
      }
      red = inputVector[0];
      green = inputVector[1];
      blue = inputVector[2];
   }

   /**
    * Constructor.
    * @param red red channel
    * @param green green channel
    * @param blue blue channel
    * @throws Exception if colors are not between 0.0 and 1.0
    */
   public ColorInput(double red, double green, double blue) throws Exception{
      this(new double[] { red, green, blue });
   }

   /**
    * Constructor.
    * Uses JavaFX color.
    * @param fxColor JavaFX color
    */
   public ColorInput(javafx.scene.paint.Color fxColor) throws Exception {
      this(fxColor.getRed(), fxColor.getGreen(), fxColor.getBlue());
   }

   @Override
   public int getInputSize() {
      return 3;
   }

   @Override
   public Node toFXNode(double width, double height) {
      Rectangle rect = new Rectangle(width, height);
      javafx.scene.paint.Color color =
         new javafx.scene.paint.Color(red, green, blue, 1.0);
      rect.setFill(color);
      return rect;
   }
}
