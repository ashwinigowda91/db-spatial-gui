import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.CLOB;

public class pantheraGUI extends JFrame implements MouseListener
{
	public static JFrame frame;
	public static JPanel panel;
	public static Stroke use;
	public static JCheckBox chckbxShowLionsAnd = new JCheckBox("Show lions and ponds in selected region");
	public static List<int[]> cList = new ArrayList<int[]>();
	public static List<Polygon> pList = new ArrayList<Polygon>();
	public static List<int[]> lList = new ArrayList<int[]>();
	public static int rule;

	//constructor
	public pantheraGUI()
	{	
		initialize();
		frame.addMouseListener(this);
	}

	public void initialize()
	{
		frame = this;
		//frame.setBounds(30,30,800,800);
		frame.setSize(600,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(chckbxShowLionsAnd, BorderLayout.NORTH);
		chckbxShowLionsAnd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event) {
				JCheckBox cb = (JCheckBox) event.getSource();
				if (cb.isSelected()) 
				{
					rule = 1;
					//System.out.println("Selected");
				} 
				else 
				{
					rule = 2;
					frame.repaint();
				}
			}
		});
	}

	public void doDrawing(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		use = g2d.getStroke();
		g2d.setStroke(new BasicStroke(4));
		if(rule == 0 || rule == 2)
		{
			g2d.setColor(Color.GREEN);

		}

		else if(rule == 1)
		{
			g2d.setColor(Color.RED);
		}
		if(rule == 0)
		{
			lList = getLionValues();
		}
		for(int j=0; j<lList.size(); j++)
		{
			int x = lList.get(j)[0];
			int y = lList.get(j)[1];
			g2d.drawLine(lList.get(j)[0],lList.get(j)[1],lList.get(j)[0],lList.get(j)[1]);   
		}
	}

	public void drawCenteredCircle(Graphics g) 
	{
		if(rule == 0)
		{
			cList = getPondValues();
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(use);
		for(int j=0; j<cList.size(); j++)
		{
			if(rule == 0 || rule == 2)
			{
				g2d.setColor(Color.BLUE);
			}
			else if(rule == 1)
			{
				g2d.setColor(Color.RED);
			}
			g2d.fillOval(cList.get(j)[0],cList.get(j)[1],cList.get(j)[2],cList.get(j)[2]);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(cList.get(j)[0],cList.get(j)[1],cList.get(j)[2],cList.get(j)[2]);
		}
	}

	public static void checkInDB(int x, int y) 
	{

		Point check = new Point(x, y);
		List<int[]> templList = new ArrayList<int[]>();
		List<int[]> tempcList = new ArrayList<int[]>();
		for(int q=0; q<pList.size(); q++)
		{
			Polygon temp = pList.get(q);
			if(temp.contains(check))
			{
				for(int k=0; k<lList.size(); k++)
				{
					int[] checkLion = lList.get(k);
					x = checkLion[0];
					y = checkLion[1];
					Point checkLeo = new Point(x, y);
					if(temp.contains(checkLeo))
					{
						templList.add(checkLion);
					}
				}

				for(int h=0; h<cList.size(); h++)
				{
					int[] checkPond = cList.get(h);
					boolean test = insidePolygon(checkPond, temp);
					if(test == true)
					{
						tempcList.add(checkPond);
					}
				}
				lList.clear();
				lList.addAll(templList);
				cList.clear();
				cList.addAll(tempcList);
			}

		}
	}

	public static boolean insidePolygon(int[] checkPond, Polygon temp) 
	{
		int xCenter = checkPond[0];
		int yCenter = checkPond[1];
		int radius = checkPond[2];

		Point p = new Point(xCenter, yCenter);
		if(temp.contains(p))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public List<int[]> getPondValues() {

		Connection conn = null;
		Statement stmt = null;

		try
		{
			conn = connectionToDB.dbConnect();
			stmt = conn.createStatement();
			ResultSet  rset = stmt.executeQuery("select circle from pond");
			while(rset.next())
			{
				int[] pts = new int[6];
				int[] coord = new int[3];
				STRUCT st = (STRUCT) rset.getObject(1);
				JGeometry jgeom = JGeometry.load(st);
				double[] ordinates = jgeom.getOrdinatesArray();
				coord[0] = (int) ordinates[2];
				coord[1] = (int) ordinates[1];
				coord[2] = (int) (ordinates[2] - ordinates[0]);
				cList.add(coord);

			}
		}
		catch(Exception e)
		{
			System.out.println("Here. . ");
		}
		return cList;
	}

	public void paint(Graphics g)
	{
		if(rule == 0)
		{
			pList = getValues();
		}	
		for(int k=0; k<pList.size(); k++)
		{
			g.drawPolygon(pList.get(k));
		}
		doDrawing(g);
		drawCenteredCircle(g);	
	}


	public List<int[]> getLionValues() 
	{
		Connection conn = null;
		Statement stmt = null;

		try
		{
			conn = connectionToDB.dbConnect();
			stmt = conn.createStatement();
			ResultSet  rset = stmt.executeQuery("select point from lion");
			while(rset.next())
			{
				int[] pts = new int[2];

				STRUCT st = (STRUCT) rset.getObject(1);
				JGeometry jgeom = JGeometry.load(st);

				double[] ordinates = jgeom.getPoint();
				pts[0] = (int)ordinates[0];
				pts[1] = (int)ordinates[1];
				lList.add(pts);

			}
		}
		catch(Exception e)
		{
			System.out.println("Here. . ");
		}
		return lList;

	}

	public static List<Polygon> getValues() 
	{
		Connection conn = null;
		Statement stmt = null;
		try
		{
			conn = connectionToDB.dbConnect();
			stmt = conn.createStatement();
			ResultSet  rset = stmt.executeQuery("select shape from region");
			while(rset.next())
			{
				int[] xPts = new int[4];
				int[] yPts = new int[4];
				STRUCT st = (STRUCT) rset.getObject(1);
				JGeometry jgeom = JGeometry.load(st);

				double[] ordinates = jgeom.getOrdinatesArray();
				int count = 0;
				for(int i=0; i<ordinates.length-2; i=i+2)
				{
					xPts[count] = (int)ordinates[i];
					count++;
				}

				count = 0;
				for(int i=1; i<ordinates.length-2; i=i+2)
				{
					yPts[count] = (int)ordinates[i];
					count++;
				}

				Polygon p = new Polygon(xPts, yPts, xPts.length);
				pList.add(p);
			}
		}
		catch(Exception e)
		{
			System.out.println("Here. . ");
		}
		return pList;
	}

	public static void main(String[] args) 
	{
		rule = 0;
		//check for an event
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					pantheraGUI pg = new pantheraGUI();
					pg.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});	
	}

	public void mouseClicked(MouseEvent m) {
		Point p = m.getPoint();
		int x = p.x;
		int y = p.y;
		checkInDB(x,y);
		frame.repaint();
		
	}

	public void mouseEntered(MouseEvent m) {
		
		
	}

	public void mouseExited(MouseEvent m) {
		
		
	}

	public void mousePressed(MouseEvent m) {
		
		
	}

	public void mouseReleased(MouseEvent m) {
		
		
	}	
}
