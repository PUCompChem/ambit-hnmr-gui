package pu.hnmr;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class AmbitHnmrGUI extends JFrame
{
	
		public static void main(String[] args) 
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() 
				{	
					try {
						AmbitHnmrGUI ahg = new AmbitHnmrGUI();
						ahg.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			});
		}
		
		public AmbitHnmrGUI() throws Exception 
		{
			super();
			initGUI();
		}
		
		private void initGUI() throws Exception
		{
			setSize(new Dimension(1110,795));
			setResizable(false);

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			revalidate();
			setTitle("Ambit HNMR");	
			setLayout(new FlowLayout());
			HNMRSimplePanel hsp = new HNMRSimplePanel();
			hsp.setPreferredSize(new Dimension(1100,760));
			add(hsp);
		}
}
