package gred.nucleus.dialogs;
import ij.measure.Calibration;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Class to construct graphical interface for the nucleus segmentation and analysis pipeline
 * @author Poulet Axel
 *
 */

public class NucleusSegmentationAndAnalysisDialog extends JFrame{
	private static final long serialVersionUID = 1L;
	private JButton m_jButtonStart = new JButton("Start");
	private JButton m_jButtonQuit = new JButton("Quit");
	private Container m_container;
	private JFormattedTextField m_jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField m_jTextFieldYCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField m_jTextFieldZCalibration =  new JFormattedTextField(Number.class);
	private JFormattedTextField m_jTextFieldMax =  new JFormattedTextField(Number.class);
	private JFormattedTextField m_jTextFieldMin =  new JFormattedTextField(Number.class);
	private JTextField m_jTextFieldUnit =  new JTextField();
	private JLabel m_jLabelXcalibration;
	private JLabel m_jLabelYcalibration;
	private JLabel m_jLabelZcalibration;
	private JLabel m_jLabelUnit;
	private JLabel m_jLabelSegmentation;
	private JLabel m_jLabelVolumeMin;
	private JLabel m_jLabelVolumeMax;
	private JLabel m_jLabelAnalysis;
	private JLabel m_JLabelCalibration;
	private JLabel m_jLabelUnitTexte;
	private ButtonGroup m_buttonGroupChoiceAnalysis = new ButtonGroup();
	private JRadioButton m_jRadioButton2D = new JRadioButton("2D");
	private JRadioButton m_jRadioButton3D = new JRadioButton("3D");
	private JRadioButton m_jRadioButton2D3D = new JRadioButton("2D and 3D");
	private boolean m_start = false;
	
	
	    
	/**
	 * 
	 * Architecture of the graphical windows
	 * 
	 */
	
	public NucleusSegmentationAndAnalysisDialog (Calibration cal){
		this.setTitle("NJ1: Nucleus segmentation & analysis");
		this.setSize(500, 350);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		m_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[] {17, 100, 124, 7};
		gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
		m_container.setLayout (gridBagLayout);
		
		m_JLabelCalibration = new JLabel();
		m_container.add(
			m_JLabelCalibration, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
			)
		);
		m_JLabelCalibration.setText("Voxel Calibration:");
			   	
