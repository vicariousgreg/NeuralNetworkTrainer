package model.network.schema;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A Neural Network schema for color identification.
 * Includes primary and secondary colors.
 */
public class ColorInputAdapter extends InputAdapter {
   /**
    * Constructor.
    */
   public ColorInputAdapter() {
      super(3);
   }

   @Override
   public boolean validInput(Object in) {
      return in instanceof Color;
   }

   @Override
   public Object recreateInput(double[] inputVector) throws Exception {
      if (inputVector.length != inputSize)
         throw new Exception ("Invalid input vector length!");

      return new javafx.scene.paint.Color(
         inputVector[0], inputVector[1], inputVector[2], 1.0);
   }

   @Override
   public double[] encode(Object in) throws Exception {
      if (validInput(in)) {
         Color color = (Color) in;
         return new double[] {
            color.getRed(),
            color.getGreen(),
            color.getBlue()
         };
      } else {
         throw new Exception("Unrecognized input!");
      }
   }

   @Override
   public Node toFXNode(Object in, double width, double height) throws Exception {
      if (validInput(in)) {
         Color color = (Color) in;
         Rectangle rect = new Rectangle(width, height);
         rect.setFill(color);
         return rect;
      } else {
         throw new Exception("Unrecognized input!");
      }
   }
}
