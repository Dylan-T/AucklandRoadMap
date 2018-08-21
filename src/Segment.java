import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Set;
/**
 * This class creates a segment of a road between two Nodes
 * @author Dylan
 *
 */
public class Segment {

	private Road road; // Road road
	private double length;
	private Node fromNode; // Node fromNode
	private Node toNode; // Node toNode
	private List<Location> coords;
	
	public Segment(Road road, double length, Node fromNode, Node toNode, List<Location> coords) {
		this.road = road;
		this.length = length;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.coords = coords;
	}

	//Getters
	public double getLength() {
		return length;
	}
	
	public List<Location> getCoords() {
		return coords;
	}
	
	public Road getRoad() {
		return road;
	}
	
	public Node getTo() {
		return toNode;
	}
	
	public Node getFrom() {
		return fromNode;
	}
	
	public void draw(Graphics g, Location origin, double scale) {
		Point from = fromNode.getLoc().asPoint(origin, scale);
		Point to;
		for(Location l: coords) {
			to = l.asPoint(origin, scale);
			g.drawLine(from.x, from.y, to.x, to.y);
			from = to;
		}
	}
}
