package org.plweb.jedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.plweb.suite.common.xml.XCommand;
import org.plweb.suite.common.xml.XTask;

public class FrameTaskEditor extends JFrame implements ActionListener,
		TableModelListener {

	private static final long serialVersionUID = 8335165746820233953L;

	private XTask task;

	private JTextField tfId;

	private JTextField tfTitle;

	private JTable tCommands;

	private DefaultTableModel tmCommands;

	private JTable propstable;

	private DefaultTableModel propsmodel;

	private CompilerUserInterface iface;

	public FrameTaskEditor(CompilerUserInterface iface, XTask task) {
		this.iface = iface;
		this.task = task;

		init();
		setTitle("Task Editor - " + task.getTitle());
		setPreferredSize(new Dimension(800, 600));
		setResizable(true);
		pack();
		setVisible(true);
	}

	private void init() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		p.add(createBasicComponent(), BorderLayout.NORTH);
		p.add(createAdvancedComponent(), BorderLayout.CENTER);

		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.setActionCommand("confirm");
		btnConfirm.addActionListener(this);
		p.add(btnConfirm, BorderLayout.SOUTH);
		add(p);
	}

	private JComponent createBasicComponent() {
		JPanel p = new JPanel(new SpringLayout());
		p.setBorder(BorderFactory.createTitledBorder("Basic Task Settings"));

		p.add(new JLabel("ID: ", JLabel.RIGHT));
		p.add(tfId = new JTextField(task.getId()));
		tfId.setEditable(false);

		p.add(new JLabel("Title: ", JLabel.RIGHT));
		p.add(tfTitle = new JTextField(task.getTitle()));

		SpringUtilities.makeCompactGrid(p, 2, 2, 0, 0, 2, 2);

		return p;
	}

	private JComponent createAdvancedComponent() {
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Commands", createCommandsPanel());
		tabPane.addTab("Properties", createPropertiesPanel());
		return tabPane;
	}

	private JPanel createCommandsPanel() {
		tmCommands = new DefaultTableModel();
		tmCommands.addColumn("Mode");
		tmCommands.addColumn("Name");
		tmCommands.addColumn("Type");
		tmCommands.addColumn("OS");
		tmCommands.addColumn("CMD");
		tmCommands.addColumn("Stdin File");
		tmCommands.addColumn("Stdout File");

		for (XCommand command : task.getCommands()) {
			String[] rowData = new String[tmCommands.getColumnCount()];
			rowData[0] = command.getMode();
			rowData[1] = command.getName();
			rowData[2] = command.getType();
			rowData[3] = command.getOs();
			rowData[4] = command.getCmd();
			rowData[5] = command.getStdinFile();
			rowData[6] = command.getStdoutFile();
			tmCommands.addRow(rowData);
		}

		tCommands = new JTable(tmCommands);
		tCommands.setRowSelectionAllowed(true);
		tCommands.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		int[] width = { 60, 60, 60, 60, 300, 80, 80 };
		for (int id = 0; id < width.length; id++) {
			tCommands.getColumnModel().getColumn(id).setPreferredWidth(
					width[id]);
		}

		JPanel pCommandList = new JPanel(makeGapGridLayout());
		pCommandList.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JButton btn;

		btn = new JButton("Add");
		btn.setActionCommand("add-command");
		btn.addActionListener(this);
		pCommandList.add(btn);

		btn = new JButton("Remove");
		btn.setActionCommand("remove-command");
		btn.addActionListener(this);
		pCommandList.add(btn);

		btn = new JButton("Move Up");
		btn.setActionCommand("up-command");
		btn.addActionListener(this);
		pCommandList.add(btn);

		btn = new JButton("Move Down");
		btn.setActionCommand("down-command");
		btn.addActionListener(this);
		pCommandList.add(btn);

		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createLoweredBevelBorder());
		p.add(new JScrollPane(tCommands), BorderLayout.CENTER);
		p.add(pCommandList, BorderLayout.SOUTH);

		return p;
	}

	private JPanel createPropertiesPanel() {
		propsmodel = new DefaultTableModel();
		
		propsmodel.addColumn("Name");
		propsmodel.addColumn("Value");
		int[] psizes = { 100, 300 };

		Map<String, String> props = task.getProperties();

		for (String name : props.keySet()) {
			String value = props.get(name);
			propsmodel.addRow(new String[] { name, value });
		}

		propstable = new JTable(propsmodel);
		propstable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		propstable.setRowSelectionAllowed(true);

		for (int id = 0; id < psizes.length; id++) {
			propstable.getColumnModel().getColumn(id).setPreferredWidth(
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
		p.add(new JScrollPane(propstable), BorderLayout.CENTER);
		p.add(pProperties, BorderLayout.SOUTH);

		return p;
	}

	private GridLayout makeGapGridLayout() {
		GridLayout layout = new GridLayout();
		layout.setHgap(2);
		layout.setVgap(2);
		return layout;
	}

	public void actionPerformed(ActionEvent actionevent) {
		String action = actionevent.getActionCommand();

		if ("confirm".equals(action)) {
			updateTask();
			this.setVisible(false);
			this.dispose();
		} else if ("add-command".equals(action)) {
			tmCommands.addRow(new String[tmCommands.getColumnCount()]);
		} else if ("remove-command".equals(action)) {
			tmCommands.removeRow(tCommands.getSelectedRow());
		} else if ("up-command".equals(action)) {
			ListMoveUp(tCommands, tmCommands);
		} else if ("down-command".equals(action)) {
			ListMoveDown(tCommands, tmCommands);
		} else if ("add-property".equals(action)) {
			propsmodel.addRow(new String[propsmodel.getColumnCount()]);
		} else if ("remove-property".equals(action)) {
			propsmodel.removeRow(propstable.getSelectedRow());
		} else if ("up-property".equals(action)) {
			ListMoveUp(propstable, propsmodel);
		} else if ("down-property".equals(action)) {
			ListMoveDown(propstable, propsmodel);
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

	public void tableChanged(TableModelEvent e) {
	}

	public void updateTask() {
		task.setId(tfId.getText());
		task.setTitle(tfTitle.getText());

		List<XCommand> commands = new ArrayList<XCommand>();
		for (int row = 0; row < tmCommands.getRowCount(); row++) {
			XCommand command = new XCommand();
			command.setMode((String) tmCommands.getValueAt(row, 0));
			command.setName((String) tmCommands.getValueAt(row, 1));
			command.setType((String) tmCommands.getValueAt(row, 2));
			command.setOs((String) tmCommands.getValueAt(row, 3));
			command.setCmd((String) tmCommands.getValueAt(row, 4));
			command.setStdinFile((String) tmCommands.getValueAt(row, 5));
			command.setStdoutFile((String) tmCommands.getValueAt(row, 6));
			commands.add(command);
		}
		task.setCommands(commands);

		Map<String, String> properties = new HashMap<String, String>();
		for (int row = 0; row < propsmodel.getRowCount(); row++) {
			properties.put((String) propsmodel.getValueAt(row, 0),
					(String) propsmodel.getValueAt(row, 1));
		}
		task.setProperties(properties);

		iface.refreshTaskComboBox(true);
		// iface.reloadTask();
	}
}
