package gui.controller.widget;

import model.network.Network;

/**
 * Created by gpdavis on 6/5/15.
 */
public abstract class GenericHandler<T> {
   public abstract void handle(T item);
}
