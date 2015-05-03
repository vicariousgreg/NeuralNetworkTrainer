package model.network.schema;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import model.network.memory.Memory;

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
   public Object recreateInput(double[] inputVector) {
      return new javafx.scene.paint.Color(
         inputVector[0], inputVector[1], inputVector[2], 1.0);
   }

   @Override
   public double[] encode(Object in) throws Exception {
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
         throw new Exception ("Input class not recognized by this schema: "
            + in.getClass().toString());
      }

      return outputVector;
   }

   @Override
   public Node toFXNode(Memory exp, double width, double height) throws Exception {
      Rectangle rect = new Rectangle(width, height);
      rect.setFill((javafx.scene.paint.Color)(recreateInput(exp.inputVector)));
      return rect;
   }
}
