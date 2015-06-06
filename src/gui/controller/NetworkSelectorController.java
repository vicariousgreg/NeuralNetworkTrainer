package gui.controller;

import application.DialogFactory;
import application.FileManager;
import gui.controller.widget.NetworkHandler;
import gui.controller.widget.NetworkList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import model.network.Network;
import model.network.Parameters;
import model.network.schema.ColorSchema;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NetworkSelectorController extends MultiNetworkController implements Initializable {
   @FXML Pane subControllerPane;
   @FXML ListView listView;

   private NetworkController subController;
   private NetworkList networkList;

      /**
       * Initialization.
       * Sets up listeners for GUI.
       */
   public void initialize(URL location, ResourceBundle resources) {
      // Add as a listener to whatever the source of the network list is.
      //WorkSpace.instance.addObserver(this);

      networkList = new NetworkList(listView);

      // Add click listener
      networkList.addClickListener(new NetworkHandler() {
         @Override
         public void handle(Network network) {
            setNetwork(network);
         }
      });

      // Add edit parameters context menu item.
      networkList.addContextMenuItem("Edit Parameters", new NetworkHandler() {
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
      NetworkController controller = loader.getController();

      controller.setNetwork(getNetwork());

      this.subController = controller;

      networkList.addClickListener(new NetworkHandler() {
         @Override
         public void handle(Network network) {
            subController.setNetwork(network);
         }
      });
   }

   public void saveNetwork() {
      try {
         FileManager.instance.saveNetwork(networkList.getSelectedNetwork());
      } catch (Exception e) { }
   }

   public void newNetwork() {
      String name = DialogFactory.displayTextDialog("Enter network name:");

      if (name != null) {
         try {
            FileManager.instance.saveNetwork(new Network(new ColorSchema()), name);
            loadNetworks();
         } catch (Exception e) {
            DialogFactory.displayErrorDialog("Network with name already exists!");
         }
      }
   }

   @Override
   public void display() {
      loadNetworks();
   }

   private void loadNetworks() {
      networkList.clear();
      for (Network net : FileManager.instance.getNetworks().values())
         networkList.add(net);

      super.clearNetworks();
      super.addNetworks(FileManager.instance.getNetworks().values());
   }
}
