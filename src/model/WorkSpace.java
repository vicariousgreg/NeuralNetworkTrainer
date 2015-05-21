package model;

import model.network.Network;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by gpdavis on 5/2/15.
 */
public class WorkSpace extends Observable {
   private static final String kDataPath = "src/gui/controller/data/";
   public static final WorkSpace instance = new WorkSpace();

   private List<NetworkData> networks;

   private WorkSpace() {
      this.networks = new ArrayList<NetworkData>();
   }

   public void loadNetworks() throws Exception {
      File dataFile = new File(kDataPath);
      File[] networkFiles = dataFile.listFiles();

      for (File file : networkFiles) {
         FileInputStream fin = new FileInputStream(file);
         ObjectInputStream ois = new ObjectInputStream(fin);
         Network net = (Network) ois.readObject();
         ois.close();

         networks.add(new NetworkData(file.getName(), net));
      }
      setChanged();
      notifyObservers();
   }

   /**
    * Gets a list of tracked file names.
    * @return filenames list
    */
   public List<String> getNetworkNames() {
      List<String> out = new ArrayList<String>();

      for (NetworkData data : networks) {
         out.add(data.fileName);
      }

      return out;
   }

   /**
    * Gets a network based on its filename.
    * Returns null if not found.
    *
    * @param name
    * @return network
    */
   public Network getNetwork(String name) {
      for (NetworkData data : networks) {
         if (data.fileName.equals(name))
            return data.network;
      }
      return null;
   }

   /**
    * Network data entry.
    * Holds the network, its associated filename, and a changed flag.
    */
   private class NetworkData {
      public Network network;
      public String fileName;
      public boolean changed;

      public NetworkData(String name, Network net) {
         this.fileName = name;
         this.network = net;
         this.changed = false;
      }
   }
}
