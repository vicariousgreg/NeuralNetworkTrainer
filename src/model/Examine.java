package model;

import gui.controller.ExamineController;
import model.network.Network;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by gpdavis on 5/2/15.
 */
public class Examine implements Observer {
   private Network network;
   private ExamineController controller;

   public void setController(ExamineController controller) {
      this.controller = controller;
   }

   @Override
   public void update(Observable o, Object arg) {
      this.network = WorkSpace.instance.getNetwork();
      if (network != null)
         controller.setMemory(network.getMemoryModule());
      else
         controller.clearMemory();
   }
}
