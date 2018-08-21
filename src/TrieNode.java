import java.util.*;

public class TrieNode {
	List<Road> roads = new ArrayList<Road>();
	Character c;
	Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();
	
	TrieNode(char c){
		this.c = c;
	}
	
	TrieNode(){
		c = null;
	}
	
	public void add(char[] word, Road road) {
		TrieNode temp = this; //Start from this node
		for(char c: word) {
			if(temp.children.get(c) == null) { temp.children.put(c, new TrieNode(c));}// If there's no node make the node
			
			temp = temp.children.get(c); //Move down tree
		}
		temp.roads.add(road); // add the road to the final node
	}
	
	public List<Road> get(char[] word) {
		TrieNode temp = this; //Start from this node
		
		for(char c: word) {
			if(temp.children.get(c) == null) { //next char isnt there
				return null;
			}
			temp = temp.children.get(c); //move down tree
		}
		
		return temp.roads;
	}
	
	public List<Road> getAll(char[] prefix) {
		List<Road> results = new ArrayList<Road>();
		TrieNode temp = this;
		
		for(char c : prefix) {
			if(temp.children.get(c) == null) {
				return null;
			}
			temp = temp.children.get(c);
		}
		getAllFrom(temp, results);
		return results;
	}
	
	public void getAllFrom(TrieNode node, List<Road> results) {
		results.addAll(node.roads);
		for(TrieNode child: node.children.values()) {
			getAllFrom(child, results);
		}
	}
	
}
