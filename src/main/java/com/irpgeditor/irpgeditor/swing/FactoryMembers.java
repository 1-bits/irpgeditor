package com.irpgeditor.irpgeditor.swing;

import com.irpgeditor.irpgeditor.ProjectMember;
import com.irpgeditor.irpgeditor.PanelMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author not attributable
 */
public class FactoryMembers implements FactoryPanelTool {
	Logger logger = LoggerFactory.getLogger(FactoryMembers.class);
  public PanelTool construct(Object object) {
    PanelTool panelTool;
    ProjectMember projectMember;

    try {
      projectMember = (ProjectMember)object;
      panelTool = new PanelMember(projectMember);
      panelTool.setName(projectMember.getMember().getName());
      return panelTool;
    }
    catch (Exception e) {
      //e.printStackTrace();
    	logger.error(e.getMessage());
      return null;
    }
  }
}
