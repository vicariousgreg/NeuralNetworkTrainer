package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class NetworkControllerStack implements Initializable {
   public static NetworkControllerStack instance = new NetworkControllerStack();

   @FXML Button backButton;
   @FXML Pane pane;

   private Stage stage;
   private Stack<ControllerScene> stack;

   public NetworkControllerStack() {
      this.stack = new Stack<ControllerScene>();
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      instance = this;
      backButton.setVisible(false);
   }

   public void setStage(Stage stage) {
      this.stage = stage;
   }

   /**
    * Pushes a controller/view onto the stack.
    * Propagates the selected network.
    *
    * @param resource FXML resource
    * @throws IOException when resource is invalid
    */
   public void push(URL resource) throws IOException {
      // Load resource, extract node and controller
      FXMLLoader loader = new FXMLLoader(resource);
      Node node = loader.load();
      NetworkController controller = loader.getController();

      // Propagate network
      if (!stack.empty()) {
         controller.setNetwork(stack.peek().controller.getNetwork());
      }

      // Add new node
      stack.push(new ControllerScene(controller, node));

      display();
   }

   /**
    * Pops a controller/view off the stack and restores the previous.
    */
   public void pop() {
      stack.pop();
      display();
   }

   public void display() {
      // Display back button if necessary.
      backButton.setVisible(stack.size() > 1);

      // Display node
      pane.getChildren().clear();
      pane.getChildren().add(stack.peek().node);

      stack.peek().controller.display();
      stage.sizeToScene();
      stage.show();
   }

   /**
    * Controller/view combination class.
    */
   private class ControllerScene {
      public final NetworkController controller;
      public final Node node;

      public ControllerScene(NetworkController controller, Node node) {
         this.controller = controller;
         this.node = node;
      }
   }
}
