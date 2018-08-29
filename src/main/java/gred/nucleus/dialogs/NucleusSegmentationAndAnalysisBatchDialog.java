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

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;


/**
 * Class to construct graphical interface for the Nucleus Segentation analysis in batch
 * @author Poulet Axel
 *
 */
public class NucleusSegmentationAndAnalysisBatchDialog extends JFrame{
	private static final long serialVersionUID = 1L;
	private JButton m_jButtonWorkDirectory = new JButton("Output Directory");
	private JButton m_jButtonStart = new JButton("Start");
	private JButton m_jButtonQuit = new JButton("Quit");
	private JButton m_jButtonRawData = new JButton("Raw Data");
	
	private Container m_container;
	private JComboBox<Integer> m_comboBoxCpu = new JComboBox<Integer>();
	
	private JFormattedTextField m_jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField m_jTextFieldYCalibration = new JFormattedTextField(Number.class);
    private JFormattedTextField m_jTextFieldZCalibration =  new JFormattedTextField(Number.class);
    private JFormattedTextField m_jTextFieldMax =  new JFormattedTextField(Number.class);
    private JFormattedTextField m_jTextFieldMin =  new JFormattedTextField(Number.class);
    
    private JTextField m_jTextFieldUnit =  new JTextField();
    private JTextField m_jTextFieldWorkDirectory  =  new JTextField();
    private JTextField m_jTextFieldRawData = new JTextField();
	
    private JLabel m_jLabelXcalibration;
	private JLabel m_jLabelYcalibration;
	private JLabel m_jLabelZcalibration;
	private JLabel m_jLabelUnit;
	private JLabel m_jLabelSegmentation;
	private JLabel m_jLabelVolumeMin;
	private JLabel m_jLabelVolumeMax;
	private JLabel m_jLabelAnalysis;
	private JLabel m_jLabelWorkDirectory;
	private JLabel m_jLabelCalibration;
	private JLabel m_jLabelNbCpu;
	
	private ButtonGroup m_buttonGroupChoiceAnalysis = new ButtonGroup();
	private JRadioButton m_jRadioButton2DAnalysis = new JRadioButton("2D");
    private JRadioButton m_jRadioButton3DAnalysis = new JRadioButton("3D");
    private JRadioButton m_jRadioButton2D3DAnalysis = new JRadioButton("2D and 3D");
	
