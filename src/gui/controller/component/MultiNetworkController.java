package gui.controller.component;

import model.network.Network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class MultiNetworkController extends NetworkController {
   protected List<Network> networks;

   public MultiNetworkController() {
      this.networks = new ArrayList<Network>();
   }

   public void addNetwork(Network network) {
      networks.add(network);
   }
   public void addNetworks(Collection<Network> newNetworks) {
      networks.addAll(newNetworks);
   }

   public List<Network> getNetworks() {
      return networks;
   }

   public void clearNetworks() {
      networks.clear();
   }

   public boolean containsNetwork(Network network) {
      return networks.contains(network);
   }

   public boolean removeNetwork(Network network) {
      if (containsNetwork(network)) {
         networks.remove(network);
         return true;
      }
      return false;
   }

   public abstract void display();
}
