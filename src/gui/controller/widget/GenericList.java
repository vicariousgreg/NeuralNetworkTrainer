package gui.controller.widget;

import application.FileManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import model.network.Network;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpdavis on 6/5/15.
 */
public class GenericList<T> {
   private ListView listView;
   private Network selectedNetwork;
   private ContextMenu contextMenu;

   private List<NetworkHandler> clickHandlers;

   public GenericList(ListView lv) {
      this.listView = lv;
      this.selectedNetwork = null;
      this.contextMenu = new ContextMenu();
      this.clickHandlers = new ArrayList<NetworkHandler>();

      listView.setItems(FXCollections.observableArrayList());

      // Set cell factory to query FileManager for name.
      listView.setCellFactory(new Callback<ListView<Network>, ListCell<Network>>(){
         @Override
         public ListCell<Network> call(ListView<Network> p) {
            ListCell<Network> cell = new ListCell<Network>(){
               @Override
               protected void updateItem(Network t, boolean bln) {
                  super.updateItem(t, bln);
                  if (t != null) {
                     setText(FileManager.instance.getName(t));
                     setContextMenu(contextMenu);
                  }
               }
            };
            return cell;
         }
      });

      // Consume right click events.
      listView.addEventFilter(MouseEvent.MOUSE_PRESSED,
            new EventHandler<MouseEvent>() {
               @Override
               public void handle(MouseEvent event) {
                  if( event.isSecondaryButtonDown()) {
                     event.consume();
                  }
               }
            });

      // Add click listener.
      listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent click) {
            if (click.getButton() == MouseButton.PRIMARY) {
               // Load selection
               Network selection = (Network)
                     listView.getSelectionModel().getSelectedItem();

               if (selection != selectedNetwork) {
                  selectedNetwork = selection;
                  if (selection != null)
                     for (NetworkHandler handler : clickHandlers)
                        handler.handle(selection);
               }
            }
         }
      });
   }

   public Network getSelectedNetwork() {
      return selectedNetwork;
   }

   public List<Network> getAll() {
      return listView.getItems();
   }

   public void add(Network network) {
      if (!listView.getItems().contains(network))
         listView.getItems().add(network);
   }

   public void remove(Network network) {
      if (listView.getItems().contains(network))
         listView.getItems().remove(network);
   }

   public void clear() {
      listView.getItems().clear();
   }

   public void addClickListener(NetworkHandler handler) {
      clickHandlers.add(handler);
   }

   public void addContextMenuItem(String name, NetworkHandler handler) {
      final NetworkHandler finalHandler = handler;

      MenuItem item = new MenuItem(name);
      item.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent e) {
            // Load selection
            Network selection = (Network)
                  listView.getSelectionModel().getSelectedItem();
            if (selection != null)
               finalHandler.handle(selection);
         }
      });
      contextMenu.getItems().add(item);
   }
}
