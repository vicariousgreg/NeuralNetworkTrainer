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
    * Tracked networks list.
    */
   private List<Network> networks;

   /**
    * Constructor.
    */
   private NetworkManager() {
      loadNetworks();
   }

   /**
    * Loads networks from storage.
    * Prints error if network cannot be loaded.
    */
   public void loadNetworks() {
      try {
         networks = FileManager.loadNetworks();
      } catch (Exception e) {
         System.err.println("Could not load networks!");
      }
   }

   /**
    * Gets the manager's network list.
    * @return networks list
    */
   public List<Network> getNetworks() {
      return networks;
   }

   /**
    * Gets a network by name.
    * @param name network name
    * @return network if found, or null if not
    */
   public Network getNetwork(String name) {
      for (Network network : networks)
         if (network.name.equals(name)) return network;
      return null;
   }

   /**
    * Adds a network to the manager.
    * @param network network to add
    * @throws Exception if network with same name already exists
    */
   public void addNetwork(Network network) throws Exception {
      if (exists(network.name))
         throw new Exception("Network with name already exists!");
      if (!networks.contains(network))
         networks.add(network);
   }

   /**
    * Removes a network from the manager.
    * @param network network to remove
    */
   public void removeNetwork(Network network) {
      if (networks.contains(network))
         networks.remove(network);
   }

   /**
    * Checks if a network with the given name exists in the manager.
    * @param name network name to check for
    * @return whether network with name exists
    */
   public boolean exists(String name) {
      return getNetwork(name) != null;
   }
}
