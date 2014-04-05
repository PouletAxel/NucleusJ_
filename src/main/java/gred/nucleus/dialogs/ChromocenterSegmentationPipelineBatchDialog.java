package gred.nucleus.dialogs;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;	
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * 
 * @author Poulet Axel
 *
 */
public class ChromocenterSegmentationPipelineBatchDialog extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JButton _jButtonWorkDirectory = new JButton("Output Directory"), _jButtonStart = new JButton("Start"), _jButtonQuit = new JButton("Quit")
	, _JButtonRawData = new JButton("Raw Data");
	private Container _container;
	private JFormattedTextField _jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldYCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldZCalibration =  new JFormattedTextField(Number.class);
	private JTextField _jTextFieldUnit =  new JTextField(), _jTextFieldWorkDirectory  =  new JTextField(), _jTextFieldRawData = new JTextField();
	private JLabel _jLabelXcalibration, _jLabelYcalibration, _jLabelZcalibration, _jLabelUnit, _jLabelWorkDirectory, _jLabelCalibration;
	private String _workDirectory, _rawDataDirectory;
	private boolean _start = false;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)  
	{
		ChromocenterSegmentationPipelineBatchDialog chromocenterSegmentationPipelineBatchDialog = new ChromocenterSegmentationPipelineBatchDialog();
		chromocenterSegmentationPipelineBatchDialog.setLocationRelativeTo(null);
	}
		
	    
	/**
	 * 
	 *
	 */
	    
	public ChromocenterSegmentationPipelineBatchDialog ()
	{
		this.setTitle("Chromocenters segmentation pipeline (Batch)");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 200, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {236, 120, 72, 20};
		_container.setLayout (gridBagLayout);
		
		_jLabelWorkDirectory = new JLabel();
		_container.add(_jLabelWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		_jLabelWorkDirectory.setText("Work directory and Raw data choice : ");
		
		JTextPane jTextPane = new JTextPane();
	   	jTextPane.setText("The Raw Data directory must contain 2 subdirectories:"
	   			+ "\n1.for raw nuclei images, named RawDataNucleus. "
	   			+ "\n2.for segmented nuclei images, named SegmentedDataNucleus."
	   			+ "\nPlease keep the same file name during the image processing.");
	   	jTextPane.setEditable(false);
	   	_container.add(jTextPane, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		
		_container.add(_JButtonRawData, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(100, 10, 0, 0), 0, 0));
		_JButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		_JButtonRawData.setFont(new java.awt.Font("Albertus",2,10));
		
		_container.add(_jTextFieldRawData, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(100, 160, 0, 0), 0, 0));
		_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldRawData.setFont(new java.awt.Font("Albertus",2,10));
		
		_container.add(_jButtonWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(140, 10, 0, 0), 0, 0));
		_jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		_jButtonWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
		
		_container.add(_jTextFieldWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(140, 160, 0, 0), 0, 0));
		_jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
		
		_jLabelCalibration = new JLabel();
		_container.add(_jLabelCalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		_jLabelCalibration.setText("Voxel Calibration:");
		
		_container.setLayout (gridBagLayout);
		_jLabelXcalibration = new JLabel();
		_container.add(_jLabelXcalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0));
		_jLabelXcalibration.setText("x :");
		_jLabelXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTextFieldXCalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(40, 60, 0, 0), 0, 0));
		_jTextFieldXCalibration.setText("1");
		_jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
			
		_jLabelYcalibration = new JLabel();
		_container.add(_jLabelYcalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(65, 20, 0, 0), 0, 0));
		_jLabelYcalibration.setText("y :");
		_jLabelYcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTextFieldYCalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(65, 60, 0, 0), 0, 0));
		_jTextFieldYCalibration.setText("1");
		_jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
			
		_jLabelZcalibration = new JLabel();
		_container.add(_jLabelZcalibration, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(90, 20, 0, 0), 0, 0));
		_jLabelZcalibration.setText("z :");
		_jLabelZcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTextFieldZCalibration, new GridBagConstraints(0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(90, 60, 0, 0), 0, 0));
		_jTextFieldZCalibration.setText("1");
		_jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));	 
			
		_jLabelUnit = new JLabel();
		_container.add(_jLabelUnit, new GridBagConstraints(0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(115, 20, 0, 0), 0, 0));
		_jLabelUnit.setText("unit :");
		_jLabelUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTextFieldUnit, new GridBagConstraints(0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(115, 60, 0, 0), 0, 0));
		_jTextFieldUnit.setText("pixel");
		_jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));	
			
		
		_container.add(_jButtonStart, new GridBagConstraints(0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(190, 140, 0,0), 0, 0));
		_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add(_jButtonQuit, new GridBagConstraints(0, 2, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(190, 10, 0, 0), 0, 0));
		_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		WorkDirectoryListener wdListener = new WorkDirectoryListener();
		_jButtonWorkDirectory.addActionListener(wdListener);
		RawDataDirectoryListener ddListener = new RawDataDirectoryListener();
		_JButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		_jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		_jButtonStart.addActionListener(startListener);	   
	}
		
	public double getXCalibration(){ return Double.parseDouble(_jTextFieldXCalibration.getText()); }
	public double getYCalibration(){ return Double.parseDouble(_jTextFieldYCalibration.getText()); }
	public double getZCalibration(){ return Double.parseDouble(_jTextFieldZCalibration.getText()); }
	public String getUnit(){ return _jTextFieldUnit.getText(); }
	public String getWorkDirectory(){return _jTextFieldWorkDirectory.getText();}
	public String getRawDataDirectory(){return _jTextFieldRawData.getText();}
	public boolean isStart() {	return _start; };

	/********************************************************************************************************************************************
	 * 	Classes listener to interact with the several element of the window
	 */
	/********************************************************************************************************************************************
 	/********************************************************************************************************************************************
	/********************************************************************************************************************************************
	/********************************************************************************************************************************************/
	
	
	class StartListener implements ActionListener 
	{
		
		ChromocenterSegmentationPipelineBatchDialog _chromocenterSegmentationPipelineBatchDialog;	
		public  StartListener (ChromocenterSegmentationPipelineBatchDialog chromocenterSegmentationPipelineBatchDialog)
		{
			_chromocenterSegmentationPipelineBatchDialog = chromocenterSegmentationPipelineBatchDialog;
		}
		
		public void actionPerformed(ActionEvent actionEvent)
		{
			if (_jTextFieldWorkDirectory.getText().isEmpty() || _jTextFieldRawData.getText().isEmpty())
				JOptionPane.showMessageDialog(null, "You did not choose a work directory or the raw data", "Error", JOptionPane.ERROR_MESSAGE); 
			else
			{
				_start=true;
				_chromocenterSegmentationPipelineBatchDialog.dispose();
			}
		}
	}
		
	
	class QuitListener implements ActionListener 
	{
		ChromocenterSegmentationPipelineBatchDialog _chromocenterSegmentationPipelineBatchDialog;
		public  QuitListener (ChromocenterSegmentationPipelineBatchDialog chromocenterSegmentationPipelineBatchDialog) {_chromocenterSegmentationPipelineBatchDialog = chromocenterSegmentationPipelineBatchDialog;}
		public void actionPerformed(ActionEvent actionEvent) { _chromocenterSegmentationPipelineBatchDialog.dispose(); }
	}
		
		
	class WorkDirectoryListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				_workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldWorkDirectory.setText(_workDirectory);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
	
	
	class RawDataDirectoryListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				_rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldRawData.setText(_rawDataDirectory);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
}	