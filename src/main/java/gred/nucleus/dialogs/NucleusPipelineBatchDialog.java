package gred.nucleus.dialogs;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


/**
 * 
 * @author lom
 *
 */

public class NucleusPipelineBatchDialog extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton _JBWorkDirectory = new JButton("Output Directory"), _JBstart = new JButton("Start"), _JBquit = new JButton("Quit"), _JBRawData = new JButton("Raw Data");
	private Container _container;

	private JFormattedTextField _jTFX = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTFY = new JFormattedTextField(Number.class);
    private JFormattedTextField _jTFZ =  new JFormattedTextField(Number.class);
    private JFormattedTextField _jTFMax =  new JFormattedTextField(Number.class);
    private JFormattedTextField _jTFMin =  new JFormattedTextField(Number.class);
    private JTextField _jTFUnit =  new JTextField(), _jTFwd  =  new JTextField(), _jTFrd = new JTextField();
	private JLabel _jLXcalibration, _jLYcalibration, _jLZcalibration, _jLUnit,
		_jLSegmentation, _jLVolumeMin, _jLVolumeMax, _jLAnalysis, _JLWorkDirect, _JLCalibration;
	private ButtonGroup bgChoiceAnalysis = new ButtonGroup();
	private JRadioButton _JRB2d = new JRadioButton("2D");
    private JRadioButton _JRB3d = new JRadioButton("3D");
    private JRadioButton _JRB2d3d = new JRadioButton("2D and 3D");
	private String _workDir, _dataDir;
	private boolean _start = false;
	
	public static void main(String[] args)  
    {
		NucleusPipelineBatchDialog fenetre = new NucleusPipelineBatchDialog();
    	fenetre.setLocationRelativeTo(null);
    }
	
    
    /**
     * 
     *
     */
    
	public NucleusPipelineBatchDialog ()
	{
		this.setTitle("Plop");
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 71, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
	   	_container.setLayout (gridBagLayout);
	   	
	   	_JLWorkDirect = new JLabel();
	   	_container.add(_JLWorkDirect, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
	   	_JLWorkDirect.setText("Work directory and Raw data choice : ");
	   
	   	_container.add(_JBRawData, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(30, 10, 0, 0), 0, 0));
	   	_JBRawData.setPreferredSize(new java.awt.Dimension(120, 21));
	   	_JBRawData.setFont(new java.awt.Font("Albertus",2,10));
	   	
		_container.add(_jTFrd, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(30, 160, 0, 0), 0, 0));
		_jTFrd.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTFrd.setFont(new java.awt.Font("Albertus",2,10));
	   	
	   	_container.add(_JBWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(60, 10, 0, 0), 0, 0));
	   	_JBWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
	   	_JBWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
	   	
		_container.add(_jTFwd, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(60, 160, 0, 0), 0, 0));
	   	_jTFwd.setPreferredSize(new java.awt.Dimension(280, 21));
	   	_jTFwd.setFont(new java.awt.Font("Albertus",2,10));
	   	
	   	_JLCalibration = new JLabel();
	   	_container.add(_JLCalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
	   	_JLCalibration.setText("Voxel Calibration:");
	   	
	   	_container.setLayout (gridBagLayout);
		_jLXcalibration = new JLabel();
		_container.add(_jLXcalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0));
		_jLXcalibration.setText("x :");
		_jLXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFX, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(40, 60, 0, 0), 0, 0));
		_jTFX.setText("1");
		_jTFX.setPreferredSize(new java.awt.Dimension(60, 21));
	
		_jLYcalibration = new JLabel();
		_container.add(_jLYcalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(65, 20, 0, 0), 0, 0));
		_jLYcalibration.setText("y :");
		_jLYcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFY, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(65, 60, 0, 0), 0, 0));
		_jTFY.setText("1");
		_jTFY.setPreferredSize(new java.awt.Dimension(60, 21));
	
		_jLZcalibration = new JLabel();
		_container.add(_jLZcalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(90, 20, 0, 0), 0, 0));
		_jLZcalibration.setText("z :");
		_jLZcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFZ, new GridBagConstraints(0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(90, 60, 0, 0), 0, 0));
		_jTFZ.setText("1");
		_jTFZ.setPreferredSize(new java.awt.Dimension(60, 21));	 
		
		_jLUnit = new JLabel();
		_container.add(_jLUnit, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(115, 20, 0, 0), 0, 0));
		_jLUnit.setText("unit :");
		_jLUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFUnit, new GridBagConstraints(0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(115, 60, 0, 0), 0, 0));
		_jTFUnit.setText("pixel");
		_jTFUnit.setPreferredSize(new java.awt.Dimension(60, 21));	

		
		
		_jLSegmentation = new JLabel();
	   	_container.add(_jLSegmentation, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
	   	_jLSegmentation.setText("Choose the min and max volumes of the nucleus:");
	   	
	   	_jLVolumeMin = new JLabel();
	   	_container.add(_jLVolumeMin, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0));
	   	_jLVolumeMin.setText("Minimun volume of the segmented nucleus :");
	   	_jLVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFMin, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(40, 320, 0, 0), 0, 0));
		_jTFMin.setText("15");
		_jTFMin.setPreferredSize(new java.awt.Dimension( 60, 21));

		_jLVolumeMax = new JLabel();
	   	_container.add(_jLVolumeMax, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0));
	   	_jLVolumeMax.setText("Maximum volume of the segmented nucleus :");
	   	_jLVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFMax, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(70, 320, 0, 0), 0, 0));
		_jTFMax.setText("2000");
		_jTFMax.setPreferredSize(new java.awt.Dimension (60, 21));
		
		
		_jLAnalysis = new JLabel();
	   	_container.add(_jLAnalysis, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(100, 10, 0, 0), 0, 0));
	   	_jLAnalysis.setText("Type of analysis:");
	   
	   	bgChoiceAnalysis.add(_JRB2d);
		bgChoiceAnalysis.add(_JRB3d);
		bgChoiceAnalysis.add(_JRB2d3d);
		_JRB2d.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_JRB3d.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_JRB2d3d.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_JRB2d, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 170, 0, 0), 0, 0));
		_container.add(_JRB3d, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 120, 0, 0), 0, 0));
		_container.add(_JRB2d3d, new GridBagConstraints(0,3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 10, 0, 0), 0, 0));
		_JRB2d3d.setSelected(true);
		
	   	_container.add(_JBstart, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(190, 140, 0,0), 0, 0));
		_JBstart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add(_JBquit, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(190, 10, 0, 0), 0, 0));
		_JBquit.setPreferredSize(new java.awt.Dimension(120, 21));
	  	this.setVisible(true);
	   	
	  	WorkDirListener wdListener = new WorkDirListener();
	  	_JBWorkDirectory.addActionListener(wdListener);
	  	DataDirListener ddListener = new DataDirListener();
	  	_JBRawData.addActionListener(ddListener);
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
	public String getWorkDirectory(){return _jTFwd.getText();}
	public String getDirRawData(){return _jTFrd.getText();}
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
	
		NucleusPipelineBatchDialog _jfpfso;	
		public  StartListener (NucleusPipelineBatchDialog jfpfso) {_jfpfso = jfpfso;}
		/**
		  * 
		  */
		 public void actionPerformed(ActionEvent actionEvent)
		 {
			 if (_jTFwd.getText().isEmpty() || _jTFrd.getText().isEmpty())
				 JOptionPane.showMessageDialog(null, "You did not choose a work directory or the raw data", "Error", JOptionPane.ERROR_MESSAGE); 
			 else
			 {
				 _start=true;
				 _jfpfso.dispose();
			 }
		 }
	 }
	
	
	/**
	 * 
	 * @author lom
	 *
	 */
	
	class QuitListener implements ActionListener 
	{
		NucleusPipelineBatchDialog _jfpfso;	
		public  QuitListener (NucleusPipelineBatchDialog jfpfso) {_jfpfso = jfpfso;}
		public void actionPerformed(ActionEvent actionEvent) { _jfpfso.dispose(); }
	}
	
	
	/*	Bouton Reference et dossier ava		*/
	
	/**
	 * 
	 * @author lom
	 *
	 */
	 class WorkDirListener implements ActionListener
	 {
		 /**
		  * 
		  */		 
		 public void actionPerformed(ActionEvent actionEvent)
		 {
			 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			 JFileChooser jFileChooser = new JFileChooser();
			 jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			 int returnVal = jFileChooser.showOpenDialog(getParent());
			 if(returnVal == JFileChooser.APPROVE_OPTION)
			 {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				 _workDir = jFileChooser.getSelectedFile().getAbsolutePath();
				 _jTFwd.setText(_workDir);
			 }
			 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
	 
	 /**
		 * 
		 * @author lom
		 *
		 */
		 class DataDirListener implements ActionListener
		 {
			 /**
			  * 
			  */		 
			 public void actionPerformed(ActionEvent actionEvent)
			 {
				 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				 JFileChooser jFileChooser = new JFileChooser();
				 jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 int returnVal = jFileChooser.showOpenDialog(getParent());
				 if(returnVal == JFileChooser.APPROVE_OPTION)
				 {
					 @SuppressWarnings("unused")
					String run = jFileChooser.getSelectedFile().getName();
					_dataDir = jFileChooser.getSelectedFile().getAbsolutePath();
					 _jTFrd.setText(_dataDir);
				 }
				 setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			 }	
		 }
}