package gui.controller.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gpdavis on 6/5/15.
 */
public abstract class Randomizer<T> {
   /** Randomization listeners. */
   protected List<GenericHandler<T>> handlers =
         new ArrayList<GenericHandler<T>>();
   /** Current value in the randomizer. */
   protected T value;

   /**
    * Randomizes the value in the randomizer.
    */
   public void randomize() {
      render();
      for (GenericHandler handler : handlers)
         handler.handle(value);
   }

   /**
    * Gets the current value.
    * @return value
    */
   public T getValue() {
      return value;
   }

   /**
    * Sets the randomizer value.
    * @param value value to set
    */
   public void setValue(T value) {
      this.value = value;
      render();
      for (GenericHandler handler : handlers)
         handler.handle(value);
   }

   /**
    * Adds a randomizer listener.
    * @param handler handler to add
    */
   public void addListener(GenericHandler<T> handler) {
      handlers.add(handler);
   }

   /**
    * Renders the randomizer.
    */
   public abstract void render();
}
