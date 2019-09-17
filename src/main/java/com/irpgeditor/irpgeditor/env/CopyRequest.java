package com.irpgeditor.irpgeditor.env;

import com.irpgeditor.irpgeditor.AS400System;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public interface CopyRequest {
  public void copyTo(AS400System as400, String library, String file) throws Exception;
  public void copyTo(AS400System as400, String library) throws Exception;
}
