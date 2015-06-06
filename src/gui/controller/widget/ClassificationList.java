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
public class ClassificationList {
   private ListView listView;
   private String selectedClassification;
   private ContextMenu contextMenu;

   private List<ClassificationHandler> clickHandlers;

   public ClassificationList(ListView lv) {
      this.listView = lv;
      this.selectedClassification = null;
      this.contextMenu = new ContextMenu();
      this.clickHandlers = new ArrayList<ClassificationHandler>();

      listView.setItems(FXCollections.observableArrayList());

      // Set cell factory to query FileManager for name.
      listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>(){
         @Override
         public ListCell<String> call(ListView<String> p) {
            ListCell<String> cell = new ListCell<String>(){
               @Override
               protected void updateItem(String t, boolean bln) {
                  super.updateItem(t, bln);
                  if (t != null) {
                     setText(t);
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
               String selection = (String)
                     listView.getSelectionModel().getSelectedItem();

               selectedClassification = selection;
               if (selection != null)
                  for (ClassificationHandler handler : clickHandlers)
                     handler.handle(selection);
            }
         }
      });
   }

   public void setSelectedClassification(String newClass) {
      if (listView.getItems().contains(newClass)) {
         selectedClassification = newClass;
         listView.getSelectionModel().select(newClass);
      }
   }

   public String getSelectedClassification() {
      return selectedClassification;
   }

   public List<String> getAll() {
      return listView.getItems();
   }

   public void add(String classification) {
      if (!listView.getItems().contains(classification))
         listView.getItems().add(classification);
   }

   public void remove(String classification) {
      if (listView.getItems().contains(classification))
         listView.getItems().remove(classification);
   }

   public void clear() {
      listView.getItems().clear();
   }

   public void addClickListener(ClassificationHandler handler) {
      clickHandlers.add(handler);
   }

   public void addContextMenuItem(String name, ClassificationHandler handler) {
      final ClassificationHandler finalHandler = handler;

      MenuItem item = new MenuItem(name);
      item.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent e) {
            // Load selection
            String selection = (String)
                  listView.getSelectionModel().getSelectedItem();
            if (selection != null)
               finalHandler.handle(selection);
         }
      });
      contextMenu.getItems().add(item);
   }
}
