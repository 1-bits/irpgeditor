package com.irpgeditor.irpgeditor.env;

import com.irpgeditor.irpgeditor.event.ListenerStructure;
import java.util.*;
import javax.swing.tree.*;


/**
 * @author Derek Van Kooten.
 */
public class Structure {
  OutputStructure output;
  
  public void setOutput(OutputStructure output) {
    this.output = output;
  }
  
  @SuppressWarnings("rawtypes")
	public void setStructure(TreeModel model, Enumeration expands, ListenerStructure listener) {
    output.setStructure(model, expands, listener);
  }
  
  public void removeStructure(TreeModel model) {
    output.removeStructure(model);
  }
}
