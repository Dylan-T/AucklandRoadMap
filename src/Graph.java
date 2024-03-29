import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
/**
 * A class to store a road graph
 * @author Dylan
 *
 */
public class Graph {
	Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
	Set<Segment> segList = new HashSet<Segment>();
	Map<Integer, Road> roadMap = new HashMap<Integer, Road>();
	TrieNode searchTrie = new TrieNode();
	Set<Node> APs = new HashSet<Node>();
	
	public Graph(File nodes, File segs, File roads){
		try {
			/* ----- Create Readers ------*/
			BufferedReader bNode = new BufferedReader(new FileReader(nodes));
			BufferedReader bRoad = new BufferedReader(new FileReader(roads));
			BufferedReader bSeg = new BufferedReader(new FileReader(segs));
			
			/* ----- Load Nodes ------*/
			String line;
			while((line = bNode.readLine()) != null) {
				String[] tokens;
				tokens = line.split("	");
				nodeMap.put(Integer.parseInt(tokens[0]) ,new Node(Integer.parseInt(tokens[0]), Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])));
			}
			
			/* ----- Load Roads ------*/
			bRoad.readLine(); // Skip first line
			while((line = bRoad.readLine()) != null) {
				String[] tokens;
				tokens = line.split("	");
				roadMap.put(Integer.parseInt(tokens[0]), new Road(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), tokens[2], tokens[3], tokens[4] == "0", Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), tokens[7] == "0", tokens[8] == "0", tokens[9] == "0"));
			}
			for(Road r: roadMap.values()) {
				searchTrie.add(r.getLabel().toCharArray(), r);
			}
			
			
			/* ----- Load Segments ------*/
			bSeg.readLine();// Skip first line
			while((line = bSeg.readLine()) != null) {
				
				String[] tokens = line.split("	");
				List<Location> coords = new ArrayList<Location>();
				//Create the locations List
				for(int i = 4; i < tokens.length - 1; i +=2) {
					coords.add(Location.newFromLatLon(Double.parseDouble(tokens[i]), Double.parseDouble(tokens[i+1])));
				}
				Node fromNode = getNode(Integer.parseInt(tokens[2]));
				Node toNode = getNode(Integer.parseInt(tokens[3]));
				Road tempRoad = getRoad(Integer.parseInt(tokens[0]));
				Segment tempSeg = new Segment(tempRoad, Double.parseDouble(tokens[1]), fromNode, toNode, coords);
				segList.add(tempSeg);
				
				// Add this segment to the Corresponding Nodes & road
				tempRoad.addSeg(tempSeg);
				fromNode.addSeg(tempSeg);
				toNode.addSeg(tempSeg);
			}
			for(Node n: getNodes()) {
				if(n.count == Integer.MAX_VALUE)getArticulationPoints(n);
			}
			System.out.println(APs.size());
			bNode.close();
			bRoad.close();
			bSeg.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Initialize an empty graph
	 */
	public Graph() {}
	
	public Collection<Node> getNodes(){
		return nodeMap.values();
	}
	
	public Collection<Road> getRoads(){
		return roadMap.values();
	}
	
	public Set<Segment> getSegs(){
		return segList;
	}
	
	public Node getNode(int id){
		return nodeMap.get(id);
	}
	
	public Road getRoad(int id) {
		return roadMap.get(id);
	}
	
	/**
	 * Returns a location object that represents the origin (NW most position)
	 * @return
	 */
	public Location getOrigin() {
		double x = Double.MAX_VALUE;
		double y = Double.MIN_VALUE;
		
		for(Node n: getNodes()) {
			if(n.getX() < x) x = n.getX();
			if(n.getY() > y) y = n.getY();
		}
		
		return new Location(x,y);
	}
	
	/**
	 * returns the node closest to the given location
	 * @param l
	 * @return
	 */
	public Node getClosestNode(Location l) {
		double minDistance = Integer.MAX_VALUE;
		Node closest = null;
		for(Node n : getNodes()) {
			if(n.getLoc().distance(l) < minDistance) {
				minDistance = n.getLoc().distance(l);
				closest = n;
			}
		}
		return closest;
	}

	public TrieNode getSearchTrie() {
		return searchTrie;
	}

	public double getGraphWidth() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for(Node n: getNodes()) {
			if(n.getLoc().x < min) min = n.getLoc().x;
			if(n.getLoc().x > max) max = n.getLoc().x;
		}
		return max - min;
	}
	
	/**
	 * This method returns the shortest path between the start and goal nodes
	 * @param start
	 * @param goal
	 * @return
	 */
	public List<Segment> getShortestPath(Node start, Node goal){
		Set<Node> visited = new HashSet<Node>();
		PriorityQueue<SearchElement> fringe = new PriorityQueue<SearchElement>(new Comparator<SearchElement>() {
			public int compare(SearchElement i, SearchElement j) {
				if(i.f > j.f) return 1;
				else if(j.f > i.f)return -1;
				else return 0;
		}});
		
		fringe.add(new SearchElement(start, goal));// add starting node
		boolean found = false;
		SearchElement current = fringe.peek();
		while(!fringe.isEmpty() && !found) {
			current = fringe.poll();
			visited.add(current.node);
			
			if(current.node.equals(goal)) { //Reached goal
				found = true;
			}
			
			
			for(Segment s: current.node.getSegs()) {//Finding all neighbours
				if(!(s.getRoad().oneWay && s.getTo() == current.node)) { //Check for oneway
					Node cNode;
					if(s.getTo() == current.node && !s.getRoad().oneWay) cNode = s.getFrom();
					else cNode = s.getTo();
					if(!visited.contains(cNode) ) {
						SearchElement child = new SearchElement(cNode, current, s, goal);
						fringe.add(child);
					}
				}
			}
		}
		List<Segment> path = new ArrayList<Segment>();
		if(found) {
			for(SearchElement s = current; s.seg != null; s = s.prev) {//Add path to list
				path.add(s.seg);
			}
		}
		Collections.reverse(path);
		return path;//return the short path
	}
	
	private class SearchElement{
		Node goal;
		Node node;
		Segment seg;
		SearchElement prev;
		double g;
		double h;
		double f; //Cost + estimate
		
		/**
		 * constructor for all elements but the first
		 * @param node
		 * @param prev
		 * @param seg
		 * @param goal
		 */
		public SearchElement(Node node, SearchElement prev, Segment seg, Node goal) {
			this.node = node;
			this.prev = prev;
			this.seg = seg;
			this.goal = goal;
			g = seg.getLength() + prev.getCost();
			h = getEstimate();
			f =  g + h;
		}
		
		/**
		 * Constructor for the first element
		 * @param n
		 * @param goal
		 */
		public SearchElement(Node node, Node goal) {
			this.node = node;
			this.goal = goal;
			g = 0;
			h = getEstimate();
			f = g + h;
		}
		
		double getCost(){
			return g;
		}
		
		double getEstimate() {
			return node.distanceTo(goal);
		}
	}

	public void getArticulationPoints(Node root) { //240 Small && 10853 Large
		root.count = 0;
		root.reachBack = 0;
		root.parent = null;
		int numSubTrees = 0;
		for(Node n: root.getNeighbours()){
			if(n.count == Integer.MAX_VALUE){
				iterArtPts(n,1,root);
				numSubTrees++;
			}
		}
		if(numSubTrees > 1) {
			APs.add(root);
		}
	}
	
	public void iterArtPts(Node firstNode, int count, Node parent) {
		Stack<Node> stack = new Stack<Node>();
		firstNode.parent = parent;
		stack.push(firstNode);
		Node n;
		while(!stack.isEmpty()) {
			n = stack.peek();
			if(n.count == Integer.MAX_VALUE){
				n.count = n.parent.count+1;
				n.reachBack = n.parent.count+1;
				
				for(Node temp: n.getNeighbours()) {
					if(temp != parent) n.children.add(temp);
				}
			
			}else if(!n.children.isEmpty()){
				Node child = n.children.remove(0);
				if(child.count < Integer.MAX_VALUE)
					n.reachBack = Math.min(child.count, n.reachBack);
				else {
					child.parent = n;
					stack.push(child);
				}
			}
			else{
				if(n != firstNode){
					n.parent.reachBack = Math.min(n.reachBack, n.parent.reachBack);
					if(n.reachBack >= n.parent.count) APs.add(n.parent);
				}
				stack.pop();
			}
		}
	}
	
}
