package model;

import gui.controller.InteractController;
import model.network.Network;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by gpdavis on 5/2/15.
 */
public class Interact implements Observer {
   private Network network;
   private InteractController controller;

   public void setController(InteractController controller) {
      this.controller = controller;
   }

   public Object guess(Object input) {
      return WorkSpace.instance.queryNetwork(input);
   }

   public void addMemory(Object input, Object output) {
      WorkSpace.instance.addMemory(input, output);
   }

   @Override
   public void update(Observable o, Object arg) {
      this.network = WorkSpace.instance.getNetwork();
      if (controller != null) {
         if (network != null)
            controller.setClassifications(network.schema.getOutputClassifications());
         else
            controller.clearClassifications();
      }
   }
}
