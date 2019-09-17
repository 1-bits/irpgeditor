package com.irpgeditor.irpgeditor.swing;

import com.irpgeditor.irpgeditor.event.ListenerProjects;
import com.irpgeditor.irpgeditor.env.Environment;
import com.irpgeditor.irpgeditor.Project;
import javax.swing.*;


/**
 * @author Derek Van Kooten
 */
@SuppressWarnings("rawtypes")
public class ComboBoxModelProjects extends AbstractListModel implements javax.swing.ComboBoxModel, ListenerProjects {
  /**
	 * 
	 */
	private static final long serialVersionUID = 3303495099645947565L;

public ComboBoxModelProjects() {
    Environment.projects.addListener(this);
  }

  public Object getSelectedItem() {
    return Environment.projects.getSelected();
  }

  public void setSelectedItem(Object object) {
    Environment.projects.select((Project)object);
  }

  public Object getElementAt(int index) {
    return Environment.projects.get(index);
  }

  public int getSize() {
    return Environment.projects.getSize();
  }

  public void added(Project project, int index) {
    fireIntervalAdded(this, index, index);
  }

  public void removed(Project project, int index) {
    fireIntervalRemoved(this, index, index);
  }

  public void selected(Project project) {}
}
