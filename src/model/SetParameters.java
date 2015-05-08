package model;

import gui.controller.ParametersController;
import model.network.Network;
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
         Parameters params = WorkSpace.instance.getNetwork().getParameters();
         if (params != null)
            controller.setFields(params);
         else
            controller.clearFields();
      }
   }
}
