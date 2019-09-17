package com.irpgeditor.irpgeditor.event;

import com.irpgeditor.irpgeditor.SubmitJob;

public abstract interface ListenerSubmitJob
{
  public abstract void jobCompleted(SubmitJob paramSubmitJob);
}
