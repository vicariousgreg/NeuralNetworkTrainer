package gui.controller.widget;

/**
 * Created by gpdavis on 6/5/15.
 */
public abstract class GenericHandler<T> {
   /**
    * Simple handler callback.
    * @param item item to be handled
    */
   public abstract void handle(T item);
}
