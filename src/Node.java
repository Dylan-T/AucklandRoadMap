import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

/**
 * This creates a node with coordinates, an id and lists of connected road segments
 * @author Dylan
 *
 */
public class Node {
	private int id;
	private Location loc;
	private Set<Segment> segments = new HashSet<Segment>();
	
	
	int count = Integer.MAX_VALUE;
	int reachBack = 0;
	Node parent;
	List<Node> children = new ArrayList<Node>();
	
	public Node(int id, double lat, double lon) {
		this.id = id;
		loc = Location.newFromLatLon(lat, lon);
	}
	
	public double getX() {
		return loc.x;
	}
	
	public double getY() {
		return loc.y;
	}
	
	public Set<Segment> getSegs(){
		return segments;
	}

	public int getId() {
		return id;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public void addSeg(Segment s) {
		segments.add(s);
	}
	
	public List<Node> getNeighbours(){
		List<Node> neighbours = new ArrayList<Node>();
		for(Segment s: getSegs()) {
			Node n1 = s.getTo();
			Node n2 = s.getFrom();
			if(n1 != this) neighbours.add(n1);
			else if(n2 != this) neighbours.add(n2);
		}
		return neighbours;
	}
	

	
	public void draw(Graphics g, Location origin, double scale) {
		Point p = loc.asPoint(origin, scale);
		g.fillOval(p.x-1, p.y-1, 2, 2);
	}
	
	public String toString() {
		Set<String> adjRoads = new HashSet<String>();
		for(Segment s : segments) {
			adjRoads.add(s.getRoad().getLabel());
		}
		return "ID: " + id + "\nRoads:" + adjRoads.toString();
	}

	/**
	 * 
	 * @param goal
	 * @return distance to the given node
	 */
	public double distanceTo(Node goal) {
		return loc.distance(goal.getLoc());
	}
}
