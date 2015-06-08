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
   /** GUI list view. */
   private ListView listView;
   /** Selected item in the list. */
   private T selectedItem;
   /** Context menu. */
   private ContextMenu contextMenu;

   /** All string. */
   public static final String kAll = "=== ALL ===";

   /** Whether an 'all' item exists in this list. */
   private boolean containsAllItem;

   /** Click handler for 'All' item, if one exists. */
   private GenericHandler<T> allHandler;

   /** Registered click handlers. */
   private List<GenericHandler<T>> clickHandlers;

   /**
    * Constructor.
    * @param lv list view to hook into
    */
   public GenericList(ListView lv) {
      this.listView = lv;
      this.selectedItem = null;
      this.contextMenu = new ContextMenu();
      this.containsAllItem = false;
      this.clickHandlers = new ArrayList<GenericHandler<T>>();

      listView.setItems(FXCollections.observableArrayList());

      // Set cell factory to query FileManager for name.
      listView.setCellFactory(new Callback<ListView, ListCell>(){
         @Override
         public ListCell call(ListView p) {
            ListCell cell = new ListCell(){
               @Override
               protected void updateItem(Object t, boolean bln) {
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
               Object selection = listView.getSelectionModel().getSelectedItem();

               if (selection == null) {
                  return;
               } else if (selection.equals(kAll)) {
                  if (allHandler != null)
                     selectedItem = null;
                     allHandler.handle(null);
               } else {
                  selectedItem = (T) selection;
                  if (selectedItem != null) {
                     for (GenericHandler<T> handler : clickHandlers)
                        handler.handle(selectedItem);
                  }
               }
            }
         }
      });
   }

   /**
    * Sets the selected item in the list.
    * @param newSelection item to select
    */
   public void setSelectedItem(T newSelection) {
      if (listView.getItems().contains(newSelection)) {
         selectedItem = newSelection;
         listView.getSelectionModel().select(newSelection);
      }
   }

   /**
    * Gets the selected item in the list.
    * @return selected item
    */
   public T getSelectedItem() {
      return selectedItem;
   }

   /**
    * Gets a list of all items in this list.
    * @return list of items
    */
   public List<T> getAll() {
      return listView.getItems();
   }

   /**
    * Adds an 'all' item to the list
    * @param handler all item click handler
    */
   public void addAllItem(GenericHandler<T> handler) {
      if (!containsAllItem) {
         containsAllItem = true;
         listView.getItems().add(0, kAll);
      }
      allHandler = handler;
   }

   /**
    * Adds multiple items to the list.
    * @param items items to add
    */
   public void addAll(T[] items) {
      for (int i = 0; i < items.length; ++i) {
         add(items[i]);
      }
   }

   /**
    * Adds a single item to the list.
    * @param item item to add
    */
   public void add(T item) {
      if (!listView.getItems().contains(item))
         listView.getItems().add(item);
   }

   /**
    * Removes an item from the list, if it exists.
    * @param item item to remove
    */
   public void remove(T item) {
      if (listView.getItems().contains(item))
         listView.getItems().remove(item);
   }

   /**
    * Clears the list.
    */
   public void clear() {
      listView.getItems().clear();
      if (containsAllItem)
         listView.getItems().add(kAll);
   }

   /**
    * Adds a click listener.
    * @param handler on click handler
    */
   public void addClickListener(GenericHandler<T> handler) {
      clickHandlers.add(handler);
   }

   /**
    * Adds a context menu item.
    * @param name menu item name
    * @param handler click handler for context menu item
    */
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
