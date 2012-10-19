package graph;

import java.util.*;

public class Path{
   protected LinkedList <Edge> edges; // ordered list of edges from 'from' to 'to'.
   protected Vertex from; // may not be defined
   protected Vertex to;  // may not be defined
   protected double length;
   
   public Path(){
	   edges = new LinkedList <Edge> ();
	   length = 0;
   }
   
   //use this add, so we keep track of length too
   public void add(Edge e){
	   edges.addFirst(e);
	   length += e.length;
   }
   
   public Path(Vertex f1, Vertex t1){
	   from = f1;
	   to = t1;
	   edges = new LinkedList <Edge> ();
   }
   
   // visit all of the edges on this path
   public void visit(){
	   Iterator <Edge> it = edges.iterator();
	   while (it.hasNext()){
		   Edge e = it.next();
		   e.visited = true;
	   }
   }
}
