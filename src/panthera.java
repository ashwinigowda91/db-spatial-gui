import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class panthera
{
	public static JFrame frame = new JFrame("Panthera");
	public static void main(String[] args) 
	{
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new pantheraPanel());
        frame.setSize(800,800);
        frame.setVisible(true);
        frame.getContentPane().add(pantheraPanel.chckbxShowLionsAndPonds, BorderLayout.NORTH);
	}
}	

