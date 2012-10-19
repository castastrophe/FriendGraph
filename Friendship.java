package graph;

import java.awt.*;
import java.util.Calendar;

import javax.swing.*;

//Friendships should have the year it started & a number indicating intensity 
//where 1 is the most intense and 10 is not
public class Friendship extends Edge{
	static protected Map theMap;
	static protected Graphics g;
	static protected boolean hide = false;
	
	Person p1, p2;
	int year; //cannot be earlier than 1987 or later than 2011
	int intensity; //# between 1 and 10
	Color bg;
	
	public static void main(String[] args){ new Network(); }

	public Friendship(Person fp, Person tp, int yr, int in){
		super(null, null);
		
		intensity = in;
		year = yr;
		
		from = fp;
		from = (Person)from;
		from.out.add(this);
		
		to = tp;
		to = (Person)to;
		to.in.add(this);
		
		//set bounds for years known, between 1900 and 2011
		if(year < 1900 || year > 2011){ 
			year = Integer.parseInt(JOptionPane.showInputDialog("When did "+from.name+" meet "+to.name+"?"));
		}
		
		//set bounds for the intensity - between 0-10
		if(intensity < 1)		{ intensity = 1;  }
		else if(intensity > 10) { intensity = 10; }
		
		//add logic here to let user pick which length they want to measure
		//length = intensity;
		if(Map.costyr){
			length = Calendar.getInstance().get(Calendar.YEAR) - year;
		}
		else{
			length = intensity;
		}
	}
	
	//map the friendship on the main panel
	public void drawMe(Graphics g){
		Person p1 = (Person) from;
		Person p2 = (Person) to;
		   
		//if optimum path has visited this relationship, set the line to red
		if (visited || altvisit){ g.setColor(Color.RED); }
		else{ g.setColor(bg); } //else, use it's intensity color
		
		g.drawLine(p1.x, p1.y, p2.x, p2.y); //draw the line from person1 to person2
		
		if(visited && !altvisit){ g.setColor(Color.RED); }
		else{ g.setColor(bg); }
		
		int arrow[][] = drawArrow(p1, p2); //get the dimensions for the directional arrow
		g.fillPolygon(arrow[0], arrow[1], 3); //draw the arrow
		
		//if it's been visited, make the inverse relationship also red so it's easier to see
		if(visited){
			//this will help to make the path more clearly visible
			Friendship f = theMap.findFriendship(p2, p1);
			if(f != null){ f.altvisit = true; }
		}
	
		//if the year is not hidden, draw it on the map
		if(!hide){
			//get the center point of the line
			int detailX = (p1.x+p2.x)/2, 
			detailY = (p1.y+p2.y)/2;
			
			if(altvisit){ g.setColor(Color.RED); }
			g.fillRoundRect(detailX-5, detailY-15, 38, 20, 2, 2);
			g.setColor(Color.BLACK);
			
			if(!visited){
				//if the background is dark, text is white
				if(intensity > 5){ g.setColor(Color.WHITE); }
			}
			
			g.drawString(year+"", detailX, detailY);
		}
	}

	public int[][] drawArrow(Person p1, Person p2){
		int results[][] = new int[2][3];
		double phi = Math.toRadians(35), barb=12;
		double dy = p2.y - p1.y;
		double dx = p2.x - p1.x;
		double theta = Math.atan2(dy, dx);
		double rho = theta + phi;
		int[] x = new int[3];
		int[] y = new int[3];
		x[2]=p2.x; y[2]=p2.y;
		for(int j = 0; j < 2; j++){   
            x[j] = (int)(p2.x - barb * Math.cos(rho));   
            y[j] = (int)(p2.y - barb * Math.sin(rho));
            rho = theta - phi;
        }
		
		results[0] = x;
		results[1] = y;
		return results;
	}
	
	public void show(){
		Person a1 = (Person) from;
		Person a2 = (Person) to;
		g.setColor(Color.black);
		g.drawLine(a1.x, a1.y, a2.x, a2.y);
	}

	public void unshow(){
		Person a1 = (Person) from;
		Person a2 = (Person) to;
		g.setColor(Color.white);
		g.drawLine(a1.x, a1.y, a2.x, a2.y);
	}
	
	 // access 
	 static public void setTheMap(Map m){ theMap = m; }
}
