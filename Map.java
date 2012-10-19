/*
 * Author: Cas Gentry
 * Last edited: 15 July '12
 * 
 * Purpose: This program is designed to demonstrate the
 * optimum path and traveling salesperson problem. Basis
 * of the data is sorted into a graph with vertices and edges -
 * friendships are edges, people are vertices.
 */
package graph;

import java.awt.*;
import java.io.*;
import java.util.*;

/* Map is the algorithmic foundation for this program */
public class Map extends Graph{
	/* keep track of people and relationships added */
	LinkedList <Person> people; //list of people on the map
	LinkedList <Friendship> friends; //list of friendships that exist
	
	protected static boolean costyr = true; //how is the cost value measured, years or intensity
	
	//global Color scale variable
	Color[] scale;
	
	//name of file with initial input
	String filename = "friendfile.txt";
					//"friendfile2.txt"; // use friendfile2 to test corrupted data

	//selected person is 'from' node, person2 is 'to' node
	Person selectedPerson=null;
	Person selectedPerson2=null;
	
	//true if the path algorithm has been run and is finished
	protected static boolean finished = false;
	
	public static void main(String[] args){ new Network(); }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map(Color[] s){
		scale = s; //collect color scale data
		people = (LinkedList)vertices; //people list is an extension of the vertex list
		friends = (LinkedList)edges;   //friends list is an extension of the edges list
		load(filename); //get data from a .txt file
	}

	//file format is: person     name     gender
	//file format is: friendship     from     to     year     intensity
	public void load(String fn){
		FileReader fr = null;
		//Read in the file, if it fails, print an error message
		try{ fr = new FileReader(fn); }
		catch(Exception e){ System.out.println("Map.load: File "+fn+" not found."); }
		
		//create a scanner, get next line
		Scanner sc = new Scanner(fr);
		
		Person v1 = null, v2 = null;
		while(sc.hasNext()){
			//read in the whole line and parse
			String line = sc.nextLine();
			//System.out.println("Map.load: about to process line: "+line);
			try{
				StringTokenizer st = new StringTokenizer(line);
			    String s = st.nextToken();
			    
			    if(s.equalsIgnoreCase("person")) {
			    	v2=v1; 
			    	people.add(v1 = new Person(st));
			    	//make sure another person isn't too close
			    	while(isNear(v1)){
			    		v1.x = v1.setCoords();
			    		v1.y = v1.setCoords();
			    	}  
			    }
				else if(s.equalsIgnoreCase("friendship")){ 
			    	String fromPerson = st.nextToken(); //get the name
					Person f 		  = findPerson(fromPerson); //find the person on the map
					String toPerson   = st.nextToken(); //get the second name
					Person t 		  = findPerson(toPerson); //find that person on the map
					
					int y = Integer.parseInt(st.nextToken()); //get year friendship initiated
					//check that a different year doesn't already exist in similar friendship
					y = checkYear(y, t, f);
					
					int i = Integer.parseInt(st.nextToken()); //get intensity rating
					
					/* create the new friendship object */
					Friendship f1;
					friends.add(f1 = new Friendship(f, t, y, i)); //add to list
					f1.bg = scale[f1.intensity - 1]; //send it's intensity color
				}
			}
			catch(Exception e){ 
				System.out.println("Map.load: There might be a problem with input file format."); 
			}
		}
		
		selectedPerson = v1; 
		selectedPerson2 = v2;
	}
	
	//find a Person object by the provided String name, return Person
	//used when adding friendships
	public Person findPerson(String target){
		Person ret = null;
		Iterator<Person> it = people.iterator(); //go through all listed people
		while(it.hasNext() && ret == null){
			Person p = it.next();
			 //if the names match, return that person
			if(p.name.equalsIgnoreCase(target)){ ret = p; }
		}
		
		//if the person isn't found, make them
		if(ret == null){ people.add(ret = new Person(target, null)); }
		
		return ret;
	}
	
	//find a friendship object by the provided 2 people involved, return Friendship
	public Friendship findFriendship(Person from, Person to){
		Friendship ret = null;
		Iterator<Friendship> it = friends.iterator();
		while(it.hasNext() && ret == null){
			Friendship f = it.next();
			if(f.from == from && f.to == to){ ret = f; }
		}
		
		return ret;
	}
	
	//find the person near this click and set it to selectedPerson
	public Person click(int x, int y){
		Iterator <Person> it = people.iterator();
		while (it.hasNext()){
			Person p = it.next();
			if(p.zatyou(x,y)){ selectedPerson2 = selectedPerson; selectedPerson = p; }
		}
		return selectedPerson;
	}
	
