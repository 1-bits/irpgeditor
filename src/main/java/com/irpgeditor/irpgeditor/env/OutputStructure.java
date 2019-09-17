package com.irpgeditor.irpgeditor.env;

import com.irpgeditor.irpgeditor.event.ListenerStructure;
import java.util.*;
import javax.swing.tree.*;


/**
 * @author Derek Van Kooten
 */
public interface OutputStructure {
	@SuppressWarnings("rawtypes")
	public void setStructure(TreeModel model, Enumeration expands, ListenerStructure listener);

	public void removeStructure(TreeModel model);
}
