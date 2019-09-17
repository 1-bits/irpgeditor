package com.irpgeditor.irpgeditor.event;

import com.irpgeditor.irpgeditor.Member;

/**
 * @author not attributable
 */
public interface ListenerMember {
  public void memberChanged(Member member);
  // return true if it is ok to close, otherwise return a false.
  public boolean isOkToClose(Member member);
}
