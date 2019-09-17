package com.irpgeditor.irpgeditor.event;

import com.irpgeditor.irpgeditor.swing.PanelTool;

/**
 * Listens to the paneltool for when actions are added or removed.
 * 
 * @author Derek Van Kooten.
 */
public interface ListenerPanelTool {
  public void requestingFocus(PanelTool panel);
}
