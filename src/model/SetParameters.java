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
   private Network network;
   private ParametersController controller;

   public void setController(ParametersController controller) {
      this.controller = controller;
   }

   public Parameters getParameters() {
      return network.getParameters();
   }

   public void setParameters(Parameters newParams) {
      network.setParameters(newParams);
   }

   @Override
   public void update(Observable o, Object arg) {
      if (controller != null) {
         Parameters params = WorkSpace.instance.getNetworkParameters();
         if (params != null)
            controller.setFields(params);
         else
            controller.clearFields();
      }
   }
}
