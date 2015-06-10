package model.network.parameters;

/**
 * Created by gpdavis on 6/9/15.
 */
public class BoundedParameter<T extends Comparable> extends Parameter<T> {
   public final T minimum;
   public final T maximum;

   public BoundedParameter(String name, T value, T minimum, T maximum) {
      super(name, value);
      this.value = value;
      this.minimum = minimum;
      this.maximum = maximum;
   }

   public boolean setValue(T newValue) {
      if (newValue.getClass().equals(value.getClass())) {
         if (checkBounds(newValue)) {
            this.value = newValue;
            return true;
         }
      }
      return false;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(name + ":");
      if (minimum != null) sb.append("\n  Minimum: " + minimum.toString());
      if (maximum != null) sb.append("\n  Maximum: " + maximum.toString());

      return sb.toString();
   }

   public BoundedParameter clone() {
      return new BoundedParameter(name, value, minimum, maximum);
   }

   protected boolean checkBounds(T newValue) {
      if (minimum != null) {
         if (newValue.compareTo(minimum) < 0) return false;
      }
      if (maximum != null) {
         if (newValue.compareTo(maximum) > 0) return false;
      }
      return true;
   }
}
