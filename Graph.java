package graph;

import java.util.*;

public class Graph {
	LinkedList <Vertex> vertices;
	LinkedList <Edge> edges;
	
	public Graph(){
		vertices = new LinkedList <Vertex>();
		edges 	 = new LinkedList <Edge>();
	}
	
	//add v to the list of vertices
	public void add(Vertex v){
		vertices.add(v);
	}
	
	// clears all of the visited flags on all the nodes
	public void clearVisits(){
		Iterator <Vertex> it = vertices.iterator();
		while (it.hasNext()){
			Vertex v = it.next();
			v.visited = false;
			v.lengthToTarget = 1000000;
		}
		
		Iterator <Edge> jt = edges.iterator();
		while(jt.hasNext()){
			Edge e = jt.next();
			e.visited = false;
			e.altvisit = false;
		}
	}
	
}
