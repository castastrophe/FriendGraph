package graph;


public class Edge{
	Person from, to;
	int length;
	boolean visited, altvisit;
	
	public Edge(Vertex f, Vertex t){
		from = (Person) f;
		to 	 = (Person) t;
		
		if(f != null){ f.out.add(this); }
		if(t != null){ t.in.add(this);  }
		
		//length = (int)(Math.random()*10);
		visited = false;
	}
	
	public void show() {}
	public void unshow() {}
}
