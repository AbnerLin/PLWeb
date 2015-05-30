package org.plweb.jedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.plweb.suite.common.xml.XProject;

public class FrameProjectEditor extends JFrame implements ActionListener {
	
	private static final int width = 640;
	
	private static final int height = 480;

	private static final long serialVersionUID = 8335165746820233953L;

	private XProject project;

	//private JTextField tfId;

	private JTextField tfTitle;

	private JTable tProperties;

	private DefaultTableModel tmProperties;

	public FrameProjectEditor(XProject project) {
		this.project = project;

		init();
		setTitle("Project Editor - " + project.getTitle());
		
		setPreferredSize(new Dimension(width, height));
		setResizable(true);
		pack();
		
		setVisible(true);		
	}

	private void init() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.add(createBasicPanel(), BorderLayout.NORTH);
		p.add(createAdvancedPanel(), BorderLayout.CENTER);
		
		JButton btn;
		btn = new JButton("Confirm");
		btn.setActionCommand("confirm");
		btn.addActionListener(this);
		p.add(btn, BorderLayout.SOUTH);
		
		getContentPane().add(p);
	}

	private JPanel createBasicPanel() {
		JPanel p = new JPanel(new SpringLayout());
		p.setBorder(BorderFactory.createTitledBorder("Basic Project Settings"));

//		p.add(new JLabel("ID: ", JLabel.RIGHT));
//		p.add(tfId = new JTextField(project.getId()));
//		tfId.setEditable(false);

		p.add(new JLabel("Title: ", JLabel.RIGHT));
		p.add(tfTitle = new JTextField(project.getTitle()));

		SpringUtilities.makeCompactGrid(p, 1, 2, 0, 0, 2, 2);

		return p;
	}

	private GridLayout makeGapGridLayout() {
		GridLayout layout = new GridLayout();
		layout.setHgap(2);
		layout.setVgap(2);
		return layout;
	}

	private JPanel createPropertiesPanel() {
		tmProperties = new DefaultTableModel();
		tmProperties.addColumn("Name");
		tmProperties.addColumn("Value");
		int[] psizes = { 100, 300 };

		for (String name : project.getProperties().keySet()) {
			String value = project.getProperties().get(name);
			tmProperties.addRow(new String[] { name, value });
		}

		tProperties = new JTable(tmProperties);
		tProperties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tProperties.setRowSelectionAllowed(true);

		for (int id = 0; id < psizes.length; id++) {
			tProperties.getColumnModel().getColumn(id).setPreferredWidth(
					psizes[id]);
		}

		JPanel pProperties = new JPanel(makeGapGridLayout());
		pProperties.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JButton btn;
		
		btn = new JButton("Add");
		btn.setActionCommand("add-property");
		btn.addActionListener(this);
		pProperties.add(btn);
		
		btn = new JButton("Remove");
		btn.setActionCommand("remove-property");
		btn.addActionListener(this);
		pProperties.add(btn);

		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createLoweredBevelBorder());
		p.add(new JScrollPane(tProperties), BorderLayout.CENTER);
		p.add(pProperties, BorderLayout.SOUTH);

		return p;
	}

	private JComponent createAdvancedPanel() {
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Properties", createPropertiesPanel());
		return tabPane;
	}

	public void actionPerformed(ActionEvent actionevent) {

		String action = actionevent.getActionCommand();

		if ("confirm".equals(action)) {
			updateProject();
			this.setVisible(false);
		} else if ("add-property".equals(action)) {
			tmProperties.addRow(new String[tmProperties.getColumnCount()]);
		} else if ("remove-property".equals(action)) {
			tmProperties.removeRow(tProperties.getSelectedRow());
		} else if ("up-property".equals(action)) {
			ListMoveUp(tProperties, tmProperties);
		} else if ("down-property".equals(action)) {
			ListMoveDown(tProperties, tmProperties);
		}

	}

	private void ListMoveUp(JTable table, DefaultTableModel tableModel) {
		int row = table.getSelectedRow();
		if (row != -1 && row - 1 >= 0) {
			String[] rowData = new String[tableModel.getColumnCount()];
			for (int i = 0; i < rowData.length; i++) {
				rowData[i] = (String) tableModel.getValueAt(row, i);
			}
			tableModel.removeRow(row);
			tableModel.insertRow(row - 1, rowData);
			table.setRowSelectionInterval(row - 1, row - 1);
		}
	}

	private void ListMoveDown(JTable table, DefaultTableModel tableModel) {
		int row = table.getSelectedRow();
		if (row != -1 && row + 1 < tableModel.getRowCount()) {
			String[] rowData = new String[tableModel.getColumnCount()];
			for (int i = 0; i < rowData.length; i++) {
				rowData[i] = (String) tableModel.getValueAt(row, i);
			}
			tableModel.removeRow(row);
			tableModel.insertRow(row + 1, rowData);
			table.setRowSelectionInterval(row + 1, row + 1);
		}
	}
	
	private void updateProject() {
		//project.setId(tfId.getText());
		project.setTitle(tfTitle.getText());
		
		// renew properties
		project.getProperties().clear();
		for (int row = 0; row < tmProperties.getRowCount(); row++) {
			String key = (String) tmProperties.getValueAt(row, 0);
			String value = (String) tmProperties.getValueAt(row, 1); 
			project.addProperty(key, value);
		}
	}
}
