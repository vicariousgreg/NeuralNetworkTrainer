package gui.controller.widget;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

/**
 * Created by gpdavis on 6/5/15.
 */
public class ColorRandomizer extends Randomizer<Color> {
   /** GUI color rectangle. */
   private Rectangle colorBox;
   /** GUI color picker. */
   private ColorPicker colorPicker;
   /** Random number generator. */
   private Random rand;

   /**
    * Constructor.
    * @param cb color rectangle
    * @param cp color picker
    */
   public ColorRandomizer(Rectangle cb, ColorPicker cp) {
      rand = new Random();
      this.colorBox = cb;
      this.colorPicker = cp;

      colorBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent click) {
            randomize();
         }
      });

      colorPicker.setOnAction(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent e) {
            setValue(colorPicker.getValue());
         }
      });

      randomize();
   }

   @Override
   public void randomize() {
      double r = rand.nextDouble();
      double g = rand.nextDouble();
      double b = rand.nextDouble();
      setValue(new Color(r, g, b, 1.0));

      super.randomize();
   }

   @Override
   public void render() {
      colorPicker.setValue(value);
      colorBox.setFill(value);
   }
}
