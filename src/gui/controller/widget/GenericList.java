package gui.controller.widget;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpdavis on 6/5/15.
 */
public class GenericList<T> {
   private ListView listView;
   private T selectedItem;
   private ContextMenu contextMenu;

   private List<GenericHandler<T>> clickHandlers;

   public GenericList(ListView lv) {
      this.listView = lv;
      this.selectedItem = null;
      this.contextMenu = new ContextMenu();
      this.clickHandlers = new ArrayList<GenericHandler<T>>();

      listView.setItems(FXCollections.observableArrayList());

      // Set cell factory to query FileManager for name.
      listView.setCellFactory(new Callback<ListView<T>, ListCell<T>>(){
         @Override
         public ListCell<T> call(ListView<T> p) {
            ListCell<T> cell = new ListCell<T>(){
               @Override
               protected void updateItem(T t, boolean bln) {
                  super.updateItem(t, bln);
                  if (t != null) {
                     setText(t.toString());
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
               T selection = (T)
                     listView.getSelectionModel().getSelectedItem();

               selectedItem = selection;
               if (selection != null)
                  for (GenericHandler<T> handler : clickHandlers)
                     handler.handle(selection);
            }
         }
      });
   }

   public void setSelectedItem(T newSelection) {
      if (listView.getItems().contains(newSelection)) {
         selectedItem = newSelection;
         listView.getSelectionModel().select(newSelection);
      }
   }

   public T getSelectedItem() {
      return selectedItem;
   }

   public List<T> getAll() {
      return listView.getItems();
   }

   public void add(T item) {
      if (!listView.getItems().contains(item))
         listView.getItems().add(item);
   }

   public void remove(T item) {
      if (listView.getItems().contains(item))
         listView.getItems().remove(item);
   }

   public void clear() {
      listView.getItems().clear();
   }

   public void addClickListener(GenericHandler<T> handler) {
      clickHandlers.add(handler);
   }

   public void addContextMenuItem(String name, GenericHandler<T> handler) {
      final GenericHandler<T> finalHandler = handler;

      MenuItem item = new MenuItem(name);
      item.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent e) {
            // Load selection
            T selection = (T)
                  listView.getSelectionModel().getSelectedItem();
            if (selection != null)
               finalHandler.handle(selection);
         }
      });
      contextMenu.getItems().add(item);
   }
}
