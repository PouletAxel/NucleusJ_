package gred.nucleus.graphicInterface;

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

/**
 * 
 * @author gred
 *
 */

public class JFSegmentation  extends JFrame
{
	
	private static final long serialVersionUID = 1L;
	private JButton  _JBstart = new JButton("Start"), _JBquit = new JButton("Quit");
	private Container _container;
    private JFormattedTextField _jTFMax =  new JFormattedTextField(Number.class);
	private JFormattedTextField _jTFMin =  new JFormattedTextField(Number.class);
	private JLabel _jLSegmentation, _jLVolumeMin, _jLVolumeMax, _JlUnitMin;
	private boolean _start = false;
	
	/**
	 * 
	 * @param args
	 */
	
	public static void main(String[] args)  
	{
		JFSegmentation fenetre = new JFSegmentation("pixel");
	   	fenetre.setLocationRelativeTo(null);
	}
		
	    
	  
	    
	public JFSegmentation (String unit)
	{
		this.setTitle("Segmentation 3D");
		this.setSize(500, 250);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		_container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[] {17, 71, 124, 7};
		gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[] {236, 109, 72, 20};
		_container.setLayout (gridBagLayout);
		_jLSegmentation = new JLabel();
		_container.add(_jLSegmentation, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		_jLSegmentation.setText("Choose the min and max volume of the nucleus:");
		_jLVolumeMin = new JLabel();
		_container.add(_jLVolumeMin, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(30, 20, 0, 0), 0, 0));
		_jLVolumeMin.setText("Minimun volume of the segmented nucleus :");
		_jLVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFMin, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(30, 300, 0, 0), 0, 0));
		_jTFMin.setText("15");
		_jTFMin.setPreferredSize(new java.awt.Dimension( 60, 21));
		_JlUnitMin = new JLabel();
		_container.add(_JlUnitMin, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(30, 370, 0, 0), 0, 0));
		_JlUnitMin.setText(unit+"^3");
		_JlUnitMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		
		_jLVolumeMax = new JLabel();
		_container.add(_jLVolumeMax, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(60, 20, 0, 0), 0, 0));
		_jLVolumeMax.setText("Maximum volume of the segmented nucleus :");
		_jLVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));
		_container.add(_jTFMax, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(60, 300, 0, 0), 0, 0));
		_jTFMax.setText("2000");
		_jTFMax.setPreferredSize(new java.awt.Dimension (60, 21));
		_JlUnitMin = new JLabel();
		_container.add(_JlUnitMin, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(60, 370, 0, 0), 0, 0));
		_JlUnitMin.setText(unit+"^3");
		_JlUnitMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)",2,12));		
		
		_container.add(_JBstart, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(100, 140, 0,0), 0, 0));
		_JBstart.setPreferredSize(new java.awt.Dimension(120, 21));
		_container.add(_JBquit, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(100, 10, 0, 0), 0, 0));
		_JBquit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		QuitListener quitListener = new QuitListener(this);
		_JBquit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		_JBstart.addActionListener(startListener);	   
	}
	
	/**
	 * 
	 * @return
	 */
	public double getMinSeg(){ return Double.parseDouble(_jTFMin.getText()); }
	/**
	 * 
	 * @return
	 */
	public double getMaxSeg(){ return Double.parseDouble(_jTFMax.getText()); }
	/**
	 * 
	 * @return
	 */
	public boolean isStart() {	return _start; }
		
		

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
		JFSegmentation _jfSeg;	
		public  StartListener (JFSegmentation jfSeg) {_jfSeg = jfSeg;}
		/**
		 * 
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			_start=true;
			_jfSeg.dispose();
		}
	}
		
		
	/**
	 * 
	 * @author lom
	 *
	 */
	
	class QuitListener implements ActionListener 
	{
		JFSegmentation _jfSeg;	
		public  QuitListener (JFSegmentation jfSeg) {_jfSeg = jfSeg;}
		public void actionPerformed(ActionEvent actionEvent) { _jfSeg.dispose(); }
	}
}