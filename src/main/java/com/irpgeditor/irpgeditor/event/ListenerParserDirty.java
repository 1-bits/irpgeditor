package com.irpgeditor.irpgeditor.event;

import com.irpgeditor.irpgeditor.SourceParser;

/**
 * @author not attributable
 */
public interface ListenerParserDirty {
  public void parserDirty(SourceParser parser, boolean dirty);
}
