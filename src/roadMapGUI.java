import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class creates the GUI and implements its functionality
 * @author Dylan
 *
 */
public class roadMapGUI extends GUI {
	Graph graph = new Graph(); // Graph storing data
	Location origin = new Location(0,0);
	double scale = 1;
	List<Road> selectRoad = new ArrayList<Road>();
	List<Node> selectNodes = new ArrayList<Node>();
	List<Segment> shortPath = new ArrayList<Segment>();
	List<Polygon> polygons = new ArrayList<Polygon>();
	
	public roadMapGUI(){
		
	}
	
	
	@Override
	protected void redraw(Graphics g) {
		for(Polygon p : polygons) {
			p.draw(g, origin, scale);
		}
		g.setColor(Color.BLACK);
		for(Node n : graph.getNodes()){
			n.draw(g, origin, scale);
		}
		for(Segment s: graph.getSegs()) {
			s.draw(g, origin, scale);
		}
		g.setColor(Color.GREEN);
		for(Node n: graph.APs) {
			n.draw(g, origin, scale);
		}
		g.setColor(Color.BLUE);
		for(Road r : selectRoad) {
			for(Segment s : r.getSegs()) {
				s.draw(g, origin, scale);
			}
		}
		g.setColor(Color.RED);
		for(Node n : selectNodes){
			n.draw(g, origin, scale);
		}
		for(Segment s: shortPath) {
			s.draw(g, origin, scale);
		}
		g.setColor(Color.BLACK);
	}

	@Override
	protected void onClick(MouseEvent e) {
		
		if(selectNodes.size() == 2) {//Max two nodes can be selected
			shortPath.clear();
			selectNodes.clear(); 
		}
		Point cP = e.getPoint();
		Location cL = Location.newFromPoint(cP, origin, scale);
		double minDist = Double.MAX_VALUE;
		
		//Find selected Node
		Node selectNode = null;
		for(Node n: graph.getNodes()) {
			double distTemp = n.getLoc().distance(cL);
			if(distTemp < minDist) {
				minDist = distTemp;
				selectNode = n;
			}
		}
		selectNodes.add(selectNode);
		getTextOutputArea().setText(selectNodes.get(selectNodes.size()-1).toString());
		if(selectNodes.size() == 2) {
			String message = "";
			for(Node n : selectNodes) {
				message += n.toString() + "\n\n";
			}
			shortPath = graph.getShortestPath(selectNodes.get(0), selectNodes.get(1));
			
			double lenTot = 0;
			double lenRoad = 0;
			List<Road> roads = new ArrayList<Road>();
			int i = 0;
			
			while(i < shortPath.size()) {
				Segment s = shortPath.get(i);
				Road road = s.getRoad();
				roads.add(road);
				while(s.getRoad().getLabel().equals(road.getLabel()) && i < shortPath.size()) {
					lenRoad += s.getLength();
					lenTot += s.getLength();
					i++;
					if(i < shortPath.size())s = shortPath.get(i);
				}
				message += road.getLabel() + ": " + String.format("%.3f", lenRoad) + "km\n";
				lenRoad = 0;
			}
			message += "Total Distance: " + String.format("%.3f",lenTot);
			getTextOutputArea().setText(message);
		}
	}

	@Override
	protected void onSearch() {
		TrieNode search = graph.getSearchTrie();
		List<Road> get = search.get(getSearchBox().getText().toCharArray());
		if(get == null) {return;}
		if(get.size() == 1) { selectRoad = get;}
		else {
			selectRoad = search.getAll(getSearchBox().getText().toCharArray());
		}
	}

	@Override
	protected void onMove(Move m) {
		double shiftDist = 30;
		double zoomFactor = 1.1;
		Location botRight = Location.newFromPoint(new Point((int)getDrawingAreaDimension().getWidth(), (int)getDrawingAreaDimension().getHeight()), origin, scale);
		Location topLeft = Location.newFromPoint(new Point(0,0), origin, scale);
		double width = botRight.x - topLeft.x;
		double height = botRight.y - topLeft.y;
		double dx = 0;
		double dy = 0;
		switch(m) {
			case NORTH:
				dy = shiftDist/scale;
				break;
	
			case EAST:
				dx = shiftDist/scale;
				break;
	
			case SOUTH:
				dy = -shiftDist/scale;
				break;
	
			case WEST:
				dx = -shiftDist/scale;
				break;
	
			case ZOOM_IN:
				scale *= zoomFactor;
				dx = ((width - (width / zoomFactor))/2);
				dy = ((height - (height / zoomFactor))/2);
				break;
	
			case ZOOM_OUT:
				if(scale > 1) {
					scale /= zoomFactor;
					dx = ((width - (width * zoomFactor))/2);
					dy = ((height - (height * zoomFactor))/2);
				}
				break;
		}
		origin = origin.moveBy(dx, dy);

	}

	

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		graph = new Graph(nodes, segments, roads);
		origin = graph.getOrigin();
		scale = ( getDrawingAreaDimension().getWidth() / graph.getGraphWidth());
		this.polygons = new ArrayList<Polygon>();
		// Load polygons
		if(polygons != null)
		try {
			BufferedReader bPoly = new BufferedReader(new FileReader(polygons));
			String line = "";
			String[] tokens;
			while((line = bPoly.readLine()) != null) {
				if(line.equals("[POLYGON]")) {
					//Polygon Variables
					String type = "";
					int endLevel = 0;
					int cityIdx = 0;
					String label = "";
					List<List<Location>> data0 = new ArrayList<List<Location>>();
					//Parse polygon info
					while(!(line = bPoly.readLine()).equals("[END]")) {
						tokens = line.split("=");
						switch (tokens[0]) {
							case "Type":
								type = tokens[1];
								break;
							
							case "EndLevel":
								endLevel = Integer.parseInt(tokens[1]);
								break;
							
							case "CityIdx":
								cityIdx = Integer.parseInt(tokens[1]);
								break;
								
							case "Label":
								label = tokens[1];
								break;
								
							case "Data0":
								List<Location> tempList = new ArrayList<Location>();
								//Remove brackets
								tokens[1] = tokens[1].replace("(", "");
								tokens[1] = tokens[1].replace(")", "");
								//add Locations
								String[] dataStr = tokens[1].split(",");
								for(int i = 0; i < dataStr.length; i=i+2) {
									tempList.add(Location.newFromLatLon(Double.parseDouble(dataStr[i]), Double.parseDouble(dataStr[i+1])));
								}
								data0.add(tempList);
								break;
						}
					}
					//Make the polygon
					this.polygons.add(new Polygon(type, endLevel, cityIdx, label, data0));
				}
				
			}
			bPoly.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		new roadMapGUI();
	}
}