	//check to see if a Person too close to another Person
	//send the person object
	public boolean isNear(Person per){
		//get this person's coordinates
		int x = per.x,
			y = per.y;
		boolean ret = false; //initialize to false

		//check against all existing people
		Iterator<Person> it = people.iterator();
		while(it.hasNext()){
			Person p = it.next();
			if(p != per){ //make sure this is not the same person
				if(p.x > (x-100) && p.x < (x+100) && p.y > (y-100) && p.y < (y+100)){
					ret = true; 
				}
			}
		}
		
		return ret;
	}
	
	public boolean makeFriend(Person fromP, Person toP){
		if(fromP==null || toP==null){
			//get 2 random people
			int numFriends = people.size();
			int index1 = (int) (Math.random()*numFriends);
			int index2 = (int) (Math.random()*numFriends);
			while(index1 == index2){
				index1 = (int) (Math.random()*numFriends);
				index2 = (int) (Math.random()*numFriends);
			}
			
			fromP = people.get(index1);
			toP = people.get(index2);
		}
		
		boolean doFriend = true;
		Iterator <Friendship> it = friends.iterator();
		while (it.hasNext()){
			Friendship f = it.next();
			if (f.to==toP && f.from==fromP){ doFriend = false; }
		}
		
		if (doFriend){
			int i = (int)(Math.random()*10);
			int y = (int)((Math.random()*(2011-1960))+1960);
			y = checkYear(y, toP, fromP);
			
			Friendship f = new Friendship(fromP, toP, y, i);
			f.bg = scale[f.intensity-1];
			friends.add(f);
		}
		else{
			System.out.println("Friendship already exists.");
		}
		
		return doFriend;
	}
	
	public int checkYear(int y, Person p1, Person p2){
		Friendship other = findFriendship(p1, p2);
		if(other!=null){ if(other.year < y){ y = other.year; } }
		return y;
	}
	
	/* find the SHORTEST path between the two marked people,
	 * color it red, use Edge.length to measure 'distance' */
	public double shortPath(){
		clearVisits();
		selectedPerson.lengthToTarget = 0;
		selectedPerson.takeThis = null;
		
		int numberOfPeople = vertices.size();
		 
		for(int i=0; i<numberOfPeople; i++){
			Iterator <Person> it = people.iterator();
			while (it.hasNext()){
				Person p = it.next();
				p.setPointerTo(selectedPerson);
			}
		}
		
		selectedPerson2.visitShorties();
		return selectedPerson2.lengthToTarget;
	}
	
	/* go through all pairs of people  *
	 * add the friendship between them *
	 * if they don't already exist     */
	public void doAllFriends(){
		Iterator <Person> it = people.iterator();
		while(it.hasNext()){
			Person p = it.next();
			Iterator <Person> it2 = people.iterator();
			while(it2.hasNext()){
				Person p2 = it2.next();
				if(p != p2){
					Friendship exist = findFriendship(p, p2);
					if(exist == null){ makeFriend(p, p2); }
				}
			}
		}
	}
	
	boolean sale = false; //tells paint to do traveling salesman
	public void salesBeing(){ sale = true; }
	
	//solve the traveling salesman problem from selectedPerson
	public void salesBeing(Graphics g){
		Friendship.g = g;
		System.out.println("Traveling salesman solution starting...");
		clearVisits(); //start with a clean slate
		
		Vertex.home = selectedPerson;
		Vertex.bestDistance = 1000000; //infinity to start
		Path p = selectedPerson.salesBeing(0,0);
		//show the result
		if(p!=null){ 
			p.visit(); 
			Network.salesLength = Vertex.bestDistance;
			System.out.print("Path found. ");
		}
		else{
			Network.salesLength = 0;
			System.out.print("There is no path. ");
		}
		
		System.out.println("Salesbeing complete. \n");
		finished = true;
	}
	
	//put the people and friendships on the map
	public void paint(Graphics g){
		if(sale){ sale= false; salesBeing(g); }
		
		Iterator <Friendship> itF = friends.iterator();
		while(itF.hasNext()){
			Friendship f = itF.next();
			f.drawMe(g);
		}
		
		Iterator <Person> itP = people.iterator();
		while(itP.hasNext()){
			Person p = (Person)itP.next();
			p.drawMe(g);
		}

		g.setColor(Color.red);
		if ( selectedPerson!=null ){ g.drawOval( selectedPerson.x-9, selectedPerson.y-9, 20 , 20 ); }
		if ( selectedPerson2!=null ){ g.drawRect( selectedPerson2.x-9, selectedPerson2.y-9, 20 , 20 ); }
	}
	
	public void updateCost(){
		Iterator <Friendship> itF = friends.iterator();
		while(itF.hasNext()){
			Friendship f = itF.next();
			if(Map.costyr){
				f.length = Calendar.getInstance().get(Calendar.YEAR) - f.year;
			}
			else{
				f.length = f.intensity;
			}
		}
	}
}
