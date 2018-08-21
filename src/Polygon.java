import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

public class Polygon {
	
	String type;
	int endLevel;
	int cityIdx;
	String label;
	List<List<Location>> coords = new ArrayList<List<Location>>();
	Color color;
	
	public Polygon(String type, int endLevel, int cityIdx, String label, List<List<Location>> data0){
		this.type = type;
		this.endLevel = endLevel;
		this.cityIdx = cityIdx;
		this.label = label;
		coords = data0;
		color = getColor();
	}
	
	public void draw(Graphics g, Location origin, double scale) {
		//Get the color
		g.setColor(color);
		for(List<Location> li: coords) {
			int nPoints = li.size();
			int[] xPoints = new int[nPoints];
			int[] yPoints = new int[nPoints];
			ArrayList<Point> points = new ArrayList<Point>();
			for(Location l: li) {
				points.add(l.asPoint(origin, scale));
			}
			int i = 0;
			for(Point p: points) {
				xPoints[i] = p.x;
				yPoints[i++] = p.y;
			}
			g.fillPolygon(xPoints, yPoints, nPoints);
		}
	}
	
	public Color getColor() {		
		switch(type) {
		
		//City
		case "0x13":
		case "0x17":
		case "0x2":
		case "0xa":
		case "0x7":
		case "0x5":
		case "0xb":
		case "0xe":
		case "0x8":
			return new Color(234,233,228);
			
		//Waters
		case "0x40":
		case "0x28":
		case "0x3c":
		case "0x47":
		case "0x3e":
		case "0x48":
		case "0x45":
		case "0x41":
			return new Color(170,218,255);
		
		//Greens
		case "0x19":
		case "0x16":
		case "0x50":
		case "0x1a":
		case "0x1e":
			return new Color(196,237,181);

		

		}
		//Default to city color
		return new Color(234,233,228);
	}
}
