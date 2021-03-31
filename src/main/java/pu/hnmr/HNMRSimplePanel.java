package pu.hnmr;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.groupcontribution.nmr.HNMRShifts;
import ambit2.groupcontribution.nmr.nmr_1h.HShift;
import ambit2.smarts.SmartsHelper;
import ambit2.ui.Panel2D;


public class HNMRSimplePanel extends JPanel 
{

	//GUI components
	JPanel topArea, bottomArea;
	JPanel targetPanel, buttonsPanel, configPanel;
	JTextField smilesField;
	JTextArea resultTextArea;
	Panel2D panel2d;	
	JButton runButton;
	
	JCheckBox checkboxPrintLog;
	JCheckBox checkboxPrintExplanation;
	JCheckBox checkboxMultiplicity;
	

	//Chem data
	String inputSmiles = null;
	IAtomContainer targetMol = null;
	HNMRShifts hnmrShifts = null;
	
	//Config flags
	String defaultConfigFile = "./hnmr-knowledgebase.txt";
	String configFile = "/hnmr-knowledgebase.txt";
	boolean printExplanation = true;
	boolean printLog = false;
	boolean spinSplit = true;
	


	public HNMRSimplePanel() throws Exception
	{
		initGUI();
		initHNMR();

		//Setup initial demo chem data
		try {
			targetMol = SmartsHelper.getMoleculeFromSmiles("CCCC");
			panel2d.setAtomContainer(targetMol);
		} catch (Exception e) {}
		smilesField.setText("CCCC");		
	}


	private void initGUI()
	{	
		this.setLayout(new BorderLayout());
		topArea = new JPanel(new BorderLayout());
		bottomArea = new JPanel(new BorderLayout());
		this.add(bottomArea, BorderLayout.CENTER);
		this.add(topArea, BorderLayout.NORTH);

		//Target panel
		targetPanel = new JPanel(new BorderLayout());
		//this.add(targetPanel, BorderLayout.NORTH);
		topArea.add(targetPanel, BorderLayout.CENTER);
		targetPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		JPanel smilesPanel = new JPanel(new FlowLayout());
		JLabel smilesInputLabel = new JLabel("Target molecule (Smiles/InChI): ");
		smilesField = new JTextField(30);
		smilesPanel.add(smilesInputLabel);
		smilesPanel.add(smilesField);		
		targetPanel.add(smilesPanel, BorderLayout.NORTH);
		
		panel2d = new Panel2D();
		panel2d.setPreferredSize(new Dimension(150,150));
		targetPanel.add(panel2d, BorderLayout.CENTER);


		//Buttons panel (is put within target panel)
		buttonsPanel = new JPanel(new FlowLayout());
		//this.add(buttonsPanel, BorderLayout.SOUTH);
		targetPanel.add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		runButton = new JButton("Run");
		buttonsPanel.add(runButton);
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleRunButton(e);	
			}
		});

		resultTextArea = new JTextArea();
		resultTextArea.setEditable(false);
		resultTextArea.setLineWrap(true);

		JScrollPane scroll = new JScrollPane (resultTextArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scroll.setPreferredSize(new Dimension(10,10));
		bottomArea.add(scroll, BorderLayout.CENTER);
		
		
		
		//Config panel is within rightArea
		//topArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.PAGE_AXIS)); 
		configPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		topArea.add(configPanel, BorderLayout.EAST);
		JLabel labelConfig = new JLabel("Config");
		configPanel.add(labelConfig);
				
		setupCheckBoxes();
		
		Font basicFont = new Font("Ariel", Font.PLAIN, 15);
		changeFont(this, basicFont);
	}
	
	
	void setupCheckBoxes()
	{	
		checkboxPrintLog = 
				new JCheckBox("Print Log");
		checkboxPrintLog.setSelected(false);
		configPanel.add(checkboxPrintLog);

		
		checkboxPrintExplanation = 
				new JCheckBox("Print Explanation");
		checkboxPrintExplanation.setSelected(true);
		configPanel.add(checkboxPrintExplanation);
		
		
		checkboxMultiplicity = 
				new JCheckBox("Calc Multiplicity");
		checkboxMultiplicity.setSelected(true);
		configPanel.add(checkboxMultiplicity);	
		
	}	
		
	
	
	public int initHNMR() 
	{
		/*
		String knowledgeBaseFileName = configFile;
		if (knowledgeBaseFileName == null)
		{	
			knowledgeBaseFileName = defaultConfigFile;
			System.out.println("Using default HNMR database: " + defaultConfigFile);
		}
		*/
		
		String knowledgeBaseFileName = defaultConfigFile;
		
		try {
			hnmrShifts = new HNMRShifts(new  File(knowledgeBaseFileName));
			//hnmrShifts.setFlagSpinSplitting(spinSplit);
			//hnmrShifts.getSpinSplitManager().setFlagReportEquivalenceAtomCodes(true);
		}
		catch (Exception x) {
			System.out.println(x.getMessage());
			resultTextArea.setText("Default knowledge base file is missing: " + x.getMessage());
			return -1;
		}
		
		return 0;
	}



	private void handleRunButton(ActionEvent e)
	{
		inputSmiles = smilesField.getText();
		
		try {
			targetMol = SmartsHelper.getMoleculeFromSmiles(smilesField.getText());
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(targetMol);
			panel2d.setAtomContainer(targetMol);
		} catch (Exception x) {
			System.out.println("Incorrect input smiles: " + x.getMessage());
			resultTextArea.setText("Incorrect input smiles: " + x.getMessage());
			
		}

		//Setup flags:
		printLog = checkboxPrintLog.isSelected();
		printExplanation = checkboxPrintExplanation.isSelected();
		spinSplit = checkboxMultiplicity.isSelected();
		//resultTextArea.setText("test: " + smilesField.getText());
		
		
		int iniRes = initHNMR(); //bug fix hack !!!
		if (iniRes == 0)
			runForInputSmiles();
	}

	public int runForInputSmiles() 
	{
		//System.out.println("Input smiles: " + inputSmiles);
		IAtomContainer mol = null;

		try {
			mol = SmartsHelper.getMoleculeFromSmiles(inputSmiles);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		}
		catch (Exception e) {
			resultTextArea.setText("Error on creating molecule from SMILES:\n" + e.getMessage());
			return -1;
		}

		return predictForMolecule(mol);		
	}
	

	public int predictForMolecule(IAtomContainer mol) 
	{

		try {
			hnmrShifts.setStructure(mol);
			hnmrShifts.calculateHShifts();
		}
		catch(Exception x) {
			//System.out.println("Calculation error:\n" + x.getMessage());
			resultTextArea.setText("Calculation error:\n" + x.getMessage());
			return -1;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("Input smiles: " + inputSmiles);
		sb.append("\n");
		
		if (printLog) {
			sb.append("Log:\n" + hnmrShifts.getCalcLog());
			sb.append("\n");
		}
		
		
		for (HShift hs : hnmrShifts.getHShifts()) {
			//sb.append(hs.toString(printExplanation));
			sb.append(hs.toString());
			sb.append("\n");
		}	
		
		resultTextArea.setText(sb.toString());
		
		return 0;
	}
	
	public static void changeFont ( Component component, Font font )
	{
	    component.setFont ( font );
	    if ( component instanceof Container )
	    {
	        for ( Component child : ( ( Container ) component ).getComponents () )
	        {
	            changeFont ( child, font );
	        }
	    }
	}

}
