package com.irpgeditor.irpgeditor.event;

import com.irpgeditor.irpgeditor.SourceLine;
import com.irpgeditor.irpgeditor.SourceBlock;

/**
 * @author Derek Van Kooten.
 */
public interface ListenerParserSelection {
  /**
   * is called when a line is requesting to be selected.
   * @param sourceLine SourceLine
   */
  public void requestingFocus(SourceLine sourceLine);
  
  public void requestingFocus(SourceBlock sourceBlock);
}
