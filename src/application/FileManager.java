package application;

import model.network.Network;
import model.network.memory.Memory;
import model.network.schema.Schema;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpdavis on 6/4/15.
 */
public class FileManager {
   /** Path to network files. */
   public static final String kNetworkPath = "src/application/data/networks/";

   /** Path to memory files. */
   public static final String kMemoryPath = "src/application/data/memories/";

   /** Path to memory files. */
   public static final String kSchemaPath = "src/application/data/schemas/";

   /**
    * Loads the network with the given name.
    * @throws Exception if networks could not be loaded
    */
   public static List<Network> loadNetworks() throws Exception {
      final File dataFile = new File(kNetworkPath);
      final File[] networkFiles = dataFile.listFiles();
      List<Network> networks = new ArrayList<Network>();

      for (File file : networkFiles) {
         try {
            networks.add((Network)loadObject(file));
         } catch (Exception e) {
            System.err.println("Could not load " + file.getName());
         }
      }
      return networks;
   }

   /**
    * Loads the network with the given name.
    * @throws Exception if networks could not be loaded
    */
   public static List<Schema> loadSchemas() throws Exception {
      final File dataFile = new File(kSchemaPath);
      final File[] schemaFiles = dataFile.listFiles();
      List<Schema> schemas = new ArrayList<Schema>();

      for (File file : schemaFiles) {
         try {
            schemas.add((Schema)loadObject(file));
         } catch (Exception e) {
            System.err.println("Could not load " + file.getName());
         }
      }
      return schemas;
   }

   /**
    * Loads a list of memories to storage.
    * @param name name of memory set
    */
   public static List<Memory> loadMemories(String name) throws Exception {
      if (!exists(kMemoryPath, name))
         throw new Exception ("Unable to load memory set!");

      List<Memory> memories = new ArrayList<Memory>();

      String path = kMemoryPath + name + "/";

      final File[] classificationsDirectories = new File(path).listFiles();

      for (File classFile : classificationsDirectories) {
         for (File memFile : classFile.listFiles()) {
            memories.add((Memory) loadObject(memFile));
         }
      }
      return memories;
   }

   /**
    * Loads a file.
    * @param file file to load
    * @return loaded object
    * @throws Exception if object could not be loaded
    */
   public static Object loadObject(File file) throws Exception {
      FileInputStream fin = new FileInputStream(file);
      ObjectInputStream ois = new ObjectInputStream(fin);
      Object obj = ois.readObject();
      ois.close();
      return obj;
   }

   /**
    * Saves an object.
    * @param obj object to save
    * @return whether save was successful
    * @throws Exception if object could not be saved
    */
   public static boolean saveObject(Object obj) throws Exception {
      String path = null;
      if (obj instanceof Network)
         path = kNetworkPath + ((Network) obj).name;
      else if (obj instanceof Schema)
         path = kSchemaPath + ((Schema) obj).name;

      if (path != null) {
         FileOutputStream fos = new FileOutputStream(new File(path));
         ObjectOutputStream out = new ObjectOutputStream(fos);
         out.writeObject(obj);
         out.close();
         return true;
      }
      return false;
   }

   /**
    * Saves a list of memories to storage.
    * @param name name of memory set
    * @param memories memories to save
    */
   public static void saveMemories(String name, List<Memory> memories) throws Exception {
      if (!exists(kMemoryPath, name))
         new File(kMemoryPath + name).mkdir();

      String path = kMemoryPath + name + "/";

      for (Memory mem : memories) {
         // Create classification subdirectory if necessary.
         if (!exists(path, mem.output.toString()))
            new File(path + mem.output.toString()).mkdir();

         String memPath = path + mem.output.toString() + "/" + mem.hashCode();

         FileOutputStream fos = new FileOutputStream(new File(memPath));
         ObjectOutputStream out = new ObjectOutputStream(fos);
         out.writeObject(mem);
         out.close();
      }
   }

   /**
    * Checks whether a file with the given name exists in storage.
    * @param path path to directory to search
    * @param name file name
    * @return whether file exists in storage
    */
   public static boolean exists(String path, String name) {
      return new File(path + name).exists();
   }

   /**
    * Deletes a network from storage with the given name.
    * @param name network file name
    * @throws Exception if network could not be deleted
    */
   public static void delete(String path, String name) throws Exception {
      if (!exists(path, name))
         throw new Exception ("File does not exist!");
      new File(kNetworkPath + name).delete();
   }
}
