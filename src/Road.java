import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a series of road segments
 * with a name and some other properties
 * @author Dylan
 *
 */
public class Road {

	private int id;
	private int type;
	private String label;
	private String city;
	boolean oneWay;
	private int speed;
	private int roadClass;
	private boolean cars;
	private boolean pedestrians;
	private boolean bicycles;
	private List<Segment> segments = new ArrayList<Segment>();
	
	public Road(int id, int type, String label, String city, boolean oneWay, int speed, int roadClass, boolean cars, boolean pedestrians, boolean bicycles) {
		this.id = id;
		this.type = type;
		this.label = label;
		this.city = city;
		this.oneWay = oneWay;
		this.speed = speed;
		this.roadClass = roadClass;
		this.cars = cars;
		this.pedestrians = pedestrians;
		this.bicycles = bicycles;
	}
	
	public String getLabel() {
		return label;
	}
	
	public List<Segment> getSegs(){
		return segments;
	}
	
	public void addSeg(Segment s) {
		segments.add(s);
	}
	
	public String toString() {
		return label;
	}
}
