package application;

import gui.controller.dialog.*;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.network.*;
import model.network.schema.*;

import java.io.*;

/**
 * Created by gpdavis on 6/4/15.
 */
public class DialogFactory {
   /**
    * Displays and waits for a confirmation dialog.
    * @param text text to display
    * @return user response
    */
   public static boolean displayConfirmationDialog(String text) {
      final Response<Boolean> response = new Response<Boolean>(false);

      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/confirmationDialog.fxml"));
         Parent root = loader.load();
         ConfirmationDialogController controller = loader.getController();
         controller.setText(text);
         controller.setResponse(response);

         display(root);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return response.getValue();
   }

   /**
    * Displays and waits for a text box dialog.
    * @param text text to display
    * @return user response
    */
   public static String displayTextDialog(String text) {
      final Response<String> response = new Response<String>(null);

      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/textDialog.fxml"));
         Parent root = loader.load();
         TextDialogController controller = loader.getController();
         controller.setText(text);
         controller.setResponse(response);

         display(root);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return response.getValue();
   }

   /**
    * Displays and waits for an error dialog.
    * @param text text to display
    */
   public static void displayErrorDialog(String text) {
      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/errorDialog.fxml"));
         Parent root = loader.load();
         ErrorDialogController controller = loader.getController();
         controller.setText(text);

         display(root);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   /**
    * Displays and waits for a task progress dialog.
    * @param text text to display
    * @param task task to run
    * @return whether the task was successfully run
    */
   public static boolean displayTaskProgressDialog(String text, Task task) {
      final Response<Boolean> response = new Response<Boolean>(false);

      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/taskProgressDialog.fxml"));
         Parent root = loader.load();
         TaskProgressDialogController controller = loader.getController();
         controller.setText(text);
         controller.setResponse(response);

         Stage dialogStage = new Stage();
         dialogStage.setScene(new Scene(root));
         dialogStage.requestFocus();
         dialogStage.initStyle(StageStyle.UNDECORATED);
         dialogStage.initModality(Modality.APPLICATION_MODAL);

         controller.runOnStage(task, dialogStage);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return response.getValue();
   }

   /**
    * Displays a file chooser.
    * @param stage stage to display on
    * @return chosen file
    */
   public static File displayFileChooser(Stage stage) {
      FileChooser fileChooser = new FileChooser();
      return fileChooser.showOpenDialog(stage);
   }

   /**
    * Displays and waits for a load dialog.
    * @param stage stage to display on
    * @return loaded object, or null if unable to load
    */
   public static Object displayLoadDialog(Stage stage) {
      File file = displayFileChooser(stage);
      if (file != null) {
         try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Object toReturn = ois.readObject();
            ois.close();
            return toReturn;
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return null;
   }

   /**
    * Displays and waits for a save dialog.
    * @param toSave object to save
    * @param stage stage to display on
    * @return whether save was successful
    */
   public static boolean displaySaveDialog(Object toSave, Stage stage) {
      File file = displayFileChooser(stage);
      if (file != null) {
         try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(toSave);
            out.close();
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
      }
      return true;
   }

   /**
    * Displays and waits for a parameters dialog.
    * @return created parameters
    */
   public static Parameters displayParametersDialog(Parameters params) {
      final Response<Parameters> response = new Response<Parameters>(null);

      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/parametersDialog.fxml"));
         Parent root = loader.load();
         ParametersDialogController controller = loader.getController();
         controller.setParameters(params);
         controller.setResponse(response);

         display(root);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return response.getValue();
   }

   /**
    * Displays and waits for a schema dialog.
    * @return created schema
    */
   public static Schema displaySchemaDialog(Schema schema) {
      final Response<Schema> response = new Response<Schema>(null);

      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/schemaDialog.fxml"));
         Parent root = loader.load();
         SchemaDialogController controller = loader.getController();
         controller.setSchema(schema);
         controller.setResponse(response);

         display(root);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return response.getValue();
   }

   /**
    * Displays and waits for a network dialog.
    * @return created network
    */
   public static Network displayNetworkDialog() {
      final Response<Network> response = new Response<Network>(null);

      // Load resource, extract node and controller
      try {
         FXMLLoader loader = new FXMLLoader(DialogFactory.class.getResource(
               "../gui/view/dialog/networkDialog.fxml"));
         Parent root = loader.load();
         NetworkDialogController controller = loader.getController();
         controller.setResponse(response);

         display(root);
      } catch (Exception e) {
         e.printStackTrace();
      }

      return response.getValue();
   }

   /**
    * Displays a parent in a modal dialog.
    * @param root parent to display
    */
   private static void display(Parent root) {
      Stage dialogStage = new Stage();
      dialogStage.setScene(new Scene(root));
      dialogStage.requestFocus();
      dialogStage.initModality(Modality.APPLICATION_MODAL);
      dialogStage.showAndWait();
   }

   /**
    * Dialog response class.
    * @param <T> the type of user response
    */
   public static class Response<T> {
      private T value;

      public Response (T defaultValue) {
         this.value = defaultValue;
      }

      public void setValue(T val) {
         this.value = val;
      }

      public T getValue() {
         return value;
      }
   }
}
