package gred.nucleus.dialogs;
import ij.measure.Calibration;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Class to construct graphical interface for the nucleus segmentation
 * 
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationDialog  extends JFrame
{

	private static final long serialVersionUID = 1L;
	private JButton _jButtonStart = new JButton("Start");
	private JButton _jButtonQuit = new JButton("Quit");
	private Container _container;
	private JFormattedTextField _jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldYCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldZCalibration =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldMax =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldMin =  new JFormattedTextField(Number.class);
	private JTextField _jTextFieldUnit =  new JTextField();
	private JLabel _jLabelXcalibration;
	private JLabel _jLabelYcalibration;
	private JLabel _jLabelZcalibration;
	private JLabel _jLabelUnit;
	private JLabel _jLabelSegmentation;
	private JLabel _jLabelVolumeMin;
	private JLabel _jLabelVolumeMax;
	private JLabel _JLabelCalibration;
	private JLabel _jLabelUnitTexte;
	private boolean _start = false;

		
		
	/**
	 * Architecture of the graphical windows
	 *
	 */
	
	public NucleusSegmentationDialog (Calibration cal)
	{
		this.setTitle("Nucleus segmentation");
		this.setSize(500, 350);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[] {17, 100, 124, 7};
		gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
		_container.setLayout (gridBagLayout);
		
		_JLabelCalibration = new JLabel();
		_container.add
		(
			_JLabelCalibration,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(0, 10, 0, 0), 0, 0
			)
		);
		_JLabelCalibration.setText("Voxel Calibration:");
		
		_container.setLayout (gridBagLayout);
		_jLabelXcalibration = new JLabel();
		_container.add
		(
			_jLabelXcalibration,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(20, 20, 0, 0), 0, 0
			)
		);
		_jLabelXcalibration.setText("x :");
		_jLabelXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add
		(
			_jTextFieldXCalibration,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(20, 60, 0, 0), 0, 0
			)
		);
		_jTextFieldXCalibration.setText(""+cal.pixelWidth);
		_jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		_jLabelYcalibration = new JLabel();
		_container.add
		(
			_jLabelYcalibration,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(45, 20, 0, 0), 0, 0
			)
		);
		_jLabelYcalibration.setText("y :");
		_jLabelYcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add
		(
			_jTextFieldYCalibration,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(45, 60, 0, 0), 0, 0
			)
		);
		_jTextFieldYCalibration.setText(""+cal.pixelHeight);
		_jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		_jLabelZcalibration = new JLabel();
		_container.add
		(
			_jLabelZcalibration,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(70, 20, 0, 0), 0, 0
			)
		);
		_jLabelZcalibration.setText("z :");
		_jLabelZcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add
		(
			_jTextFieldZCalibration,
			new GridBagConstraints
			(
				0, 1,0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(70, 60, 0, 0), 0, 0
			)
		);
		_jTextFieldZCalibration.setText(""+cal.pixelDepth);
		_jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));	 
		
		_jLabelUnit = new JLabel();
		_container.add
		(
			_jLabelUnit,
			new GridBagConstraints
			(
				0, 1, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(95, 20, 0, 0), 0, 0
			)
		);
		_jLabelUnit.setText("unit :");
		_jLabelUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add
		(
			_jTextFieldUnit,
			new GridBagConstraints
			(
				0, 1,0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(95, 60, 0, 0), 0, 0
			)
		);
		_jTextFieldUnit.setText(cal.getUnit());
		_jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));	
		
		_jLabelSegmentation = new JLabel();
		_container.add
		(
			_jLabelSegmentation,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(20, 10, 0, 0), 0, 0
			)
		);
		_jLabelSegmentation.setText("Choose the min and max volumes of the nucleus:");
		
		_jLabelVolumeMin = new JLabel();
		_container.add
		(
			_jLabelVolumeMin,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(40, 20, 0, 0), 0, 0
			)
		);
		_jLabelVolumeMin.setText("Minimun volume of the segmented nucleus :");
		_jLabelVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add
		(
			_jTextFieldMin,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(40, 320, 0, 0), 0, 0
			)
		);
		_jTextFieldMin.setText("7");
		_jTextFieldMin.setPreferredSize(new java.awt.Dimension( 60, 21));
		
		_jLabelUnitTexte = new JLabel();
		_container.add
		(
			_jLabelUnitTexte,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(40, 410, 0, 0), 0, 0
			)
		);
		_jLabelUnitTexte.setText("unit^3");
		_jLabelUnitTexte.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		_jLabelVolumeMax = new JLabel();
		_container.add
		(
			_jLabelVolumeMax,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(70, 20, 0, 0), 0, 0
			)
		);
		_jLabelVolumeMax.setText("Maximum volume of the segmented nucleus :");
		_jLabelVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add
		(
			_jTextFieldMax,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(70, 320, 0, 0), 0, 0
			)
		);
		_jTextFieldMax.setText("2000");
		_jTextFieldMax.setPreferredSize(new java.awt.Dimension (60, 21));
		_jLabelUnitTexte = new JLabel();
		_container.add
		(
			_jLabelUnitTexte,
			new GridBagConstraints
			(
				0, 2, 0, 0, 0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(70, 410, 0, 0), 0, 0
			)
		);
		_jLabelUnitTexte.setText("unit^3");
		_jLabelUnitTexte.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		_container.add
		(
			_jButtonStart,
			new GridBagConstraints
			(
				0, 2, 0, 0,  0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(160, 140, 0,0), 0, 0
			)
		);
		_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add
		(
			_jButtonQuit,
			new GridBagConstraints
			(
				0, 2, 0, 0,  0.0, 0.0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(160, 10, 0, 0), 0, 0
			)
		);
		_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		QuitListener quitListener = new QuitListener(this);
		_jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		_jButtonStart.addActionListener(startListener);	   
	}
			
	public double getXCalibration()
	{
		String xCal = _jTextFieldXCalibration.getText();
		return Double.parseDouble(xCal.replaceAll(",", "."));
	}
	public double getYCalibration()
	{
		String yCal = _jTextFieldYCalibration.getText();
		return Double.parseDouble(yCal.replaceAll(",", ".")); 
	}
	public double getZCalibration()
	{
		String zCal = _jTextFieldZCalibration.getText();
		return Double.parseDouble(zCal.replaceAll(",", "."));
	}
	public String getUnit(){ return _jTextFieldUnit.getText(); }
	public double getMinVolume(){ return Double.parseDouble(_jTextFieldMin.getText()); }
	public double getMaxVolume(){ return Double.parseDouble(_jTextFieldMax.getText()); }
	public boolean isStart() {	return _start; }
	
	
	/********************************************************************************************************************************************
	 * 	Classes listener to interact with the several element of the window
	 */
	/********************************************************************************************************************************************/
	
	/**
	 * 
	 * 
	 */
	class StartListener implements ActionListener 
	{
		NucleusSegmentationDialog _nucleusSegmentationDialog;	
		/**
		 * 
		 * @param nucleusSegmentationDialog
		 */
		public  StartListener (NucleusSegmentationDialog nucleusSegmentationDialog)
		{
			_nucleusSegmentationDialog = nucleusSegmentationDialog;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			_start=true;
			_nucleusSegmentationDialog.dispose();
		}
	}
	/**
	 * 
	 * 
	 */
	class QuitListener implements ActionListener 
	{
		NucleusSegmentationDialog _nucleusSegmentationDialog;
		public  QuitListener (NucleusSegmentationDialog nucleusSegmentationDialog)
		{
			_nucleusSegmentationDialog = nucleusSegmentationDialog;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			_nucleusSegmentationDialog.dispose();
		}
	}
}