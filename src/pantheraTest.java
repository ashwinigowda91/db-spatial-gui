import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JFrame;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class pantheraTest extends JFrame{

	private JFrame frame;
	public static int[] xpts , ypts;
	
	/**
	 * Launch the application.
	 */
	public pantheraTest() {
		initialize();
	}

	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JCheckBox chckbxShowLionsAnd = new JCheckBox("Show lions and ponds in selected region");
		frame.getContentPane().add(chckbxShowLionsAnd, BorderLayout.SOUTH);
		
	}	
	
	public void paint(Graphics g)
	{
		g.drawRect(75,75,300,200);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					pantheraTest window = new pantheraTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}
}
