package gui.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

public class NetworkControllerStack implements Initializable {
   /** Singleton instance. */
   public static NetworkControllerStack instance = new NetworkControllerStack();

   @FXML Button backButton;
   @FXML Pane pane;
   @FXML ProgressBar progressBar;

   /** Primary stage. */
   private Stage stage;

   /** Controller/view stack. */
   private Stack<ControllerNode> stack;

   /**
    * Constructor.
    */
   public NetworkControllerStack() {
      this.stack = new Stack<ControllerNode>();
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      instance = this;
      backButton.setVisible(false);
   }

   /**
    * Setter for the stage.
    * @param stage stage
    */
   public void setStage(Stage stage) {
      this.stage = stage;
   }

   /**
    * Getter for the stage.
    * @return stage
    */
   public Stage getStage() {
      return stage;
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
      stack.push(new ControllerNode(controller, node));

      display();
   }

   /**
    * Pops a controller/view off the stack and restores the previous.
    */
   public void pop() {
      stack.pop();
      display();
   }

   /**
    * Gets the controller from the top of the stack.
    * @return controller on top of stack
    */
   public NetworkController peekController() {
      return stack.peek().controller;
   }

   /**
    * Displays the view on the top of the stack.
    */
   public void display() {
      // Display back button if necessary.
      backButton.setVisible(stack.size() > 1);

      ControllerNode cn = stack.peek();

      // Display node
      pane.getChildren().clear();
      pane.getChildren().add(cn.node);

      cn.controller.display();
      stage.sizeToScene();
      stage.show();
   }

   /**
    * Posts a task, binding it to the progress bar.
    * @param task
    */
   public void postTask(Task task) {
      progressBar.progressProperty().bind(task.progressProperty());
      task.stateProperty().addListener(new ChangeListener<Worker.State>() {
         @Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
            if (newState == Worker.State.SUCCEEDED) {
               progressBar.progressProperty().unbind();
               progressBar.setProgress(0);
            }
         }
      });
      new Thread(task).start();
   }

   /**
    * Controller/view combination class.
    */
   private class ControllerNode {
      public final NetworkController controller;
      public final Node node;

      public ControllerNode(NetworkController controller, Node node) {
         this.controller = controller;
         this.node = node;
      }
   }
}
