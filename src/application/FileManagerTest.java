package application;

import model.network.Network;
import model.network.schema.ColorSchema;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FileManagerTest {
   private static String networkName = "junitTestNetwork12345";
   private static Network testNetwork = new Network(new ColorSchema());

   @Test
   public void testSaveAndDelete() throws Exception {
      // Test save unrecognized without explicit name.
      try {
         FileManager.instance.saveNetwork(testNetwork);
         assert (false);
      } catch (Exception e) { }

      // Save network
      FileManager.instance.saveNetwork(testNetwork, networkName);

      // Save without explicit name.
      FileManager.instance.saveNetwork(testNetwork);

      // Ensure network exists and has the correct name
      assert (FileManager.instance.exists(networkName));
      assert (FileManager.instance.getName(testNetwork).equals(networkName));

      // Delete network
      FileManager.instance.delete(networkName);
      assert (!FileManager.instance.exists(networkName));


   }

   @Test
   public void testImport() throws Exception {
      // Save network to external file
      File testFile = new File(networkName);
      FileOutputStream fos = new FileOutputStream(testFile);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(testNetwork);
      out.close();

      // Import network
      FileManager.instance.importNetwork(testFile);
      assert (FileManager.instance.exists(testFile.getName()));

      // Delete network
      FileManager.instance.delete(testFile.getName());
      assert (!FileManager.instance.exists(testFile.getName()));

      // Delete file
      testFile.delete();
   }

   @Test
   public void testExport() throws Exception {
      File testFile = new File(networkName);
      FileManager.instance.exportNetwork(testNetwork, testFile);

      FileManager.instance.importNetwork(testFile);
      assert (FileManager.instance.getNetworks().get(networkName) != null);

      // Delete network
      FileManager.instance.delete(testFile.getName());
      assert (!FileManager.instance.exists(testFile.getName()));

      // Delete file
      testFile.delete();
   }

   @Test
   public void testGetNetworks() throws Exception {
      // Create test networks
      Network testNetwork1 = new Network(new ColorSchema());
      Network testNetwork2 = new Network(new ColorSchema());
      Network testNetwork3 = new Network(new ColorSchema());

      // Save networks
      FileManager.instance.saveNetwork(testNetwork1, networkName + "1");
      FileManager.instance.saveNetwork(testNetwork2, networkName + "2");
      FileManager.instance.saveNetwork(testNetwork3, networkName + "3");

      // Ensure networks exist
      Map<String, Network> map = FileManager.instance.getNetworks();
      assert (map.containsValue(testNetwork1));
      assert (map.containsValue(testNetwork2));
      assert (map.containsValue(testNetwork3));
      List<String> names = FileManager.instance.getNetworkNames();
      assert (names.contains(networkName + "1"));
      assert (names.contains(networkName + "2"));
      assert (names.contains(networkName + "3"));

      // Delete networks
      FileManager.instance.delete(networkName + "1");
      FileManager.instance.delete(networkName + "2");
      FileManager.instance.delete(networkName + "3");
   }

   @Test
   public void testExceptions() throws Exception {
      //// Save exception
      // Try to save another network to the same name
      FileManager.instance.saveNetwork(testNetwork, networkName);
      try {
         // Create unrecognized network
         Network badNetwork = new Network(new ColorSchema());

         FileManager.instance.saveNetwork(badNetwork, networkName);
         assert (false);
      } catch (Exception e) { }

      // Saving the same network should work fine.
      FileManager.instance.saveNetwork(testNetwork, networkName);


      //// Import exception
      // Save network to external file
      File testFile = new File(networkName);
      FileOutputStream fos = new FileOutputStream(testFile);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(testNetwork);
      out.close();

      // Import network
      try {
         FileManager.instance.importNetwork(testFile);
         assert (false);
      } catch (Exception e) { }


      //// Delete exception
      try {
         FileManager.instance.delete(networkName + "acdnsklsaecwsnklacdsa");
         assert (false);
      } catch (Exception e) { }

      testFile.delete();
      FileManager.instance.delete(networkName);

      assert (FileManager.instance.getName(testNetwork) == null);
   }
}