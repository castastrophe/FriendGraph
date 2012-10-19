package graph;

import java.util.*;

public class Vertex{
	protected String name;
    protected LinkedList <Edge> out; //all relationships you have with people
    protected LinkedList <Edge> in; //all relationships others have with you
    protected boolean visited = false;
    protected Edge takeThis;
    protected double lengthToTarget;
    protected static Vertex home;
    protected static double bestDistance;
   
    public Vertex(String n){
    	name = n;
    	out = new LinkedList <Edge> ();
    	in = new LinkedList <Edge> ();
    }
    
    //mark as visited every other vertex that you can get to from here
    public void connect(){
    	if(!visited){
	    	visited = true;
	    	Iterator <Edge> it = out.iterator();
	    	while(it.hasNext()){
	    		Edge f = it.next();
	    		f.to.connect();
	    	}
    	}
    }
    
    // look at all outgoing edges, see if any of the vertices on the 
    // other end of those report a way to get to the target.
    // Pick the best (least total length) way to get to target and set
    // pointerTo and lengthToTarget.
    public boolean setPointerTo(Vertex target){
    	boolean ret = false;
    		
    	Iterator <Edge> it = out.iterator();
    	while(it.hasNext()){
    		Edge e = it.next();
    		Vertex v = e.to;
    		if (v.lengthToTarget + e.length < lengthToTarget){
    			lengthToTarget = v.lengthToTarget + e.length;
    			takeThis = e;
    		}
    	}
    	
    	return ret;
    }
    
    // mark all vertices and edges from here to target as 'visited'.
    // (follow the takeThis edge at every point)
    // mark THIS node visited, mark takeThis edge visited (if not null)
    // call visitShorties on the Vertex on the other end of the takeThis edge.
    public void visitShorties(){
    	visited = true;
    	if(takeThis!=null){
    		takeThis.visited = true;
    		takeThis.to.visitShorties();
    	}
    }
    
    // find a path from this airport to home using all of the unvisted airports
    // If there are no unvisited nodes, then return new path which is flight back
    // to the starting Vertex.
    public Path salesBeing(int level, double sofar){
    	Path ret = null;
    	if(sofar > bestDistance){ ret = new Path(); ret.length = 1000000; }
    	else{
	 	   visited = true;
	 	   
	 	   Iterator <Edge> it =  out.iterator();
	 	   while (it.hasNext()){
	 		   Edge e = it.next();
	 		   Vertex a = e.to;
	 		   if(!a.visited){
	 			   e.show();
	 		       Path p = a.salesBeing(level+1, sofar+e.length);
	 		       if (p == null){ break; }
	 		       if(ret==null || p.length + e.length < ret.length){ // first or better then keep
	 		    	   ret = p;
	 		    	   p.add(e);
	 		       }
	 		       
	 		       e.unshow();
	 		   }
	 	   }
    	}
		
    	if (ret==null){// no unvisited vertices, must be time to go home
			Edge e = edgeHome();
			if(e != null){
				ret = new Path();
				ret.add(e);
				double dist = sofar + e.length;
				if(dist < bestDistance){ bestDistance = dist; }
			}
		}
	
 	   visited = false;
 	   return ret;
	}

    //find and return an Edge from here to home, directly
    public Edge edgeHome(){
    	Edge ret=null;
    	Iterator <Edge> it = out.iterator();
    	while(ret==null && it.hasNext()){
    		Edge e = it.next();
    		if(e.to == home) { ret = e; }
    	}
    	return ret;
    }
}
