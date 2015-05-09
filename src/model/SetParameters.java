package model;

import gui.controller.ParametersController;
import model.network.Parameters;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by gpdavis on 5/2/15.
 */
public class SetParameters implements Observer {
   private ParametersController controller;

   public void setController(ParametersController controller) {
      this.controller = controller;
   }

   public Parameters getParameters() {
      return WorkSpace.instance.getNetwork().getParameters();
   }

   public void setParameters(Parameters newParams) {
      WorkSpace.instance.getNetwork().setParameters(newParams);
   }

   @Override
   public void update(Observable o, Object arg) {
      if (controller != null) {
         if (WorkSpace.instance.openNetwork())
            controller.setFields(WorkSpace.instance.getNetwork().getParameters());
         else
            controller.clearFields();
      }
   }
}
