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

   /** Path to schema files. */
   public static final String kSchemaPath = "src/application/data/schemas/";

   /** Path to memory files. */
   public static final String kMemoryPath = "src/application/data/memories/";


   //////////////
   // NETWORKS //
   //////////////

   /**
    * Loads up all networks in storage.
    * @return list of networks loaded from storage
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
    * Gets a list of networks in storage.
    * @return list of networks
    */
   public static String[] getNetworkList() {
      final File dataFile = new File(kNetworkPath);
      final File[] networkFiles = dataFile.listFiles();
      String[] networkNames = new String[networkFiles.length];

      for (int i = 0; i < networkNames.length; ++i) {
         networkNames[i] = networkFiles[i].getName();
      }

      return networkNames;
   }

   /////////////
   // SCHEMAS //
   /////////////

   /**
    * Loads up all schemas in storage.
    * @return list of schemas loaded from storage
    * @throws Exception if schemas could not be loaded
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
    * Gets a list of schemas in storage.
    * @return list of schemas
    */
   public static String[] getSchemaList() {
      final File dataFile = new File(kMemoryPath);
      final File[] schemaFiles = dataFile.listFiles();
      String[] schemaNames = new String[schemaFiles.length];

      for (int i = 0; i < schemaNames.length; ++i) {
         schemaNames[i] = schemaFiles[i].getName();
      }

      return schemaNames;
   }

   //////////////
   // MEMORIES //
   //////////////

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
    * Gets a list of memory sets in storage.
    * @return list of memory set names
    */
   public static String[] getMemoryList() {
      final File dataFile = new File(kMemoryPath);
      final File[] memorySets = dataFile.listFiles();
      String[] setNames = new String[memorySets.length];

      for (int i = 0; i < setNames.length; ++i) {
         setNames[i] = memorySets[i].getName();
      }

      return setNames;
   }

   /////////////
   // OBJECTS //
   /////////////

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
    * Deletes an object from storage with the given name.
    * @param name object name
    * @throws Exception if object could not be deleted
    */
   public static void deleteObject(String path, String name) throws Exception {
      if (!exists(path, name))
         throw new Exception ("File does not exist!");
      new File(kNetworkPath + name).delete();
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
}
