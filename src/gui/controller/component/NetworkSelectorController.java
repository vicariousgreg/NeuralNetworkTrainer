package gui.controller.component;

import gui.controller.DialogFactory;
import application.FileManager;
import application.NetworkManager;
import gui.controller.widget.GenericHandler;
import gui.controller.widget.GenericList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import model.network.Network;
import model.network.parameters.Parameters;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NetworkSelectorController extends MultiNetworkController implements Initializable {
   @FXML Pane subControllerPane;
   @FXML ListView listView;

   private NetworkController subController;
   private GenericList<Network> networkList;

      /**
       * Initialization.
       * Sets up listeners for GUI.
       */
   public void initialize(URL location, ResourceBundle resources) {
      // Add as a listener to whatever the source of the network list is.
      //WorkSpace.instance.addObserver(this);

      networkList = new GenericList<Network>(listView);

      // Add click listener
      networkList.addClickListener(new GenericHandler<Network>() {
         @Override
         public void handle(Network network) {
            setNetwork(network);
         }
      });

      // Add edit parameters context menu item.
      networkList.addContextMenuItem("Edit Parameters", new GenericHandler<Network>() {
         public void handle(Network network) {
            Parameters newParams = DialogFactory.displayParametersDialog(network.getParameters());
            if (newParams != null)
               network.setParameters(newParams);
         }
      });

      // Populate network list.
      loadNetworks();
   }

   public void setChild(URL resource) throws IOException {
      // Load resource, extract node and controller
      FXMLLoader loader = new FXMLLoader(resource);
      Node node = loader.load();
      subControllerPane.getChildren().clear();
      subControllerPane.getChildren().add(node);

      subController = loader.getController();
      subController.setNetwork(getNetwork());
   }

   @Override
   public void setNetwork(Network network) {
      super.setNetwork(network);
      if (subController != null) {
         subController.setNetwork(network);
         subController.display();
      }
   }

   public void saveNetwork() {
      try {
         FileManager.saveObject(networkList.getSelectedItem());
      } catch (Exception e) { }
   }

   public void newNetwork() {
      try {
         Network newNetwork = DialogFactory.displayNetworkDialog();
         if (newNetwork != null) {
            FileManager.saveObject(newNetwork);
            NetworkManager.instance.loadNetworks();
            loadNetworks();
         }
      } catch (Exception e) {
         DialogFactory.displayErrorDialog("Invalid network name!");
      }
   }

   @Override
   public void display() {
      loadNetworks();
      setNetwork(getNetwork());
   }

   private void loadNetworks() {
      networkList.clear();
      for (Network net : NetworkManager.instance.getNetworks())
         networkList.add(net);

      super.clearNetworks();
      super.addNetworks(NetworkManager.instance.getNetworks());
   }
}
