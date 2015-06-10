package model.network.parameters;

/**
 * Created by gpdavis on 6/9/15.
 */
public class BooleanParameter extends Parameter<Boolean> {
   public BooleanParameter(String name, Boolean value) {
      super(name, value);
   }
   public String toString() {
      return value.toString();
   }

   public BooleanParameter clone() {
      return new BooleanParameter(name, value);
   }
}
