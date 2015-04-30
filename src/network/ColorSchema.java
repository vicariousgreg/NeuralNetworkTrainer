package network;

import javafx.scene.paint.Color;

/**
 * Created by gpdavis on 4/29/15.
 */
public class ColorSchema extends Schema{
   public ColorSchema() {
      super(new String[] {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Purple"
      });
   }

   @Override
   public double[] convertInput(Object in) {
      Color color = (Color) in;
      return new double[] { color.getRed(), color.getGreen(), color.getBlue() };
   }

   @Override
   public double[] convertOutput(String out) throws Exception {
      int expIndex = -1;

      for (int index = 0; index < classifications.length; ++index) {
         if (out.equals(classifications[index])) expIndex = index;
      }

      if (expIndex == -1)
         throw new Exception(
               "Output string does not represent a classification!");

      double[] outVector = new double[classifications.length];
      outVector[expIndex] = 1.0;
      return outVector;
   }
}
