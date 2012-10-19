package graph;

import java.awt.*;
import java.util.*;
import javax.swing.*;

// Person should have name, sex, address, and xy coordinates in pixels
public class Person extends Vertex{
	//declare details of a person
	int x, y; //location of person in pixels from upper left
	String name, address;
	char sex;
	Map theMap;

	public static void main( String[] args ) { new Network(); }
	
	//this method creates a new person based on the input
	public Person(StringTokenizer st){
		super("");
		
		//initialize to null so we can query if info is missing
		String n = null, gen = null;
		//file format: token[1]=name token[2]=gender token[+]=address
		int limit = st.countTokens(); //determine how many inputs there are first
		for(int i=1; i<limit+1; i++){
			if		(i == 1){ n = st.nextToken(); }
			else if	(i == 2){ gen = st.nextToken();  }
		}
		
		newPerson(n, gen);
	}
	
	public Person(String n, String s){
		super(n);
		newPerson(n, s);
	}

	//declare person class with inputs name, gender
	public void newPerson(String n, String s){
		name = n; //entry has to have a name
		
		//check if the gender has been set correctly
		if(s != null && (s.toUpperCase().charAt(0)=='F' || 
			s.toUpperCase().charAt(0)=='M')){
			sex = s.charAt(0); 
		}
		//if it isn't, ask the user for the person's gender
		else{ sex = querySex(); }
		
		//set random x,y coordinates
		x = setCoords();
		y = setCoords();
	}
	
	//ask user for the sex of the person
	public char querySex(){		
		Object[] options = {"Male", "Female"};
		int x = JOptionPane.showOptionDialog(null, "Please enter "+name+"'s gender:", " ",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 
				options[1]);
		if(x == 0){ return 'M'; }
		else{ return 'F'; }
	}
	
	//set random coordinates
	public int setCoords(){
		Random r = new Random();
		return r.nextInt(420)+40;
	}
	
	public boolean zatyou(int mx, int my){ return (mx-10<x && x < mx+10 && my-10<y && y<my+10 ); }
	
	// finds a path to the target from this node and returns that path.
	// Returns null if there is not a path.
	public Path findPath(Person target){
		Path thePath=null;
		visited = true;
		   
		Iterator<Edge> it = out.iterator();
		while(thePath==null &&  it.hasNext()){
			Friendship f = (Friendship)(it.next());
			Person p 	 = (Person)(f.to);
			if(p!=null && !p.visited ){ // a should never be null 
				if(p==target ){ thePath = new Path(); }
				else 		  { thePath = p.findPath(target); }
				
				if (thePath!=null) { thePath.edges.addFirst(f); f.visited=true; }
			   }
		   }
		   
		   return thePath;
	   }
	
	
	public void drawMe(Graphics g){
		//boys are blue
		if(sex == 'M'){ g.setColor(Color.BLUE); }
		//girls are pink
		else if(sex == 'F'){ g.setColor(new Color(255,105,180)); }
		else{ g.setColor(Color.BLACK); }
		
		//write the person's name
		g.drawString(name, x+2, y-2);
		g.fillOval(x-1, y-1, 5, 5);
	}
}