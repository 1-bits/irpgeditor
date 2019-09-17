package com.irpgeditor.irpgeditor.env;

import com.irpgeditor.irpgeditor.Member;
import com.irpgeditor.irpgeditor.AS400System;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CopyMember implements CopyRequest {
  Member member;

  public CopyMember(Member member) {
    this.member = member;
  }

  public void copyTo(AS400System as400, String library, String file) throws Exception {
	  this.member.copyTo(as400, library, file, this.member.getName(), null);
  }

  public void copyTo(AS400System as400, String library) throws Exception {
  }
}
