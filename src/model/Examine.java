package model;

import gui.controller.ExamineController;
import model.network.Network;
import model.network.memory.MemoryModule;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by gpdavis on 5/2/15.
 */
public class Examine implements Observer {
   private ExamineController controller;

   public void setController(ExamineController controller) {
      this.controller = controller;
   }

   @Override
   public void update(Observable o, Object arg) {
      if (controller != null) {
         if (WorkSpace.instance.openNetwork())
            controller.setMemory(WorkSpace.instance.getNetwork().getMemoryModule());
         else
            controller.clearMemory();
      }
   }
}
