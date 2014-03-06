package gred.nucleus.dialogs;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * 
 * @author pouletaxel
 *
 */
public class NucleusSegmentationBatchDialog extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JButton _jButtonWorkDirectory = new JButton("Output Directory"), _jButtonStart = new JButton("Start"), _jButtonQuit = new JButton("Quit"), _jButtonRawData = new JButton("Raw Data");
	private Container _container;
	private JComboBox _comboBoxCpu = new JComboBox();
	private JFormattedTextField _jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldYCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldZCalibration =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldMax =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTextFieldMin =  new JFormattedTextField(Number.class);
	private JTextField _jTextFieldUnit =  new JTextField(), _jTextFieldWorkDirectory  =  new JTextField(), _jTextFieldRawData = new JTextField();
	private JLabel _jLabelXcalibration, _jLabelYcalibration, _jLabelZcalibration, _jLabelUnit,_jLabelSegmentation,
	_jLabelVolumeMin, _jLabelVolumeMax, _jLabelWorkDirectory, _jLabelCalibration, _jLabelNbCpu;
	private String _workDirectory, _rawDataDirectory;
	private boolean _start = false;
	private int _nbCpuChosen = 1;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)  
	{
		NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog = new NucleusSegmentationBatchDialog();
		nucleusSegmentationBatchDialog.setLocationRelativeTo(null);
	}
		
	    
	/*
	 * 
	 *
	 */
	    
	public NucleusSegmentationBatchDialog ()
	{
		this.setTitle("Nucleus segmentation (batch)");
		this.setSize(500, 450);
		this.setLocationRelativeTo(null);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[] {17, 71, 124, 7};
		gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
		_container.setLayout (gridBagLayout);
		   	
		_jLabelWorkDirectory = new JLabel();
		_container.add(_jLabelWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		_jLabelWorkDirectory.setText("Work directory and Raw data choice : ");
		   
		_container.add(_jButtonRawData, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(30, 10, 0, 0), 0, 0));
		_jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		_jButtonRawData.setFont(new java.awt.Font("Albertus",2,10));
		
		_container.add(_jTextFieldRawData, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(30, 160, 0, 0), 0, 0));
		_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		_jTextFieldRawData.setFont(new java.awt.Font("Albertus",2,10));
		
		_container.add(_jButtonWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(60, 10, 0, 0), 0, 0));
		_jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		_jButtonWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
		
		_container.add(_jTextFieldWorkDirectory, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(60, 160, 0, 0), 0, 0));
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
		
		_jLabelSegmentation = new JLabel();
		_container.add(_jLabelSegmentation, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		_jLabelSegmentation.setText("Choose the min and max volumes of the nucleus:");
		
		_jLabelVolumeMin = new JLabel();
		_container.add(_jLabelVolumeMin, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0));
		_jLabelVolumeMin.setText("Minimun volume of the segmented nucleus :");
		_jLabelVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTextFieldMin, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(40, 320, 0, 0), 0, 0));
		_jTextFieldMin.setText("15");
		_jTextFieldMin.setPreferredSize(new java.awt.Dimension( 60, 21));

		_jLabelVolumeMax = new JLabel();
		_container.add(_jLabelVolumeMax, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0));
		_jLabelVolumeMax.setText("Maximum volume of the segmented nucleus :");
		_jLabelVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTextFieldMax, new GridBagConstraints(0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(70, 320, 0, 0), 0, 0));
		_jTextFieldMax.setText("2000");
		_jTextFieldMax.setPreferredSize(new java.awt.Dimension (60, 21));

		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		int nbProc = bean.getAvailableProcessors();
		for (int i = 1; i <= nbProc; ++i) _comboBoxCpu.addItem(i);
		_jLabelNbCpu= new JLabel();
		_jLabelNbCpu.setText("How many CPU :");
		_container.add(_jLabelNbCpu, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(95, 10, 0,0), 0, 0));
		_container.add(_comboBoxCpu, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(90, 200, 0,0), 0, 0));
		_comboBoxCpu.addItemListener(new ItemState());
		
		_container.add(_jButtonStart, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 140, 0,0), 0, 0));
		_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add(_jButtonQuit, new GridBagConstraints(0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(130, 10, 0, 0), 0, 0));
		_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		WorkDirListener wdListener = new WorkDirListener();
		_jButtonWorkDirectory.addActionListener(wdListener);
		DataDirListener ddListener = new DataDirListener();
		_jButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		_jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		_jButtonStart.addActionListener(startListener);	   
	}
	
	public void setNbCpu(int nb){ _nbCpuChosen = nb; }
	public int getNbCpu(){ return _nbCpuChosen; }
	public double getXCalibration(){ return Double.parseDouble(_jTextFieldXCalibration.getText()); }
	public double getYCalibration(){ return Double.parseDouble(_jTextFieldYCalibration.getText()); }
	public double getZCalibration(){ return Double.parseDouble(_jTextFieldZCalibration.getText()); }
	public String getUnit(){ return _jTextFieldUnit.getText(); }
	public double getMinVolume(){ return Double.parseDouble(_jTextFieldMin.getText()); }
	public double getMaxVolume(){ return Double.parseDouble(_jTextFieldMax.getText()); }
	public String getWorkDirectory(){return _jTextFieldWorkDirectory.getText();}
	public String getRawDataDirectory(){return _jTextFieldRawData.getText();}
	public boolean isStart() {	return _start; }
	

	/********************************************************************************************************************************************
	 * 	Classes listener to interact with the several element of the window
	 */
	/********************************************************************************************************************************************
 	/********************************************************************************************************************************************
  	/********************************************************************************************************************************************
 	/********************************************************************************************************************************************/
		
	class ItemState implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			setNbCpu((Integer) e.getItem());
		}	               
	}	
		
	class StartListener implements ActionListener 
	{
		NucleusSegmentationBatchDialog _nucleusSegmentationBatchDialog;	
		public  StartListener (NucleusSegmentationBatchDialog nucleusSegmeentationBatchDialog)
		{
			_nucleusSegmentationBatchDialog = nucleusSegmeentationBatchDialog;
		}
			
		public void actionPerformed(ActionEvent actionEvent)
		{
			if (_jTextFieldWorkDirectory.getText().isEmpty() || _jTextFieldRawData.getText().isEmpty())
				JOptionPane.showMessageDialog(null, "You did not choose a work directory or the raw data", "Error", JOptionPane.ERROR_MESSAGE); 
			else
			{
				_start=true;
				_nucleusSegmentationBatchDialog.dispose();
			}
		}
	}
	
		
	class QuitListener implements ActionListener 
	{
		NucleusSegmentationBatchDialog _nucleusSegmentationBatchDialog;	
		public  QuitListener (NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog) {_nucleusSegmentationBatchDialog = nucleusSegmentationBatchDialog;}
		public void actionPerformed(ActionEvent actionEvent) { _nucleusSegmentationBatchDialog.dispose(); }
	}
		
	class WorkDirListener implements ActionListener
	{
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
				_workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldWorkDirectory.setText(_workDirectory);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
	
	class DataDirListener implements ActionListener
	{
				 
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
				_rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				_jTextFieldRawData.setText(_rawDataDirectory);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}
}