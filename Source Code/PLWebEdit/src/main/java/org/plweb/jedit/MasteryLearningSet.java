package org.plweb.jedit;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.plweb.suite.common.xml.XProject;
import org.plweb.suite.common.xml.XTask;

public class MasteryLearningSet {

	/**
	 * @param args
	 *            Date:2014/8/11 Shao-Yang, Lin
	 */
	private ArrayList<JTextField> textList = new ArrayList<JTextField>();
	private static MasteryLearningSet instance = null;
	private JFrame frame = null;
	private String masterySet = null;
	JPanel groupPanel;
	
	public static MasteryLearningSet getInstance(){
		if(instance == null)
			return instance = new MasteryLearningSet();
		else
			return instance;
	}
	
	public MasteryLearningSet(){
	 
	}
	
	public void displayPanel(){
		/* Frame set */
		if(frame != null)
			frame.setVisible(true);
		else {
			frame = new JFrame("Mastery Learning");
			frame.setSize(580, 400);
			//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);
			
			/* Panel set */
			JPanel leftPanel = new JPanel();
			groupPanel = new JPanel();
			JPanel listPanel = new JPanel();
			JPanel btnPanel = new JPanel();
			
			
			JScrollPane scrollPane = new JScrollPane(groupPanel);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setPreferredSize(new Dimension(380, 320));

			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));

			leftPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

			scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 0, 10), BorderFactory.createTitledBorder("Group Set")));
			listPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("List")));
			btnPanel.setBorder(new EmptyBorder(0, 10, 20, 7));
			leftPanel.add(scrollPane);
			leftPanel.add(btnPanel);
			
			/* JTextField set */
			final DefaultListModel model = new DefaultListModel();
			JList list = new JList(model);
			list.setFont(new Font("Verdana", Font.BOLD, 20));
			JScrollPane _listPanel = new JScrollPane(list);
			_listPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			_listPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			_listPanel.setPreferredSize(new Dimension(140, 310));
			listPanel.add(_listPanel);

			int c = 1;
			for (XTask xtask : ProjectEnvironment.getInstance().getActiveProject().getTasks()) {
				model.addElement(String.valueOf(c).concat(" - ").concat(xtask.getProperty("ExName")));
				c++;
			}
				
				
			list.setDragEnabled(true);
			list.setTransferHandler(new TextFieldHanlder());

			/* JButton set */
			JButton newGroup = new JButton("New Group");
			JButton finish = new JButton("Finish");

			btnPanel.add(newGroup);
			btnPanel.add(Box.createRigidArea(new Dimension(5, 0)));
			btnPanel.add(finish);

			/* button actionListener set */
			newGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					groupPanel.add(newGroupPnl(textList.size() + 1));
					groupPanel.revalidate();
				}
			});
			finish.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
	
					JSONObject obj = new JSONObject();
	//				Map<Integer, String> map = new HashMap<Integer, String>();
							
					if(textList.size() != 0) {
						for(int i = 0; i < textList.size(); i++){
							if(!textList.get(i).getText().equals(""))
								obj.put(i + 1, textList.get(i).getText());
						}
						//obj.put("isPass", true);
								
						if(!obj.isEmpty()) {
							// set instance (masterySet)
							//masterySet = obj.toString();
							setMasterySet(obj.toString());
							
							XProject project = ProjectEnvironment.getInstance().getActiveProject();
							project.setProperty("hasMastery", "true");
						} else {
							XProject project = ProjectEnvironment.getInstance().getActiveProject();
							project.setProperty("hasMastery", "false");
						}
								
					} else {
						//System.out.println("NULL textField.");
					}						
					frame.setVisible(false);
				}
			});

			/* JSplitPane set */
			JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, listPanel);
			splitPanel.setOneTouchExpandable(false);
			splitPanel.setDividerLocation(380);
			frame.add(splitPanel);
			frame.setVisible(true);
			setTextField();
		}
	}
	private void setTextField(){
		if(!masterySet.equals(null)){
		
			try{
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(masterySet);
				
				for(int i = 0; i < obj.size(); i++) {
					groupPanel.add(newGroupPnl(i + 1));
					textList.get(i).setText(obj.get(String.valueOf(i+1)).toString());
				}
				
			} catch(ParseException e){
			}
			
		} else {
			
		}
	}
	 
	private JPanel newGroupPnl(int groupId) {

		JPanel _return = new JPanel();
		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(200, 30));

		/* Object static instance. */
		textList.add(textField);
		
		textField.setDragEnabled(true);
		textField.setTransferHandler(new TextFieldHanlder());
		
		textField.setFont(new Font("Verdana", Font.BOLD, 20));
		
		_return.add(textField);
		_return.setMaximumSize(new Dimension(300, 80));
		_return.setBorder(new CompoundBorder(new EmptyBorder(0, 5, 10, 0),
				BorderFactory.createTitledBorder("Group "
						+ String.valueOf(groupId))));
		return _return;
	}
	
	public void setMasterySet(String masterySet){
		this.masterySet = masterySet;
	}
	
	public String getMasterySet() {
		return this.masterySet;
	}
	 
}