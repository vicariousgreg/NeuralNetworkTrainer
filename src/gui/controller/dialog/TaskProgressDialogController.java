package gui.controller.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TaskProgressDialogController extends DialogController {
   @FXML Text textBox;
   @FXML ProgressIndicator progress;

   public void setText(String text) {
      this.textBox.setText(text);
   }

   public void runOnStage(Task task, Stage stage) {
      progress.progressProperty().bind(task.progressProperty());
      task.stateProperty().addListener(new ChangeListener<Worker.State>() {
         @Override public void changed(ObservableValue<? extends Worker.State> observableValue,
                                       Worker.State oldState, Worker.State newState) {
            if (newState == Worker.State.SUCCEEDED) {
               confirm();
               progress.progressProperty().unbind();
               progress.setProgress(0);
            }
         }
      });
      new Thread(task).start();
      stage.showAndWait();
   }

   @Override
   public void confirm() {
      setResponseValue(true);
      close();
   }
}
