package network;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * A Neural Network schema for color identification.
 * Includes primary and secondary colors.
 */
public class ColorSchema extends Schema {
   /**
    * Constructor.
    */
   public ColorSchema() {
      super(new Class[] { java.awt.Color.class, javafx.scene.paint.Color.class },
         3, new String[] {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Purple"
      });
   }

   @Override
   protected double[] encode(Object in) throws Exception {
      double[] outputVector = new double[inputSize];

      if (in instanceof java.awt.Color) {
         java.awt.Color color = (java.awt.Color) in;
         outputVector[0] = color.getRed() / 255;
         outputVector[1] = color.getGreen() / 255;
         outputVector[2] = color.getBlue() / 255;
      } else if (in instanceof javafx.scene.paint.Color) {
         javafx.scene.paint.Color color = (javafx.scene.paint.Color) in;
         outputVector[0] = color.getRed();
         outputVector[1] = color.getGreen();
         outputVector[2] = color.getBlue();
      } else {
         throw new Exception ("Input object not recognized by this schema: " + in.getClass().toString());
      }

      return outputVector;
   }

   @Override
   public Node toFXNode(Object in, double width, double height) throws Exception {
      Rectangle rect = new Rectangle(width, height);
      double[] vector = encodeInput(in);

      rect.setFill(
         new javafx.scene.paint.Color(vector[0], vector[1], vector[2], 1.0));
      return rect;
   }
}
