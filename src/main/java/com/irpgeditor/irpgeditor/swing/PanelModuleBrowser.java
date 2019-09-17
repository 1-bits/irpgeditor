package com.irpgeditor.irpgeditor.swing;

import com.irpgeditor.irpgeditor.event.ListenerAS400System;
import com.irpgeditor.irpgeditor.event.ListenerAS400Systems;
import com.irpgeditor.irpgeditor.env.Environment;
import com.irpgeditor.irpgeditor.icons.Icons;
import com.irpgeditor.irpgeditor.tree.TreeCellRendererNode;
import com.irpgeditor.irpgeditor.tree.TreeModelNode;
import com.irpgeditor.irpgeditor.tree.NodeDefault;
import com.irpgeditor.irpgeditor.tree.Node;
import com.irpgeditor.irpgeditor.tree.TreeClickHandler;
import com.irpgeditor.irpgeditor.tree.TreeExpansionListenerNode;
import com.irpgeditor.irpgeditor.BindingDirectory;
import com.irpgeditor.irpgeditor.BindingDirectoryEntry;
import com.irpgeditor.irpgeditor.Member;
import com.irpgeditor.irpgeditor.Module;
import com.irpgeditor.irpgeditor.AS400System;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Derek Van Kooten
 */
public class PanelModuleBrowser extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4808478177017523614L;
	BorderLayout borderLayout1 = new BorderLayout();
	JScrollPane scrollpaneSystemBrowser = new JScrollPane();
	TreeModelNode treeModel = new TreeModelNode();
	JTree treeSystemBrowser = new JTree();
	TreeCellRendererNode treeCellRendererNode = new TreeCellRendererNode();
	NodeSystems nodeSystems = new NodeSystems(treeModel);
	Logger logger = LoggerFactory.getLogger(PanelModuleBrowser.class);

	public PanelModuleBrowser() {
		try {
			jbInit();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void jbInit() throws Exception {
		this.setLayout(borderLayout1);
		this.add(scrollpaneSystemBrowser, BorderLayout.CENTER);
		scrollpaneSystemBrowser.getViewport().add(treeSystemBrowser, null);
		treeModel.setRoot(nodeSystems);
		treeSystemBrowser.setCellRenderer(treeCellRendererNode);
		treeSystemBrowser.setRootVisible(false);
		treeSystemBrowser.setModel(treeModel);
		treeSystemBrowser.addTreeExpansionListener(new TreeExpansionListenerNode());
		new TreeClickHandler(treeSystemBrowser);
		ToolTipManager.sharedInstance().registerComponent(treeSystemBrowser);
	}

	class NodeSystems extends NodeDefault implements ListenerAS400Systems {
		AS400System as400;
		TreeModelNode treeModel;

		public NodeSystems(TreeModelNode treeModel) {
			this.treeModel = treeModel;
			Environment.systems.addListener(this);
			ArrayList<AS400System> list = Environment.systems.getSystems();
			for (int x = 0; x < list.size(); x++) {
				addedSytem(list.get(x));
			}
		}

		public void addedSytem(AS400System system) {
			add(new NodeAS400(system, this, treeModel));
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					treeModel.structureChanged((NodeDefault) treeModel.getRoot());
				}
			});
		}

		public void removedSytem(AS400System system) {
			NodeAS400 node;

			for (int x = 0; x < list.size(); x++) {
				node = (NodeAS400) list.get(x);
				if (node.as400.equals(system)) {
					list.remove(node);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							treeModel.structureChanged((NodeDefault) treeModel.getRoot());
						}
					});
					return;
				}
			}
		}

		public void defaultSytem(AS400System system) {
		}
	}

	class NodeAS400 extends NodeDefault implements ListenerAS400System {
		AS400System as400;
		TreeModelNode treeModel;
		NodeDefault nodeWait = new NodeDefault(this, "Retrieving libraries...");
		boolean hasExpanded = false;

		public NodeAS400(AS400System as400, Node parent, TreeModelNode treeModel) {
			this.parent = parent;
			this.as400 = as400;
			this.treeModel = treeModel;
			as400.addListener(this);
			add(nodeWait);
		}

		public String getText() {
			if (as400.isConnected()) {
				return as400.getName() + " (Connected)";
			}
			return as400.getName() + " (Disconnected)";
		}

		public Icon getIcon() {
			return Icons.iconSystem;
		}

		public boolean isLeaf() {
			if (hasExpanded == false) {
				return false;
			}
			return super.isLeaf();
		}

		public void expand() {
			if (hasExpanded == false) {
				startRetrieveLibraries();
			}
			hasExpanded = true;
		}

		protected void startRetrieveLibraries() {
			new Thread() {
				public void run() {
					retrieveLibraries();
				}
			}.start();
		}

		protected void retrieveLibraries() {
			ArrayList<String> libs;

			try {
				libs = as400.getLibraries();
				for (int x = 0; x < libs.size(); x++) {
					add(new NodeLibrary(this, as400, libs.get(x), treeModel));
				}
				list.remove(nodeWait);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				nodeWait.setText(e.getMessage());
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					treeModel.structureChanged(NodeAS400.this);
				}
			});
		}

		/**
		 * Is called by the AS400System object when a connection is made.
		 */
		public void connected(AS400System system) {
			// fire a changed event.
		}

		/**
		 * Is called by the AS400System object when a disconnect happens.
		 */
		public void disconnected(AS400System system) {
		}
	}

	class NodeLibrary extends NodeDefault {
		AS400System as400;
		TreeModelNode treeModel;
		boolean hasExpanded = false;
		boolean isRetrieving = false;
		NodeDefault nodeWait = new NodeDefault(this, "Retrieving Binding Directories...");

		public NodeLibrary(Node parent, AS400System as400, String library, TreeModelNode treeModel) {
			super(parent, library);
			this.as400 = as400;
			this.treeModel = treeModel;
			this.icon = Icons.iconLibrary;
			add(nodeWait);
		}

		public String getToolTipText() {
			return as400.getName() + " - " + text;
		}

		public void expand() {
			if (hasExpanded == false) {
				startRetrieveBindingDirectories();
			}
			hasExpanded = true;
		}

		protected void startRetrieveBindingDirectories() {
			new Thread() {
				public void run() {
					retrieveBindingDirectories();
				}
			}.start();
		}

		protected void retrieveBindingDirectories() {
			ArrayList<BindingDirectory> dirs;

			isRetrieving = true;
			try {
				dirs = as400.listBindingDirectories(text, "*BNDDIR");
				for (int x = 0; x < dirs.size(); x++) {
					add(new NodeBindingDirectory(this, dirs.get(x), treeModel));
				}
				list.remove(nodeWait);
			} catch (Exception e) {
				//e.printStackTrace();
				nodeWait.setText(e.getMessage());
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					treeModel.structureChanged(NodeLibrary.this);
				}
			});
			isRetrieving = false;
		}

		public void rightClick(Component invoker, int x, int y) {
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem menuRefresh = new JMenuItem();

			menuRefresh.setText("Refresh");
			if (this.isRetrieving || this.hasExpanded == false) {
				menuRefresh.setEnabled(false);
			} else {
				menuRefresh.setEnabled(true);
			}
			popupMenu.add(menuRefresh);

			menuRefresh.addActionListener(new ActionRefresh());

			popupMenu.show(invoker, x, y);
		}

		protected void disposeBindingDirectories() {
			/*
			 * Object object;
			 * 
			 * for ( int x = 0; x < list.size(); x++ ) { object = list.get(x);
			 * if ( object instanceof NodeFile ) {
			 * ((NodeFile)object).disposeMembers(); } }
			 */
		}

		/**
		 */
		class ActionRefresh implements ActionListener {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent evt) {
				disposeBindingDirectories();
				list.clear();
				list.add(nodeWait);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						treeModel.structureChanged(NodeLibrary.this);
					}
				});
				hasExpanded = false;
				expand();
			}
		}
	}

	class NodeBindingDirectory extends NodeDefault {
		BindingDirectory bd;
		TreeModelNode treeModel;
		boolean hasExpanded = false;
		boolean isRetrieving = false;
		NodeDefault nodeWait = new NodeDefault(this, "Retrieving Binding Directory Entries...");

		public NodeBindingDirectory(Node parent, BindingDirectory bd, TreeModelNode treeModel) {
			super(parent, bd.getName());
			this.bd = bd;
			this.treeModel = treeModel;
			this.icon = Icons.iconBindingDirectory;
			add(nodeWait);
		}

		public String getToolTipText() {
			return bd.getAS400().getName() + " - " + bd.getLibrary() + " - " + text;
		}

		public void expand() {
			if (hasExpanded == false) {
				startRetrieveServicePrograms();
			}
			hasExpanded = true;
		}

		protected void startRetrieveServicePrograms() {
			new Thread() {
				public void run() {
					retrieveServicePrograms();
				}
			}.start();
		}

		@SuppressWarnings("rawtypes")
		protected void retrieveServicePrograms() {
			ArrayList svc;

			isRetrieving = true;
			try {
				svc = bd.getAS400().listEntries(bd);
				for (int x = 0; x < svc.size(); x++) {
					add(new NodeBindingDirectoryEntry(this, (BindingDirectoryEntry) svc.get(x), treeModel));
				}
				list.remove(nodeWait);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				nodeWait.setText(e.getMessage());
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					treeModel.structureChanged(NodeBindingDirectory.this);
				}
			});
			isRetrieving = false;
		}

		public void rightClick(Component invoker, int x, int y) {
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem menuRefresh = new JMenuItem();

			menuRefresh.setText("Refresh");
			if (this.isRetrieving || this.hasExpanded == false) {
				menuRefresh.setEnabled(false);
			} else {
				menuRefresh.setEnabled(true);
			}
			popupMenu.add(menuRefresh);

			menuRefresh.addActionListener(new ActionRefresh());

			popupMenu.show(invoker, x, y);
		}

		protected void disposeServicePrograms() {
			/*
			 * Object object;
			 * 
			 * for ( int x = 0; x < list.size(); x++ ) { object = list.get(x);
			 * if ( object instanceof NodeFile ) {
			 * ((NodeFile)object).disposeMembers(); } }
			 */
		}

		/**
		 */
		class ActionRefresh implements ActionListener {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent evt) {
				disposeServicePrograms();
				list.clear();
				list.add(nodeWait);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						treeModel.structureChanged(NodeBindingDirectory.this);
					}
				});
				hasExpanded = false;
				expand();
			}
		}
	}

	class NodeBindingDirectoryEntry extends NodeDefault {
		BindingDirectoryEntry bde;
		TreeModelNode treeModel;
		boolean hasExpanded = false;
		boolean isRetrieving = false;
		NodeDefault nodeWait;

		public NodeBindingDirectoryEntry(Node parent, BindingDirectoryEntry bde, TreeModelNode treeModel) {
			super(parent, bde.getName());
			this.bde = bde;
			this.treeModel = treeModel;
			if (bde.getType().trim().equalsIgnoreCase("*SRVPGM")) {
				nodeWait = new NodeDefault(this, "Retrieving Modules...");
				this.icon = Icons.iconServiceProgram;
			} else {
				nodeWait = new NodeDefault(this, "Retrieving Exported Procedures...");
				this.icon = Icons.iconModule;
			}
			// this.icon = Icons.iconFiles;
			add(nodeWait);
		}

		public String getToolTipText() {
			return bde.getBindingDirectory().getAS400().getName() + " - " + bde.getBindingDirectory().getLibrary()
					+ " - " + bde.getBindingDirectory().getName() + " - " + text;
		}

		public void expand() {
			if (hasExpanded == false) {
				startRetrieve();
			}
			hasExpanded = true;
		}

		protected void startRetrieve() {
			new Thread() {
				public void run() {
					retrieve();
				}
			}.start();
		}

		@SuppressWarnings("rawtypes")
		protected void retrieve() {
			ArrayList temp;

			isRetrieving = true;
			try {
				if (bde.getType().trim().equalsIgnoreCase("*SRVPGM")) {
					temp = bde.getBindingDirectory().getAS400().listServiceProgramModules(bde.getLibrary(),
							bde.getName());
					for (int x = 0; x < temp.size(); x++) {
						add(new NodeModule(this, (Module) temp.get(x), treeModel));
					}
				} else {
					temp = bde.getBindingDirectory().getAS400().listModuleProcedures(bde.getLibrary(), bde.getName());
					for (int x = 0; x < temp.size(); x++) {
						add(new NodeDefault(this, (String) temp.get(x), Icons.iconExportedProcedure));
					}
				}
				list.remove(nodeWait);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				nodeWait.setText(e.getMessage());
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					treeModel.structureChanged(NodeBindingDirectoryEntry.this);
				}
			});
			isRetrieving = false;
		}

		public void rightClick(Component invoker, int x, int y) {
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem menuRefresh = new JMenuItem();

			menuRefresh.setText("Refresh");
			if (this.isRetrieving || this.hasExpanded == false) {
				menuRefresh.setEnabled(false);
			} else {
				menuRefresh.setEnabled(true);
			}
			popupMenu.add(menuRefresh);

			menuRefresh.addActionListener(new ActionRefresh());

			popupMenu.show(invoker, x, y);
		}

		protected void dispose() {
			/*
			 * Object object;
			 * 
			 * for ( int x = 0; x < list.size(); x++ ) { object = list.get(x);
			 * if ( object instanceof NodeFile ) {
			 * ((NodeFile)object).disposeMembers(); } }
			 */
		}

		/**
		 */
		class ActionRefresh implements ActionListener {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent evt) {
				dispose();
				list.clear();
				list.add(nodeWait);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						treeModel.structureChanged(NodeBindingDirectoryEntry.this);
					}
				});
				hasExpanded = false;
				expand();
			}
		}
	}

	class NodeModule extends NodeDefault {
		Module module;
		TreeModelNode treeModel;
		boolean hasExpanded = false;
		boolean isRetrieving = false;
		NodeDefault nodeWait;

		public NodeModule(Node parent, Module module, TreeModelNode treeModel) {
			super(parent, module.getName());
			this.module = module;
			this.treeModel = treeModel;
			nodeWait = new NodeDefault(this, "Retrieving Exported Procedures...");
			this.icon = Icons.iconModule;
			add(nodeWait);
		}

		public String getToolTipText() {
			return "";
			// return bde.getBindingDirectory().getAS400().getName() + " - " +
			// bde.getBindingDirectory().getLibrary() + " - " +
			// bde.getBindingDirectory().getName() + " - " + text;
		}

		public void expand() {
			if (hasExpanded == false) {
				startRetrieve();
			}
			hasExpanded = true;
		}

		protected void startRetrieve() {
			new Thread() {
				public void run() {
					retrieve();
				}
			}.start();
		}

		@SuppressWarnings("rawtypes")
		protected void retrieve() {
			ArrayList temp;

			isRetrieving = true;
			try {
				temp = module.getSystem().listModuleProcedures(module.getLibrary(), module.getName());
				for (int x = 0; x < temp.size(); x++) {
					add(new NodeProcedure(this, (String) temp.get(x), module));
				}
				list.remove(nodeWait);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error(e.getMessage());
				nodeWait.setText(e.getMessage());
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					treeModel.structureChanged(NodeModule.this);
				}
			});
			isRetrieving = false;
		}

		public void click(int count) {
			if (count < 2) {
				return;
			}
			if (module.getSourceLibrary().trim().length() == 0 || module.getSourceFile().trim().length() == 0
					|| module.getSourceMember().trim().length() == 0) {
				JOptionPane.showMessageDialog(null, "Unable to determine location of source code.");
				return;
			}
			Environment.members.open(new Member(module.getSystem(), module.getSourceLibrary(), module.getSourceFile(),
					module.getSourceMember()));
		}

		public void rightClick(Component invoker, int x, int y) {
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem menuRefresh = new JMenuItem();

			menuRefresh.setText("Refresh");
			if (this.isRetrieving || this.hasExpanded == false) {
				menuRefresh.setEnabled(false);
			} else {
				menuRefresh.setEnabled(true);
			}
			popupMenu.add(menuRefresh);

			menuRefresh.addActionListener(new ActionRefresh());

			popupMenu.show(invoker, x, y);
		}

		protected void dispose() {
			/*
			 * Object object;
			 * 
			 * for ( int x = 0; x < list.size(); x++ ) { object = list.get(x);
			 * if ( object instanceof NodeFile ) {
			 * ((NodeFile)object).disposeMembers(); } }
			 */
		}

		/**
		 */
		class ActionRefresh implements ActionListener {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent evt) {
				dispose();
				list.clear();
				list.add(nodeWait);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						treeModel.structureChanged(NodeModule.this);
					}
				});
				hasExpanded = false;
				expand();
			}
		}
	}

	class NodeProcedure extends NodeDefault {
		Module module;

		public NodeProcedure(Node parent, String name, Module module) {
			super(parent, name);
			this.module = module;
			this.icon = Icons.iconExportedProcedure;
		}

		public String getToolTipText() {
			return "";
			// return bde.getBindingDirectory().getAS400().getName() + " - " +
			// bde.getBindingDirectory().getLibrary() + " - " +
			// bde.getBindingDirectory().getName() + " - " + text;
		}

		public void click(int count) {
			if (count < 2) {
				return;
			}
			if (module.getSourceLibrary().trim().length() == 0 || module.getSourceFile().trim().length() == 0
					|| module.getSourceMember().trim().length() == 0) {
				JOptionPane.showMessageDialog(null, "Unable to determine location of source code.");
				return;
			}
			Environment.members.open(new Member(module.getSystem(), module.getSourceLibrary(), module.getSourceFile(),
					module.getSourceMember()));
		}
	}
}
