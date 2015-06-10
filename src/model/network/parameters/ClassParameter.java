package model.network.parameters;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by gpdavis on 6/9/15.
 */
public class ClassParameter extends EnumeratedParameter<Class> {
   private Map<String, Parameter> subParameters;

   public ClassParameter(String name, Class value, Class[] enumerations) {
      super(name, value, enumerations);
      setValue(value);
   }

   public Object instantiate() {
      try {
         return value.getConstructor(Map.class).newInstance(subParameters);
      } catch (Exception e) {
         return null;
      }
   }

   public boolean setValue(Class newValue) {
      try {
         return setValue(newValue,
               (Map<String, Parameter>)
                     newValue.getField("defaultParameters").get(null));
      } catch (Exception e) {
         setValue(newValue, new LinkedHashMap<String, Parameter>());
         return false;
      }
   }

   public boolean setValue(Class newValue, Map<String, Parameter> subParams) {
      if (checkEnumerations(newValue)) {
         try {
            this.value = newValue;
            this.subParameters = subParams;
            return true;
         } catch (Exception e) {
            return false;
         }
      }
      return false;
   }

   public Class[] getEnumerations() {
      return enumerations;
   }

   public Map<String, Parameter> getSubParameters() {
      return subParameters;
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

   public String getValueString() {
      StringBuilder sb = new StringBuilder(value.getSimpleName());
      return sb.toString();
   }

   public ClassParameter clone() {
      return new ClassParameter(name, value, enumerations);
   }
}
