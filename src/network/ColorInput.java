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
    * @param red red channel
    * @param green green channel
    * @param blue blue channel
    * @throws Exception if colors are not between 0.0 and 1.0
    */
   public ColorInput(double red, double green, double blue) throws Exception{
      super(new double[] { red, green, blue });

      if (Double.compare(red, 1.0) > 0 ||
          Double.compare(blue, 1.0) > 0 ||
          Double.compare(green, 1.0) > 0 ||
          Double.compare(red, 0.0) < 0 ||
          Double.compare(green, 0.0) < 0 ||
          Double.compare(blue, 0.0) < 0)
         throw new Exception("Invalid color parameters!");

      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   /**
    * Constructor.
    * Uses input vector.
    * @param inputVector input vector
    */
   public ColorInput(double[] inputVector) throws Exception {
      this(inputVector[0], inputVector[1], inputVector[2]);
   }

   /**
    * Converts a JavaFX color to a network input color.
    * @param in JavaFX color
    * @return network input color
    */
   public static ColorInput convertFXColor(javafx.scene.paint.Color in) {
      try {
         return new ColorInput(in.getRed(), in.getGreen(), in.getBlue());
      } catch (Exception e) {
         // This should never happen.
         return null;
      }
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
