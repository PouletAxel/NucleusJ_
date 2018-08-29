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
import javax.swing.JTextPane;


/**
 * Class to construct graphical interface for the chromocenter analysis pipeline in batch
 * 
 * @author pouletaxel
 *
 */

public class ChromocentersAnalysisPipelineBatchDialog extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JButton m_jButtonWorkDirectory = new JButton("Output Directory");
	private JButton m_jButtonStart = new JButton("Start");
	private JButton m_jButtonQuit = new JButton("Quit");
	private JButton m_jButtonRawData = new JButton("Raw Data");
	
	private Container m_container;

	private JFormattedTextField m_jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private JFormattedTextField m_jTextFieldYCalibration = new JFormattedTextField(Number.class);
    private JFormattedTextField m_jTextFieldZCalibration =  new JFormattedTextField(Number.class);
    
    private JTextField _jTextFieldUnit =  new JTextField();
    private JTextField m_jTextFieldWorkDirectory  =  new JTextField();
    private JTextField  m_jTextFieldRawData = new JTextField();
	
    private JLabel m_jLabelXcalibration;
    private JLabel m_jLabelYcalibration;
    private JLabel m_jLabelZcalibration;
    private JLabel m_jLabelUnit;
    private JLabel m_jLabelAnalysis;
    private JLabel m_jLabelWorkDirectory;
    private JLabel m_jLabelCalibration;
    
	private ButtonGroup m_buttonGroupChoiceRhf = new ButtonGroup();
	private JRadioButton m_jRadioButtonRhfV = new JRadioButton("VolumeRHF");
    private JRadioButton m_jRadioButtonRhfI = new JRadioButton("IntensityRHF");
    private JRadioButton m_jRadioButtonRhfIV = new JRadioButton("VolumeRHF and IntensityRHF");
    
    private ButtonGroup m_buttonGroupChoiceAnalysis = new ButtonGroup();
	private JRadioButton m_jRadioButtonNucCc = new JRadioButton("Nucleus and chromocenter");
    private JRadioButton m_jRadioButtonCc = new JRadioButton("Chromocenter");
    private JRadioButton m_jRadioButtonNuc = new JRadioButton("Nucleus");
    
	private String m_workDirectory;
	private String m_rawDataDirectory;
	private boolean m_start = false;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog = new ChromocentersAnalysisPipelineBatchDialog();
    	chromocentersAnalysisPipelineBatchDialog.setLocationRelativeTo(null);
    }
	
    
    /**
     * Architecture of the graphical windows
     *
     */
    
	public ChromocentersAnalysisPipelineBatchDialog(){
		this.setTitle("NJ1: Chromocenters Analysis Pipeline (Batch)");
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		m_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
	   	gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.rowHeights = new int[] {17, 200, 124, 7};
	   	gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
	   	gridBagLayout.columnWidths = new int[] {236, 120, 72, 20};
	   	m_container.setLayout (gridBagLayout);
	   	
	   	m_jLabelWorkDirectory = new JLabel();
	   	m_container.add(
	   		m_jLabelWorkDirectory, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelWorkDirectory.setText("Work directory and data directory choice : ");

	   	JTextPane jTextPane = new JTextPane();
	   	jTextPane.setText("The Raw Data directory must contain 3 subdirectories:"
	   			+ "\n1. for raw nuclei images, named RawDataNucleus. "
	   			+ "\n2. for segmented nuclei images, named SegmentedDataNucleus."
	   			+ "\n3. for segmented images of chromocenters, named SegmentedDataCc."
	   			+ "\nPlease keep the same file name during the image processing.");
	   	jTextPane.setEditable(false);
	   	m_container.add(
	   		jTextPane, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0
	   		)
	   	);
	   	
	   	m_container.add(
	   		m_jButtonRawData, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
	   			GridBagConstraints.NONE, new Insets(110, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jButtonRawData.setFont(new java.awt.Font("Albertus",2,10));
		
		m_container.add(
			m_jTextFieldRawData, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(110, 160, 0, 0),0, 0
			)
		);
		m_jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		m_jTextFieldRawData.setFont(new java.awt.Font("Albertus",2,10));
	   	
	   	m_container.add(
	   		m_jButtonWorkDirectory, new GridBagConstraints(
	   			0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(150, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
	   	m_jButtonWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
	   	
		m_container.add(
			m_jTextFieldWorkDirectory, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(150, 160, 0, 0), 0, 0
		    )
		);
	   	m_jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
	   	m_jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus",2,10));
	   	
	   	m_jLabelCalibration = new JLabel();
	   	m_container.add(
	   		m_jLabelCalibration, new GridBagConstraints(
	   			0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0
	   	    )
	   	);
	   	m_jLabelCalibration.setText("Voxel Calibration:");
	   	
	   	m_container.setLayout (gridBagLayout);
		m_jLabelXcalibration = new JLabel();
		m_container.add(
			m_jLabelXcalibration, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(40, 20, 0, 0), 0, 0
			)
		);
		m_jLabelXcalibration.setText("x :");
		m_jLabelXcalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jTextFieldXCalibration, new GridBagConstraints(
				0, 2, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(40, 60, 0, 0), 0, 0
			)
		);
		m_jTextFieldXCalibration.setText("1");
		m_jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
	
		m_jLabelYcalibration = new JLabel();
		m_container.add( m_jLabelYcalibration, new GridBagConstraints(
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
			_jTextFieldUnit, new GridBagConstraints(
				0, 2,0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(115, 60, 0, 0), 0, 0
		    )
		);
		_jTextFieldUnit.setText("pixel");
		_jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));	
		
		m_jLabelAnalysis = new JLabel();
	   	m_container.add(
  			m_jLabelAnalysis, new GridBagConstraints(
   				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(30, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelAnalysis.setText("Relative Heterochromatin Fraction:");
	   	m_buttonGroupChoiceRhf.add(m_jRadioButtonRhfV);
		m_buttonGroupChoiceRhf.add(m_jRadioButtonRhfI);
		m_buttonGroupChoiceRhf.add(m_jRadioButtonRhfIV);
		m_jRadioButtonRhfV.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButtonRhfI.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButtonRhfIV.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jRadioButtonRhfV,	new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(60, 370, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButtonRhfI, new GridBagConstraints(
				0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(60, 250, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButtonRhfIV, new GridBagConstraints(
				0,3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(60, 20, 0, 0), 0, 0
			)
		);
		m_jRadioButtonRhfIV.setSelected(true);

		m_jLabelAnalysis = new JLabel();
	   	m_container.add(
	   		m_jLabelAnalysis, new GridBagConstraints(
	   			0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(95, 10, 0, 0), 0, 0
	   		)
	   	);
	   	m_jLabelAnalysis.setText("Results of interest: ");
	   
	   	m_buttonGroupChoiceAnalysis.add(m_jRadioButtonNucCc);
	   	m_buttonGroupChoiceAnalysis.add(m_jRadioButtonCc);
	   	m_buttonGroupChoiceAnalysis.add(m_jRadioButtonNuc);
	   	m_jRadioButtonNuc.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButtonCc.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_jRadioButtonNucCc.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		m_container.add(
			m_jRadioButtonNuc, new GridBagConstraints(
				0, 3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(120, 370, 0, 0), 0, 0
			)
		);
		
		m_container.add(
			m_jRadioButtonCc, new GridBagConstraints(
				0, 3, 0, 0,0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(120, 250, 0, 0), 0, 0
			)
		);
		m_container.add(
			m_jRadioButtonNucCc,new GridBagConstraints(
				0,3, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(120, 20, 0, 0), 0, 0
			)
		);
		m_jRadioButtonNucCc.setSelected(true);
		
	   	m_container.add(
	   		m_jButtonStart,	new GridBagConstraints(
	   			0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
	   			GridBagConstraints.NONE, new Insets(190, 140, 0,0), 0, 0
	   		)
	   	);
		m_jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		m_container.add(
			m_jButtonQuit, new GridBagConstraints(
				0, 3, 0, 0,  0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(190, 10, 0, 0), 0, 0
			)
		);
		m_jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
	  	
	   	
	  	WorkDirectoryListener wdListener = new WorkDirectoryListener();
	  	m_jButtonWorkDirectory.addActionListener(wdListener);
	  	RawDataDirectoryListener ddListener = new RawDataDirectoryListener();
	  	m_jButtonRawData.addActionListener(ddListener);
	  	QuitListener quitListener = new QuitListener(this);
	   	m_jButtonQuit.addActionListener(quitListener);
	   	StartListener startListener = new StartListener(this);
	   	m_jButtonStart.addActionListener(startListener);	  
	   	this.setVisible(true);
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
		return _jTextFieldUnit.getText();
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
	public boolean isNucAndCcAnalysis(){
		return m_jRadioButtonNucCc.isSelected(); 
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isNucAnalysis(){
		return m_jRadioButtonNuc.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isCcAnalysis(){
		return m_jRadioButtonCc.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isRHFVolumeAndIntensity(){
		return m_jRadioButtonRhfIV.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isRhfVolume(){
		return m_jRadioButtonRhfV.isSelected();
	}
	/**
	 * 
	 * @return
	 */
	public boolean isRhfIntensity(){
		return m_jRadioButtonRhfI.isSelected();
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
		ChromocentersAnalysisPipelineBatchDialog m_chr;
		/**
		 * 
		 * @param chromocentersAnalysisPipelineBatchDialog
		 */
		public  StartListener (ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog){
			m_chr = chromocentersAnalysisPipelineBatchDialog;
		}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			if (m_jTextFieldWorkDirectory.getText().isEmpty() || m_jTextFieldRawData.getText().isEmpty())
				JOptionPane.showMessageDialog(
					null, "You did not choose a work directory or the raw data",
					"Error", JOptionPane.ERROR_MESSAGE
				); 
			else{
				m_start=true;
				m_chr.dispose();
			}
		}
	}
	
	/**
	 * 
	 * 
	 *
	 */
	class QuitListener implements ActionListener{
		ChromocentersAnalysisPipelineBatchDialog m_chr;	
		/**
		 * 
		 * @param chromocentersAnalysisPipelineBatchDialog
		 */
		public  QuitListener (ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog){
			m_chr = chromocentersAnalysisPipelineBatchDialog;
		}
		
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent){
			m_chr.dispose();
		}
	}
	
	/**
	 * 
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
	 *
	 */
	class RawDataDirectoryListener implements ActionListener{
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
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