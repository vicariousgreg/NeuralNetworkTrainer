package model.network.parameters;

import java.io.Serializable;

/**
 * Created by gpdavis on 6/9/15.
 */
public abstract class Parameter<T> implements Serializable {
   public final String name;
   protected T value;

   public Parameter(String name, T value) {
      this.name = name;
      this.value = value;
   }

   public T getValue() {
      return value;
   }

   public boolean setValue(T newValue) {
      this.value = newValue;
      return true;
   }

   public abstract String toString();

   public String getValueString() {
      if (value instanceof Object[]) {
         StringBuilder sb = new StringBuilder();
         for (Object o : (Object[]) value) {
            sb.append(o.toString() + " ");
         }
         return sb.toString().trim();
      }
      return value.toString();
   }

   public abstract Parameter<T> clone();
}
