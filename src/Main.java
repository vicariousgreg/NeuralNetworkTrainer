import gui.controller.NetworkControllerStack;
import gui.controller.component.NetworkSelectorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.WorkSpace;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
       //FileManager.instance.saveNetwork(new Network(new ColorSchema()), "test.network");

       // Load networks.
       WorkSpace.instance.loadNetworks();

       FXMLLoader loader = new FXMLLoader(
             Main.class.getResource("gui/view/main.fxml"));

       primaryStage.setTitle("Neural Network Trainer");
       primaryStage.setScene(new Scene((Parent)loader.load()));
       NetworkControllerStack.instance.setStage(primaryStage);

       // Push main interaction screen.
       NetworkControllerStack.instance.push(
             Main.class.getResource("gui/view/networkSelector.fxml"));

       NetworkSelectorController nsc = (NetworkSelectorController) NetworkControllerStack.instance.peekController();
       try {
          nsc.setChild(Main.class.getResource("gui/view/interact.fxml"));
       } catch (Exception e) {
          e.printStackTrace();
       }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
