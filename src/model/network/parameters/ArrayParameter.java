package model.network.parameters;

/**
 * Created by gpdavis on 6/9/15.
 */
public class ArrayParameter<T extends Comparable> extends Parameter<T[]> {
   private BoundedParameter<T> boundedParameter;

   public ArrayParameter(String name, T[] value, T minimum, T maximum) {
      super(name, value);
      this.boundedParameter = new BoundedParameter<T>(name, value[0], minimum, maximum);
   }

   public boolean setValue(T[] newValue) {
      for (int i = 0; i < newValue.length; ++i) {
         if (!boundedParameter.checkBounds(newValue[i]))
            return false;
      }
      this.value = newValue;
      return true;
   }

   public String getValueString() {
      StringBuilder sb = new StringBuilder();
      for (T o : value) {
         sb.append(o.toString() + " ");
      }
      return sb.toString().trim();
   }

   public String toString() {
      return boundedParameter.toString();
   }

   public ArrayParameter clone() {
      return new ArrayParameter<T>(name, value, boundedParameter.minimum, boundedParameter.maximum);
   }
}
