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
public class NetworkManager {
   /**
    * Singleton instance.
    */
   public static final NetworkManager instance = new NetworkManager();

   /**
    * Map of file names to networks.
    */
   private List<Network> networks;

   /**
    * Constructor.
    */
   private NetworkManager() {
      loadNetworks();
  }

   public void loadNetworks() {
      try {
         networks = FileManager.loadNetworks();
      } catch (Exception e) {
         System.err.println("Could not load networks!");
      }

   }

   public List<Network> getNetworks() {
      return networks;
   }

   public void addNetwork(Network network) {
      if (!networks.contains(network))
         networks.add(network);
   }

   public void removeNetwork(Network network) {
      if (networks.contains(network))
         networks.remove(network);
   }

   public boolean exists(String name) {
      for (Network network : networks)
         if (network.name.equals(name)) return true;
      return false;
   }
}
