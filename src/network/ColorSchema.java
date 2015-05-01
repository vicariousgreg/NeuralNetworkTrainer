package network;

/**
 * A Neural Network schema for color identification.
 * Includes primary and secondary colors.
 */
public class ColorSchema extends Schema {
   /**
    * Constructor.
    */
   public ColorSchema() {
      super(ColorInput.class, 3, new String[] {
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Purple"
      });
   }

   @Override
   public double[] convertOutput(Object out) throws Exception {
      int expIndex = -1;

      // Find the index of the classification.
      for (int index = 0; index < classifications.length; ++index) {
         if (out.equals(classifications[index])) expIndex = index;
      }

      // If not found, the output to convert was invalid.
      if (expIndex == -1)
         throw new Exception(
               "Output string does not represent a classification!");

      double[] outVector = new double[classifications.length];
      outVector[expIndex] = 1.0;
      return outVector;
   }

   @Override
   public NetworkInput translateInput(double[] in) throws Exception {
      if (in.length != inputSize)
         throw new Exception ("Invalid input vector size!");

      return new ColorInput(in);
   }
}
