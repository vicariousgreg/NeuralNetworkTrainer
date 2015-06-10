package model.network.parameters;

/**
 * Created by gpdavis on 6/9/15.
 */
public class EnumeratedParameter<T> extends Parameter<T> {
   public T value;
   public final T[] enumerations;

   public EnumeratedParameter(String name, T value, T[] enumerations) {
      super(name, value);
      this.value = value;
      this.enumerations = enumerations;
   }

   public T getValue() {
      return value;
   }

   public boolean setValue(T newValue) {
      if (checkEnumerations(newValue)) {
         super.setValue(newValue);
         this.value = newValue;
         return true;
      }
      return false;
   }

   public T[] getEnumerations() {
      return enumerations;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(name + ":");
      if (enumerations != null) {
         sb.append("\n  Enumeration: ");
         for (int i = 0; i < enumerations.length; ++i)
            sb.append(enumerations[i].toString() + " ");
      }

      return sb.toString();
   }

   public EnumeratedParameter clone() {
      return new EnumeratedParameter(name, value, enumerations);
   }

   protected boolean checkEnumerations(T newValue) {
      if (enumerations != null) {
         boolean valid = false;

         for (int i = 0; i < enumerations.length; ++i) {
            if (enumerations[i].equals(newValue))
               valid = true;
         }
         if (!valid) return false;
      }
      return true;
   }
}
