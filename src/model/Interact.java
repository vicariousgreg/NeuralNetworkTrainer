package model;

import gui.controller.InteractController;
import model.network.schema.Schema;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by gpdavis on 5/2/15.
 */
public class Interact implements Observer {
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
      if (controller != null) {
         if (WorkSpace.instance.openNetwork())
            controller.setClassifications(
                  WorkSpace.instance.getNetwork().schema.getOutputClassifications());
         else
            controller.clearClassifications();
      }
   }
}