		m_container.setLayout (gridBagLayout);
		m_jLabelXcalibration = new JLabel();
		m_container.add(
			m_jLabelXcalibration, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0
			)
		);
		m_jLabelXcalibration.setText("x :");
		m_jLabelXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldXCalibration, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldXCalibration.setText(""+cal.pixelWidth);
		m_jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		m_jLabelYcalibration = new JLabel();
		m_container.add(
			m_jLabelYcalibration, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(45, 20, 0, 0), 0, 0
			)
		);
		m_jLabelYcalibration.setText("y :");
		m_jLabelYcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldYCalibration, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(45, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldYCalibration.setText(""+cal.pixelWidth);
		m_jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		m_jLabelZcalibration = new JLabel();
		m_container.add(
			m_jLabelZcalibration, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0
			)
		);
		m_jLabelZcalibration.setText("z :");
		m_jLabelZcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldZCalibration, new GridBagConstraints(
				0, 1,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(70, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldZCalibration.setText(""+cal.pixelDepth);
		m_jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));	 
		
		m_jLabelUnit = new JLabel();
		m_container.add(
			m_jLabelUnit, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(95, 20, 0, 0), 0, 0
			)
		);
		m_jLabelUnit.setText("unit :");
		m_jLabelUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldUnit, new GridBagConstraints(
				0, 1,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(95, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldUnit.setText(cal.getUnit());
		m_jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));	
		
		m_jLabelSegmentation = new JLabel();
		m_container.add(
			m_jLabelSegmentation, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0
			)
		);
		m_jLabelSegmentation.setText("Choose the min and max volumes of the nucleus:");
		
		m_jLabelVolumeMin = new JLabel();
		m_container.add(
			m_jLabelVolumeMin, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0
			)
		);
		m_jLabelVolumeMin.setText("Minimun volume of the segmented nucleus :");
		m_jLabelVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldMin, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 320, 0, 0), 0, 0
			)
		);
		m_jTextFieldMin.setText("15");
		m_jTextFieldMin.setPreferredSize(new java.awt.Dimension( 60, 21));
		
		m_jLabelUnitTexte = new JLabel();
		m_container.add(
			m_jLabelUnitTexte, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 410, 0, 0), 0, 0
			)
		);
		m_jLabelUnitTexte.setText("unit^3");
		m_jLabelUnitTexte.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		m_jLabelVolumeMax = new JLabel();
		m_container.add(
			m_jLabelVolumeMax, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0
			)
		);
		m_jLabelVolumeMax.setText("Maximum volume of the segmented nucleus :");
		m_jLabelVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldMax, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(70, 320, 0, 0), 0, 0
			)
		);
		m_jTextFieldMax.setText("2000");
		m_jTextFieldMax.setPreferredSize(new java.awt.Dimension (60, 21));
		m_jLabelUnitTexte = new JLabel();
		m_container.add(
			m_jLabelUnitTexte, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(70, 410, 0, 0), 0, 0
			)
		);
		m_jLabelUnitTexte.setText("unit^3");
		m_jLabelUnitTexte.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		m_jLabelAnalysis = new JLabel();
		m_container.add(
			m_jLabelAnalysis, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(100, 10, 0, 0), 0, 0
			)
		);
		m_jLabelAnalysis.setText("Type of analysis:");
		
		m_buttonGroupChoiceAnalysis.add(m_jRadioButton2D);
		m_buttonGroupChoiceAnalysis.add(m_jRadioButton3D);
		m_buttonGroupChoiceAnalysis.add(m_jRadioButton2D3D);
		m_jRadioButton2D.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButton3D.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButton2D3D.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jRadioButton2D, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(130, 170, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButton3D, new GridBagConstraints(
				0, 2, 0, 0,0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(130, 120, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButton2D3D, new GridBagConstraints(
				0,2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(130, 10, 0, 0), 0, 0
			)
		);
		m_jRadioButton2D3D.setSelected(true);
		
		m_container.add(
			m_jButtonStart, new GridBagConstraints(
				0, 2, 0, 0,0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(160, 140, 0,0), 0, 0
			)
		);
		m_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		m_container.add(
			m_jButtonQuit, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(160, 10, 0, 0), 0, 0
			)
		);
		m_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		QuitListener quitListener = new QuitListener(this);
		m_jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		m_jButtonStart.addActionListener(startListener);	   
	}
	
	/**
	 * 
	 * @return
	 */
	public double getXCalibration(){
		String xCal = m_jTextFieldXCalibration.getText();
		return Double.parseDouble(xCal.replaceAll(",", "."));
	}
	/**
	 * 
	 * @return
	 */
	public double getYCalibration(){
		String yCal = m_jTextFieldYCalibration.getText();
		return Double.parseDouble(yCal.replaceAll(",", ".")); 
	}
	/**
	 * 
	 * @return
	 */
	public double getZCalibration(){
		String zCal = m_jTextFieldZCalibration.getText();
		return Double.parseDouble(zCal.replaceAll(",", "."));
	}
	/**
	 * 
	 * @return
	 */
	public String getUnit(){
		return m_jTextFieldUnit.getText();
	}
	/**
	 * 
	 * @return
	 */
	public double getMinVolume(){
		return Double.parseDouble(m_jTextFieldMin.getText());
	}
	/**
	 * 
	 * @return
	 */
	public double getMaxVolume(){
		return Double.parseDouble(m_jTextFieldMax.getText());
	}
	/**
	 * 
	 * @return
	 */
	public boolean isStart(){
		return m_start;
	}
	/**
	 * 
	 * @return
	 */
	public boolean is2D3DAnalysis(){	
		return m_jRadioButton2D3D.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean is2D(){
		return m_jRadioButton2D.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean is3D(){
		return m_jRadioButton3D.isSelected();
	}
	
	/********************************************************************************************************************************************
	 * 	Classes listener to interact with the several element of the window
	 */
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************/
	
	/**
	 * 
	 * 
	 *
	 */
	class StartListener implements ActionListener {
		NucleusSegmentationAndAnalysisDialog m_nuc;	
		/**
		 * 
		 * @param nucleusSegmentationAndAnalysisDialog
		 */
		public  StartListener (NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog){
			m_nuc = nucleusSegmentationAndAnalysisDialog;
		}
		
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			m_start=true;
			m_nuc.dispose();
		}
	}
			
	/**
	 * 
	 *
	 */
	class QuitListener implements ActionListener {
		NucleusSegmentationAndAnalysisDialog m_nuc;	
		/**
		 * 
		 * @param nucleusSegmentationAndAnalysisDialog
		 */
		public  QuitListener (NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog){
			m_nuc = nucleusSegmentationAndAnalysisDialog;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			m_nuc.dispose();
		}
	}
}