    private String m_workDirectory;
	private String m_rawDataDirectory;
	private boolean m_start = false;
	private int m_nbCpuChosen = 1;
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		NucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog = new NucleusSegmentationAndAnalysisBatchDialog();
    	nucleusSegmentationAndAnalysisBatchDialog.setLocationRelativeTo(null);
    }
	
    
    /**
     * Architecture of the graphical windows
     *
     */
    
	public NucleusSegmentationAndAnalysisBatchDialog (){
		this.setTitle("NJ1: Nucleus segmentation & analysis (batch)");
		this.setSize(500, 500);
		this.setLocationRelativeTo(null);
		m_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 71, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
	   	m_container.setLayout (gridBagLayout);
	   	
	   	m_jLabelWorkDirectory = new JLabel();
	   	m_container.add(
	   		m_jLabelWorkDirectory, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelWorkDirectory.setText("Work directory and Raw data choice : ");
	   
	   	m_container.add(
	   		m_jButtonRawData, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(30, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jButtonRawData.setFont(new java.awt.Font("Albertus",2,10));
	   	
		m_container.add(
			m_jTextFieldRawData, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(30, 160, 0, 0), 0, 0
			)
		);
		m_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		m_jTextFieldRawData.setFont(new java.awt.Font("Albertus",2,10));
	   	
	   	m_container.add(
	   		m_jButtonWorkDirectory, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(60, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jButtonWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
	   	
		m_container.add(
			m_jTextFieldWorkDirectory, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(60, 160, 0, 0), 0, 0
			)
		);
	   	m_jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
	   	
	   	m_jLabelCalibration = new JLabel();
	   	m_container.add(
	   		m_jLabelCalibration,new GridBagConstraints(
	   			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelCalibration.setText("Voxel Calibration:");
	   	
	   	m_container.setLayout (gridBagLayout);
		m_jLabelXcalibration = new JLabel();
		m_container.add(
			m_jLabelXcalibration,new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0
			)
		);
		m_jLabelXcalibration.setText("x :");
		m_jLabelXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldXCalibration,new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldXCalibration.setText("1");
		m_jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
	
		m_jLabelYcalibration = new JLabel();
		m_container.add(
			m_jLabelYcalibration,new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(65, 20, 0, 0), 0, 0
			)
		);
		m_jLabelYcalibration.setText("y :");
		m_jLabelYcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldYCalibration, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(65, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldYCalibration.setText("1");
		m_jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
	
		m_jLabelZcalibration = new JLabel();
		m_container.add(
			m_jLabelZcalibration, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(90, 20, 0, 0), 0, 0
			)
		);
		m_jLabelZcalibration.setText("z :");
		m_jLabelZcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldZCalibration, new GridBagConstraints(
				0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(90, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldZCalibration.setText("1");
		m_jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));	 
		
		m_jLabelUnit = new JLabel();
		m_container.add(
			m_jLabelUnit, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(115, 20, 0, 0), 0, 0
			)
		);
		m_jLabelUnit.setText("unit :");
		m_jLabelUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldUnit, new GridBagConstraints(
				0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(115, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldUnit.setText("pixel");
		m_jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));	

		m_jLabelSegmentation = new JLabel();
	   	m_container.add(
	   		m_jLabelSegmentation,new GridBagConstraints(
	   			0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelSegmentation.setText("Choose the min and max volumes of the nucleus:");
	   	
	   	m_jLabelVolumeMin = new JLabel();
	   	m_container.add(
	   		m_jLabelVolumeMin, new GridBagConstraints(
	   			0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelVolumeMin.setText("Minimun volume of the segmented nucleus :");
	   	m_jLabelVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldMin, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 320, 0, 0), 0, 0
			)
		);
		m_jTextFieldMin.setText("15");
		m_jTextFieldMin.setPreferredSize(new java.awt.Dimension( 60, 21));

		m_jLabelVolumeMax = new JLabel();
	   	m_container.add(
	   		m_jLabelVolumeMax, new GridBagConstraints(
	   			0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(70, 20, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelVolumeMax.setText("Maximum volume of the segmented nucleus :");
	   	m_jLabelVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldMax, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(70, 320, 0, 0), 0, 0
			)
		);
		m_jTextFieldMax.setText("2000");
		m_jTextFieldMax.setPreferredSize(new java.awt.Dimension (60, 21));
		
		
		m_jLabelAnalysis = new JLabel();
	   	m_container.add(
	   		m_jLabelAnalysis, new GridBagConstraints(
	   			0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(100, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelAnalysis.setText("Type of analysis:");
	   
	   	m_buttonGroupChoiceAnalysis.add(m_jRadioButton2DAnalysis);
		m_buttonGroupChoiceAnalysis.add(m_jRadioButton3DAnalysis);
		m_buttonGroupChoiceAnalysis.add(m_jRadioButton2D3DAnalysis);
		m_jRadioButton2DAnalysis.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButton3DAnalysis.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButton2D3DAnalysis.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jRadioButton2DAnalysis, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(130, 170, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButton3DAnalysis, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(130, 120, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButton2D3DAnalysis,new GridBagConstraints(
				0,3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(130, 10, 0, 0), 0, 0
			)
		);
		m_jRadioButton2D3DAnalysis.setSelected(true);
		
		OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
		int nbProc = bean.getAvailableProcessors();
		for (int i = 1; i <= nbProc; ++i) m_comboBoxCpu.addItem(i);
		m_jLabelNbCpu= new JLabel();
		m_jLabelNbCpu.setText("How many CPU(s) :");
		m_container.add(
			m_jLabelNbCpu, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(175, 10, 0,0), 0, 0
			)
		);
		m_container.add(
			m_comboBoxCpu, new GridBagConstraints(
				0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(170, 200, 0,0), 0, 0
			)
		);
		m_comboBoxCpu.addItemListener(new ItemState());
			
		m_container.add(
			m_jButtonStart, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(210, 140, 0,0), 0, 0
			)
		);
		m_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		m_container.add(
			m_jButtonQuit, new GridBagConstraints(
				0, 3, 0, 0,0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(210, 10, 0, 0), 0, 0
			)
		);
		m_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
	  	this.setVisible(true);
	   	
	  	WorkDirectoryListener wdListener = new WorkDirectoryListener();
	  	m_jButtonWorkDirectory.addActionListener(wdListener);
	  	RawDataDirectroryListener ddListener = new RawDataDirectroryListener();
	  	m_jButtonRawData.addActionListener(ddListener);
	  	QuitListener quitListener = new QuitListener(this);
	   	m_jButtonQuit.addActionListener(quitListener);
	   	StartListener startListener = new StartListener(this);
	   	m_jButtonStart.addActionListener(startListener);	   
	}
	
	/**
	 * 
	 * @param nb
	 */
	public void setNbCpu(int nb){
		m_nbCpuChosen = nb;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNbCpu(){
		return m_nbCpuChosen;
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
	public String getWorkDirectory(){
		return m_jTextFieldWorkDirectory.getText();
	}
	/**
	 * 
	 * @return
	 */
	public String getRawDataDirectory(){
		return m_jTextFieldRawData.getText();
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
		return m_jRadioButton2D3DAnalysis.isSelected();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean is2D(){
		return m_jRadioButton2DAnalysis.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean is3D(){
		return m_jRadioButton3DAnalysis.isSelected();
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
	 */
	class ItemState implements ItemListener{
		/**
		 * 
		 */
		public void itemStateChanged(ItemEvent e){
			setNbCpu((Integer) e.getItem());
		}               
	}
	
	/**
	 * 
	 * 
	 */
	class StartListener implements ActionListener {
	
		NucleusSegmentationAndAnalysisBatchDialog m_nuc;	
		/**
		 * 
		 * @param nucleusSegmentationAndAnalysisBatchDialog
		 */
		public  StartListener (NucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog){
			m_nuc = nucleusSegmentationAndAnalysisBatchDialog;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			if (m_jTextFieldWorkDirectory.getText().isEmpty() || m_jTextFieldRawData.getText().isEmpty())
				 JOptionPane.showMessageDialog
				 (
					null, "You did not choose a work directory or the raw data",
					"Error", JOptionPane.ERROR_MESSAGE
				); 
			else{
				m_start=true;
				m_nuc.dispose();
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	class QuitListener implements ActionListener {
		NucleusSegmentationAndAnalysisBatchDialog m_nuc;	
		/**
		 * 
		 * @param nucleusSegmentationAndAnalysisBatchDialog
		 */
		public  QuitListener (NucleusSegmentationAndAnalysisBatchDialog nucleusSegmentationAndAnalysisBatchDialog){
			m_nuc = nucleusSegmentationAndAnalysisBatchDialog;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			m_nuc.dispose();
		}
	}
	
	/**
	 * 
	 *
	 */
	class WorkDirectoryListener implements ActionListener{	
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION){
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				 m_workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				 m_jTextFieldWorkDirectory.setText(m_workDirectory);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}	
	}

	/**
	 * 
	 * 
	 */
	class RawDataDirectroryListener implements ActionListener{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if(returnValue == JFileChooser.APPROVE_OPTION){
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				m_rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				m_jTextFieldRawData.setText(m_rawDataDirectory);
			}
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		 }	
	 }
}