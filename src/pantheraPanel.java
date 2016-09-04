import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;


public class pantheraPanel extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 1L;
	public static JPanel panel;
	public static Stroke use;
	public static JCheckBox chckbxShowLionsAndPonds = new JCheckBox("Show lions and ponds in selected region");
	public static List<int[]> cList = new ArrayList<int[]>();
	public static List<Polygon> pList = new ArrayList<Polygon>();
	public static List<int[]> lList = new ArrayList<int[]>();
	public static List<int[]> tempcList = new ArrayList<int[]>();
	public static List<Polygon> tempList = new ArrayList<Polygon>();
	public static List<int[]> templList = new ArrayList<int[]>();
	public static int click;
	
	public pantheraPanel()
	{
		initialize();
		addMouseListener(this);
	}
	
	public void initialize()
	{
		chckbxShowLionsAndPonds.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event) {
				JCheckBox cb = (JCheckBox) event.getSource();
				if (cb.isSelected()) 
				{
					click = 1;
				} 
				else
				{
					click = 2;
					repaint();
				}
			}
		});
	}
	
	public void doDrawing(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		use = g2d.getStroke();
		g2d.setStroke(new BasicStroke(4));
		if(click == 0)
		{
			lList = getLionValues();
			g2d.setColor(Color.green);
			colorLionGreen(g2d);
		}
		
		if(click == 2)
		{
			g2d.setColor(Color.green);
			colorLionGreen(g2d);
		}

		else if(click == 1)
		{
			g2d.setColor(Color.green);
			colorLionGreen(g2d);
			g2d.setColor(Color.RED);
			for(int j=0; j<templList.size(); j++)
			{
				int[] coord = templList.get(j);
				int x = coord[0];
				int y = coord[1];
				//g2d.drawLine(templList.get(j)[0],templList.get(j)[1],templList.get(j)[0],templList.get(j)[1]);   
				int r = 8;
				x = x-(r/2);
				y = y-(r/2);
				g2d.fillOval(x,y,r,r);
			}
		}
	}

	public void colorLionGreen(Graphics2D g2d)
	{
		for(int j=0; j<lList.size(); j++)
		{
			int[] coord = lList.get(j);
			int x = coord[0];
			int y = coord[1];
			//g2d.drawLine(lList.get(j)[0],lList.get(j)[1],lList.get(j)[0],lList.get(j)[1]);   
			int r = 8;
			x = x-(r/2);
			y = y-(r/2);
			g2d.fillOval(x,y,r,r);
		}
	}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(click == 0)
		{
			pList = getValues();
			for(int k=0; k<pList.size(); k++)
			{
				g.drawPolygon(pList.get(k));
			}
		}	
		else
		{
			for(int k=0; k<pList.size(); k++)
			{
				g.drawPolygon(pList.get(k));
			}
		}
		doDrawing(g);
		drawCenteredCircle(g);	
	}
	
	public void drawCenteredCircle(Graphics g) 
	{
		Graphics2D g2d = (Graphics2D) g;
		if(click == 0)
		{
			cList = getPondValues();
		}
		g2d.setStroke(use);
		if(click == 0)
		{
			drawPondBlue(g2d);
		}
		
		if(click == 1)
		{
			drawPondBlue(g2d);
			for(int j=0; j<tempcList.size(); j++)
			{
				int[] coord = new int[3];
				coord = tempcList.get(j);
				int x = coord[0];
				int y = coord[1];
				int z = coord[2];
				g2d.setColor(Color.RED);
				g2d.fillOval(x,y,z,z);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(x,y,z,z);
			}
		}
		
		if(click == 2)
		{
			drawPondBlue(g2d);
		}
	}
	
	public void drawPondBlue(Graphics2D g2d)
	{
		for(int j=0; j<cList.size(); j++)
		{
			int[] coord = new int[3];
			coord = cList.get(j);
			int x = coord[0];
			int y = coord[1];
			int z = coord[2];
			g2d.setColor(Color.BLUE);
			g2d.fillOval(x,y,z,z);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(x,y,z,z);
		}
	}

	public void checkInDB(int x, int y) 
	{
		Point check = new Point(x, y);
		List<int[]> newcList = new ArrayList<int[]>();
		List<int[]> newlList = new ArrayList<int[]>();
		for(int q=0; q<pList.size(); q++)
		{
			Polygon temp = pList.get(q);
			if(temp.contains(check))
			{
				newlList = checkLion(lList, temp, x, y);
				for(int h=0; h<cList.size(); h++)
				{
					int[] checkPond = cList.get(h);
					int test = insidePolygon(checkPond, temp);
					if(test == 1)
					{
						newcList.add(checkPond);
					}
				}
				templList.clear();
				templList.addAll(newlList);
				tempcList.clear();
				tempcList.addAll(newcList);
			}	
		}
		repaint();
	}
	
	public List<int[]> checkLion(List<int[]> lList, Polygon temp, int x, int y)
	{
		List<int[]> newlList = new ArrayList<int[]>();
		for(int k=0; k<lList.size(); k++)
		{
			int[] checkLion = lList.get(k);
			x = checkLion[0];
			y = checkLion[1];
			Point checkLeo = new Point(x,y);
			if(temp.contains(checkLeo))
			{
				newlList.add(checkLion);
			}
		}
		return newlList;
	}
	
	public static int insidePolygon(int[] checkPond, Polygon temp) 
	{
		int flag = 0;
		int xCenter = checkPond[0];
		int yCenter = checkPond[1];
		int radius = checkPond[2];
		Point p = new Point(xCenter, yCenter);
		if(temp.contains(p))
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
	public void mouseClicked(MouseEvent m) {
		Point p = m.getPoint();
		int x = p.x;
		int y = p.y;
		checkInDB(x,y);
	}

	public void mouseEntered(MouseEvent m) {
		
		
	}

	public void mouseExited(MouseEvent m) {
		
		
	}

	public void mousePressed(MouseEvent m) {
		
		
	}

	public void mouseReleased(MouseEvent m) {
		
		
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
			System.out.println("**Some exception**");
		}
		return cList;
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
			System.out.println("**Some exception**");
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
				int xcount = 0;
				for(int i=0; i<ordinates.length-2; i=i+2)
				{
					xPts[xcount] = (int)ordinates[i];
					xcount++;
				}
				int ycount = 0;
				for(int i=1; i<ordinates.length-2; i=i+2)
				{
					yPts[ycount] = (int)ordinates[i];
					ycount++;
				}
				Polygon p = new Polygon(xPts, yPts, xPts.length);
				pList.add(p);
			}
		}
		catch(Exception e)
		{
			System.out.println("**Some exception**");
		}
		return pList;
	}
}
