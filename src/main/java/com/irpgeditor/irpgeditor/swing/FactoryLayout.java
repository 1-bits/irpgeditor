package com.irpgeditor.irpgeditor.swing;

import com.irpgeditor.irpgeditor.LayoutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gets called when someone wants to see the layout of a file.
 * 
 * @author not attributable
 */
public class FactoryLayout implements FactoryPanelTool {
	Logger logger = LoggerFactory.getLogger(FactoryLayout.class);
  public PanelTool construct(Object object) {
    PanelLayout panelLayout;
    LayoutRequest request;

    try {
      request = (LayoutRequest)object;
      panelLayout = new PanelLayout();
      panelLayout.setName(request.getParsedName().toUpperCase());
      panelLayout.setLayoutRequest(request);
      return panelLayout;
    }
    catch (Exception e) {
      //e.printStackTrace();
      logger.error(e.getMessage());
      return null;
    }
  }
}
