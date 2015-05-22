package gui.controller;

import model.network.Network;

public abstract class NetworkController {
   protected Network network;

   public void setNetwork(Network network) {
      this.network = network;
   }

   public Network getNetwork() {
      return network;
   }

   public abstract void display();
}
