package com.irpgeditor.irpgeditor.event;

import javax.swing.*;

/**
 * @author Derek Van Kooten
 */
public interface ListenerActions {
  public void actionsAdded(Action[] actions);
  public void actionsRemoved(Action[] actions);
}
