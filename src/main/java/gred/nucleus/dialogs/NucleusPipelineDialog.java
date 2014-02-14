package gred.nucleus.dialogs;

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
 * 
 * @author lom
 *
 */

public class NucleusPipelineDialog extends JFrame
{

	private static final long serialVersionUID = 1L;
	private JButton _JBstart = new JButton("Start"), _JBquit = new JButton("Quit");
	private Container _container;
	private JFormattedTextField _jTFX = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTFY = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTFZ =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTFMax =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTFMin =  new JFormattedTextField(Number.class);
	private JTextField _jTFUnit =  new JTextField();
	private JLabel _jLXcalibration, _jLYcalibration, _jLZcalibration, _jLUnit,
	_jLSegmentation, _jLVolumeMin, _jLVolumeMax, _jLAnalysis, _JLCalibration,_JlUnitMin;
	private ButtonGroup bgChoiceAnalysis = new ButtonGroup();
	private JRadioButton _JRB2d = new JRadioButton("2D");
	private JRadioButton _JRB3d = new JRadioButton("3D");
	private JRadioButton _JRB2d3d = new JRadioButton("2D and 3D");
	private boolean _start = false;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)  
	{
		NucleusPipelineDialog fenetre = new NucleusPipelineDialog();
		fenetre.setLocationRelativeTo(null);
	}
	
	    
	/**
	 * 
	 *
	 */
	
	public NucleusPipelineDialog ()
	{
		this.setTitle("Plopi");
		this.setSize(500, 350);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[] {17, 100, 124, 7};
		gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
		_container.setLayout (gridBagLayout);
		
		_JLCalibration = new JLabel();
		_container.add(_JLCalibration, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		_JLCalibration.setText("Voxel Calibration:");
			   	
		_container.setLayout (gridBagLayout);
		_jLXcalibration = new JLabel();
		_container.add(_jLXcalibration, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		_jLXcalibration.setText("x :");
		_jLXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFX, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 60, 0, 0), 0, 0));
		_jTFX.setText("1");
		_jTFX.setPreferredSize(new java.awt.Dimension(60, 21));
		
		_jLYcalibration = new JLabel();
		_container.add(_jLYcalibration, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(45, 20, 0, 0), 0, 0));
		_jLYcalibration.setText("y :");
		_jLYcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFY, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(45, 60, 0, 0), 0, 0));
		_jTFY.setText("1");
		_jTFY.setPreferredSize(new java.awt.Dimension(60, 21));
		
		_jLZcalibration = new JLabel();
		_container.add(_jLZcalibration, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0));
		_jLZcalibration.setText("z :");
		_jLZcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFZ, new GridBagConstraints(0, 1,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(70, 60, 0, 0), 0, 0));
		_jTFZ.setText("1");
		_jTFZ.setPreferredSize(new java.awt.Dimension(60, 21));	 
		
		_jLUnit = new JLabel();
		_container.add(_jLUnit, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(95, 20, 0, 0), 0, 0));
		_jLUnit.setText("unit :");
		_jLUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFUnit, new GridBagConstraints(0, 1,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(95, 60, 0, 0), 0, 0));
		_jTFUnit.setText("pixel");
		_jTFUnit.setPreferredSize(new java.awt.Dimension(60, 21));	
		
		
		
		_jLSegmentation = new JLabel();
		_container.add(_jLSegmentation, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		_jLSegmentation.setText("Choose the min and max volumes of the nucleus:");
		
		_jLVolumeMin = new JLabel();
		_container.add(_jLVolumeMin, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0));
		_jLVolumeMin.setText("Minimun volume of the segmented nucleus :");
		_jLVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFMin, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(40, 320, 0, 0), 0, 0));
		_jTFMin.setText("15");
		_jTFMin.setPreferredSize(new java.awt.Dimension( 60, 21));
		
		_JlUnitMin = new JLabel();
		_container.add(_JlUnitMin, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(40, 410, 0, 0), 0, 0));
		_JlUnitMin.setText("unit^3");
		_JlUnitMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		_jLVolumeMax = new JLabel();
		_container.add(_jLVolumeMax, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0));
		_jLVolumeMax.setText("Maximum volume of the segmented nucleus :");
		_jLVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFMax, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(70, 320, 0, 0), 0, 0));
		_jTFMax.setText("2000");
		_jTFMax.setPreferredSize(new java.awt.Dimension (60, 21));
		_JlUnitMin = new JLabel();
		_container.add(_JlUnitMin, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(70, 410, 0, 0), 0, 0));
		_JlUnitMin.setText("unit^3");
		_JlUnitMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		_jLAnalysis = new JLabel();
		_container.add(_jLAnalysis, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(100, 10, 0, 0), 0, 0));
		_jLAnalysis.setText("Type of analysis:");
		
		bgChoiceAnalysis.add(_JRB2d);
		bgChoiceAnalysis.add(_JRB3d);
		bgChoiceAnalysis.add(_JRB2d3d);
		_JRB2d.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_JRB3d.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_JRB2d3d.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_JRB2d, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 170, 0, 0), 0, 0));
		_container.add(_JRB3d, new GridBagConstraints(0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 120, 0, 0), 0, 0));
		_container.add(_JRB2d3d, new GridBagConstraints(0,2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 10, 0, 0), 0, 0));
		_JRB2d3d.setSelected(true);
		
		_container.add(_JBstart, new GridBagConstraints(0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(160, 140, 0,0), 0, 0));
		_JBstart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add(_JBquit, new GridBagConstraints(0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(160, 10, 0, 0), 0, 0));
		_JBquit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		QuitListener quitListener = new QuitListener(this);
		_JBquit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		_JBstart.addActionListener(startListener);	   
	}
	
	public double getx(){ return Double.parseDouble(_jTFX.getText()); }
	public double gety(){ return Double.parseDouble(_jTFY.getText()); }
	public double getz(){ return Double.parseDouble(_jTFZ.getText()); }
	public String getUnit(){ return _jTFUnit.getText(); }
	public double getMinSeg(){ return Double.parseDouble(_jTFMin.getText()); }
	public double getMaxSeg(){ return Double.parseDouble(_jTFMax.getText()); }
	public boolean isStart() {	return _start; }
	public boolean isTheBoth() {	return _JRB2d3d.isSelected(); }
	public boolean is2D() {	return _JRB2d.isSelected(); }
	public boolean is3D() {	return _JRB3d.isSelected(); }
	
	/********************************************************************************************************************************************
	 * 	Classe listener pour interagir avec les composants de la fenetre
	 */
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************
			
	/**
	 * 
	 * @author lom
	 *
	 */
			 
	class StartListener implements ActionListener 
	{
		
		NucleusPipelineDialog _jfc;	
		public  StartListener (NucleusPipelineDialog jfc) {_jfc = jfc;}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			_start=true;
			_jfc.dispose();
		}
	}
			
	/**
	 * 
	 * @author lom
	 *
	 */
	class QuitListener implements ActionListener 
	{
		NucleusPipelineDialog _jfc;	
		public  QuitListener (NucleusPipelineDialog jfc) {_jfc = jfc;}
		public void actionPerformed(ActionEvent actionEvent) { _jfc.dispose(); }
	}
}