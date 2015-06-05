import application.DialogFactory;
import application.FileManager;
import gui.controller.NetworkControllerStack;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.WorkSpace;
import model.network.Network;
import model.network.schema.ColorSchema;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
       //FileManager.instance.saveNetwork(new Network(new ColorSchema()), "test.network");

       // Load networks;
       WorkSpace.instance.loadNetworks();

       FXMLLoader loader = new FXMLLoader(
             Main.class.getResource("gui/view/main.fxml"));

       primaryStage.setTitle("Neural Network Trainer");
       primaryStage.setScene(new Scene((Parent)loader.load()));
       NetworkControllerStack.instance.setStage(primaryStage);

       // Push main interaction screen.
       NetworkControllerStack.instance.push(
             Main.class.getResource("gui/view/interact.fxml"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
