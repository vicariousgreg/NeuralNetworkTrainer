package network;

import network.activation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class SerializeTest {
   public static void main (String[] args) {
      try {
         Sigmoid activ = new SigmoidEstimate(1, 1000);
         Schema schema = new ColorSchema();
         NetworkParameters params = new NetworkParameters();
         Neuron neuron = new Neuron(activ, 5);
         Experience exp = new Experience(schema, new ColorInput(0.5, 0.5, 0.5), "Red");

         HashMap<String, Serializable> map = new HashMap<String, Serializable>();
         map.put("activ.ser", activ);
         map.put("schema.ser", schema);
         map.put("params.ser", params);
         map.put("neuron.ser", neuron);
         map.put("exp.ser", exp);
         for (String key : map.keySet()) {
            Serializable s = map.get(key);
            File file = new File("ser/" + key);
            file.createNewFile();
            if (file != null) {
               try {
                  FileOutputStream fos = new FileOutputStream(file);
                  ObjectOutputStream out = new ObjectOutputStream(fos);
                  out.writeObject(s);
                  out.close();
               } catch (Exception e) {
                  System.out.println("Could not save network!");
                  e.printStackTrace();
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}