package application;

import model.network.Network;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gpdavis on 6/4/15.
 */
public class FileManager {
   /** Singleton instance. */
   public static final FileManager instance = new FileManager();

   /** Path to network files. */
   private static final String kNetworkPath = "src/application/data/networks/";

   /** Path to memory files. */
   private static final String kMemoryPath = "src/application/data/memories/";

   /** Path to memory files. */
   private static final String kSchemaPath = "src/application/data/schemas/";


   /** Map of file names to networks. */
   private Map<String, Network> networks;

   /** Constructor. */
   private FileManager() {
      networks = new HashMap<String, Network>();
      try {
         loadNetworks();
      } catch (Exception e) {
         System.err.println("Could not load networks!");
      }
   }

   /**
    * Loads the network with the given name.
    * @throws Exception if networks could not be loaded
    */
   private void loadNetworks() throws Exception {
      File dataFile = new File(kNetworkPath);
      File[] networkFiles = dataFile.listFiles();

      for (File file : networkFiles) {
         try {
            networks.put(file.getName(), loadNetwork(file));
         } catch (Exception e) {
            System.err.println("Could not load " + file.getName());
         }
      }
   }

   /**
    * Loads a network.
    * @param file network file
    * @return network
    * @throws Exception if network could not be loaded
    */
   private Network loadNetwork(File file) throws Exception {
      FileInputStream fin = new FileInputStream(file);
      ObjectInputStream ois = new ObjectInputStream(fin);
      Network net = (Network) ois.readObject();
      ois.close();
      return net;
   }

   /**
    * Saves a network to storage.
    * @param network network to save.
    * @param name network file name
    * @throws Exception if network could not be saved
    */
   public void saveNetwork(Network network, String name) throws Exception {
      if (!exists(name) ||
            (exists(name) && getName(network) != null && getName(network).equals(name))) {
         FileOutputStream fos = new FileOutputStream(new File(kNetworkPath + name));
         ObjectOutputStream out = new ObjectOutputStream(fos);
         out.writeObject(network);
         out.close();
         networks.put(name, network);
      } else {
         throw new Exception ("Could not save network!");
      }
   }

   /**
    * Saves a network to storage.
    * @param network network to save.
    * @throws Exception if network is not recognized or could not be saved
    */
   public void saveNetwork(Network network) throws Exception {
      if (networks.containsValue(network)) {
         String name = "";
         for (String key : networks.keySet())
            if (networks.get(key) == network) name = key;

         FileOutputStream fos = new FileOutputStream(new File(kNetworkPath + name));
         ObjectOutputStream out = new ObjectOutputStream(fos);
         out.writeObject(network);
         out.close();
         networks.put(name, network);
      } else {
         throw new Exception ("Could not save network!");
      }
   }

   /**
    * Imports a network.
    * @param networkFile network file
    * @throws Exception if network could not be imported
    */
   public void importNetwork(File networkFile) throws Exception {
      if (exists(networkFile.getName()))
         throw new Exception("Network with same name already exists!");

      saveNetwork(loadNetwork(networkFile), networkFile.getName());
   }

   /**
    * Exports a network.
    * @param network network to export
    * @param destination destination file
    * @throws Exception if network could not be exported
    */
   public void exportNetwork(Network network, File destination) throws Exception {
      FileOutputStream fos = new FileOutputStream(destination);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(network);
      out.close();
   }

   /**
    * Checks whether a network with the given name exists.
    * @param name network file name
    * @return whether network exists
    */
   public boolean exists(String name) {
      return networks.get(name) != null;
   }

   /**
    * Deletes a network from storage with the given name.
    * @param name network file name
    * @throws Exception if network could not be deleted
    */
   public void delete(String name) throws Exception {
      if (exists(name)) {
         File networkFile = new File(kNetworkPath + name);
         networkFile.delete();
         networks.remove(name);
      } else {
         throw new Exception ("Network does not exist!");
      }
   }

   /**
    * Gets a list of network names in storage.
    * @return list of network names
    */
   public List<String> getNetworkNames() {
      return new ArrayList<String>(networks.keySet());
   }

   /**
    * Gets a map of names to networks loaded from storage.
    * @return
    */
   public Map<String, Network> getNetworks() {
      return networks;
   }

   /**
    * Gets a network's name.
    * @param network network to identify
    * @return network name or null if does not exist
    */
   public String getName(Network network) {
      for (String name : networks.keySet()) {
         if (networks.get(name).equals(network))
            return name;
      }
      return null;
   }
}
