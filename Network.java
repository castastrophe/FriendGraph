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
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

@SuppressWarnings("serial")
/* Network is the visual foundation for this program
 * and the program's point of entry, this builds the UI */
public class Network extends JFrame implements ActionListener{
	/* Must create a relationship between the visual framework
	 * and the Map class which is the foundation for the algorithm */
	Map theMap; //connect to the Map class
	
	/* salesLength holds the value of the total distance traveled by the
	 * traveling salesman algorithm to reach every node; updated by Path */
	protected static double salesLength;
	
	/* to allow users to create random people on the fly,
	 * build 2 arrays of 5 names for each gender */
	String[] ranFP = {"Parker", "Leslie", "Trish", "Maria", "Hannah"};
	String[] ranMP = {"Carter", "Rick", "Walter", "Frank", "Oliver"};
	//keep track of which name was used last with a counter for male and female
	int fcount = 0, mcount = 0;
	
	//screen dimension standards
	int screenX = 800, screenY = 600, menuX=200, menuY=100;
	
	JMenuBar menuBar;
	JMenu menu, submenu;
	JRadioButtonMenuItem rbMenuItem;

	public static void main(String[] args){ new Network(); }
	
	public Network(){
		setSize(screenX,screenY);   	 		 // set the size of the frame
		setTitle("Graph-tastic by Cas"); 		 // set title of main frame
		setDefaultCloseOperation(EXIT_ON_CLOSE); // close program when frame is closed
		
		//Create the menu bar.
	    menuBar = new JMenuBar();

	    //Build the first menu.
	    menu = new JMenu("Cost");
	    menu.setMnemonic(KeyEvent.VK_A);
	    menu.setBackground(getBackground());
	    menu.getAccessibleContext().setAccessibleDescription(
	            "Set how the cost value is defined.");
	    menuBar.add(menu);

	    //a group of JMenuItems
	    ButtonGroup group = new ButtonGroup();
	    rbMenuItem = new JRadioButtonMenuItem("Years known");
	    rbMenuItem.setSelected(true);
	    rbMenuItem.setMnemonic(KeyEvent.VK_R); 
        rbMenuItem.addActionListener(this);
	    rbMenuItem.setActionCommand("costyears");
	    group.add(rbMenuItem);
	    menu.add(rbMenuItem);

	    rbMenuItem = new JRadioButtonMenuItem("Intensity");
	    rbMenuItem.setMnemonic(KeyEvent.VK_O);
	    rbMenuItem.addActionListener(this);
	    rbMenuItem.setActionCommand("costint");
	    group.add(rbMenuItem);
	    menu.add(rbMenuItem);
	    
	    add(menuBar, BorderLayout.NORTH);
	    
		add(new panel());  						 // call the class that builds the screen
		setVisible(true);  					 	 // show the frame
	}

	//this main screen is built on a JPanel object and uses mouse and action listeners
	public class panel extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
		//3 main panels divide the screen
		JPanel mainPanel; 	// screenX-menuX x screenY-menuY
		JPanel buttonPanel; // screenX-menuX x menuY
		JPanel keyPanel; 	// menuX x screenY-menuY
		
		/* ------------------------------ *
		 * |					|		| *
		 * |					|		| *
		 * |					|  key	| *
		 * |	main			|		| *
		 * |					|		| *
		 * |					|		| *
		 * ------------------------------ *
		 * |	button					| *
		 * ------------------------------ */
		
		/* Variables for the keyPanel */
		JTextField title; 	// describe the key
		JTextField[] scale; // print friendship scale
		JButton hideYear; 	// allow user to hide the years people met
		
		/* Variables for the buttonPanel */
		JButton	newPersonButton, 	// user create person manually
				newrandomPerson,	// system creates random person
				newrandomFriend,	// system assigns random friendship
				pathButton,			// find a path between 2 people
				connectAllFriends,	// connect all people with a friendship
				salesmanButton;		// traveling salesman algorithm
		JButton cost;  // the distance traveled between each person is printed here
		
		//set up standard colors for intensity rating
		Color[] iColors = {	Color.WHITE, 			new Color(255,250,205), new Color(255,255,0), 
							new Color(173,255,47), 	new Color(50,205,50), 	new Color(0,112,0),  
							new Color(85,107,47), 	new Color(95,158,160), 	new Color(70,130,180), 
							new Color(25,25,112)
						  };
		
		/* this constructor builds the objects on the main screen */
		public panel(){
			/* settings for the overall environment */
			setBackground(new Color(204,230,255));  // create standard background color
			
			/* Border layout works best for this set-up since we have one primary screen
			 * and 2 peripheral panels, CENTER, SOUTH, and EAST */
			setLayout(new BorderLayout()); 
			
			//this panel will hold the relationship graph
		    mainPanel = new JPanel();
		    mainPanel.setPreferredSize(new Dimension(screenX-menuX,screenY-menuY));
		    mainPanel.setBackground(getBackground()); // get the background from the main frame
		    //mainPanel.setLayout(new FlowLayout());  // layout isn't important here
		    add(mainPanel, BorderLayout.CENTER); 	  // add this to the main frame
		    
		    //build the map buttons panel
		    buttonPanel = new JPanel();
		    buttonPanel.setPreferredSize(new Dimension(screenX-menuX, menuY));
		    buttonPanel.setBackground(getBackground());
		    buttonPanel.setLayout(new GridLayout(1,7)); // 6 buttons, 1 result window
		    add(buttonPanel, BorderLayout.SOUTH); 		// add to the bottom of the main frame
			
		    //build the key panel
		    keyPanel = new JPanel();
		    keyPanel.setPreferredSize(new Dimension(menuX, screenY-menuY));
		    keyPanel.setBackground(getBackground());
		    keyPanel.setLayout(new GridLayout(12,1)); // 10 levels, 1 title, 1 year button
		    add(keyPanel, BorderLayout.EAST);		  // add to right of screen
		
	        /* Build the title for the table
	         * this will show the colors associated with the intensity ratings */
	        title = new JTextField();
	        title.setEditable(false);
	        title.setText("Friendship Intensity");
	        title.setBackground(getBackground());
	        title.setBorder(null);
	        title.setHorizontalAlignment(SwingConstants.CENTER);
	        title.setFont(new Font("Algerian",Font.BOLD,15));
	        keyPanel.add(title);
	    
	        // build the intensity ratings table
	        scale = new JTextField[iColors.length];
	        for(int i=0; i<scale.length; i++){//1 entry for every color
	        	scale[i] = new JTextField();
	        	scale[i].setBackground(iColors[i]);
	        	scale[i].setEditable(false);
	        	//as the colors get darker, text needs to be white
	        	if(i>4){ scale[i].setForeground(Color.WHITE); }

	        	// add start and end descriptions to describe scale 
	        	if		(i == 0)			 { scale[i].setText((i+1)+" = very intense");  }
	        	else if	(i==(scale.length-1)){ scale[i].setText((i+1)+" = least intense"); }
	        	else						 { scale[i].setText((i+1)+""); }
	        	
	        	scale[i].setHorizontalAlignment(JTextField.CENTER);
	        	keyPanel.add(scale[i]);
	        }
	        
	        // allow the user to hide the year tags to better view the graph
	        hideYear = makeButton("Hide Year", keyPanel);
	        
	        keyPanel.setVisible(true);
	        /* end key panel build */
	        
	        /* build the button panel */
	        newPersonButton = makeButton("<html><center>Create new person</center></html>", buttonPanel);
	        newrandomPerson = makeButton("<html><center>Create random person</center></html>", buttonPanel);
	        newrandomFriend = makeButton("<html><center>Create random friendship</center></html>", buttonPanel);
	        pathButton = makeButton("<html><center>Optimum path algorithm</center></html>", buttonPanel);
	        connectAllFriends = makeButton("<html><center>Connect<BR>everyone</center></html>", buttonPanel);
	        salesmanButton = makeButton("<html><center>Salesman algorithm</center></html>", buttonPanel);
	        
	        // disable the cost button since it only displays output
	        cost = new JButton("");
	        cost.setEnabled(false);
	        buttonPanel.add(cost);
	        
	        buttonPanel.setVisible(true);
	        /* end button panel build */
	        
	        mainPanel.addMouseListener(this);
	        mainPanel.addMouseMotionListener(this);
	        
	        //create a new map and send it the intensity scale colors
	        theMap = new Map(iColors);
	        Friendship.setTheMap(theMap);
	        	        
	        repaint(); // repaint the screen once everything is added
	        setVisible(true);
		}
		
		
		/* makes a button with 'name' on it and returns it,  
		 * also add it to the panel and attaches a listener */
		public JButton makeButton (String name, JPanel back){
			JButton jb = new JButton(name);
			back.add(jb);
			jb.addActionListener(this);
			return jb;
		}
		
		public void actionPerformed (ActionEvent e) {
            if      ( e.getSource()==newPersonButton  	){ makePerson(false); }
    		else if ( e.getSource()==newrandomPerson    ){ makePerson(true);  }
	    	else if ( e.getSource()==newrandomFriend    ){ makeFriend(true);  }
	    	else if ( e.getSource()==pathButton         ){ doPath(); 		  }
	    	else if ( e.getSource()==connectAllFriends  ){ doAllFriends(); 	  }
	    	else if ( e.getSource()==salesmanButton     ){ salesBeing(); 	  }
	    	else if ( e.getSource()==hideYear           ){ hidetheyear(); 	  }
	        
            repaint();
		}
	
	   	Person picked;
	   	@Override
	    public void mouseClicked(MouseEvent m){
	    	picked.x = m.getX(); picked.y = m.getY();
	    	repaint();
	   	}
	    @Override  
	    public void mousePressed(MouseEvent m){
	    	picked = theMap.click(m.getX(), m.getY());
	    }
	    @Override
		public void mouseDragged(MouseEvent m){
	    	picked.x = m.getX(); picked.y = m.getY();
	    	repaint();
		}
		@Override    
	    public void mouseReleased(MouseEvent m){}
		@Override
		public void mouseEntered(MouseEvent e){}
		@Override
		public void mouseExited(MouseEvent e){}
		@Override
		public void mouseMoved(MouseEvent e){}

		public void hidetheyear(){
    		Friendship.hide = !Friendship.hide; 
    	    String yr;
    	    if(Friendship.hide) { yr = "Show Year"; }
    	    else				{ yr = "Hide Year"; }
    		hideYear.setText(yr); 
		}
		
		//connect everyone as friends
		public void doAllFriends(){ theMap.doAllFriends(); }
		
		//find a path between the two marked cities and color it blue
		public void doPath(){ 
			double len = theMap.shortPath();
			if(len > 1000){ cost.setText("No path.");   }
			else		  { cost.setText("Cost: "+len); }
			
		}
		
		//find a path that touches every node at least once
		public void salesBeing(){ theMap.salesBeing(); }
		
		// type is true if it's a random person, false if it requires inputs
		public void makePerson(boolean type){
			Random r = new Random();
			String n;
			Person a;
			
			if(type != true){
				n = JOptionPane.showInputDialog("Please enter new Person's name:");
				a = new Person(n, null);
			}
			else{
				//pick a gender
				int gennum = r.nextInt(2);
				String gender;
				if(gennum == 0){ 
					gender = "F";
					n = ranFP[fcount++];
					if(fcount > 4){ fcount = 0; }
				}
				else{ 
					gender = "M";
					n = ranMP[mcount++];
					if(mcount > 4){ mcount = 0; }
				}
				
				a = new Person(n, gender);
			}
			
			theMap.people.add(a);
		}
		
		// type is true if it's a random person, false if it requires inputs
		public void makeFriend(boolean type){ 
			//not yet using type since all friendships created are random right now
			//try 100 times to create a random friendship
			int count=0, limit=100;
			while(!theMap.makeFriend(null, null) && count < limit){ count++; }
		}
		
		public void paint(Graphics g){
			super.paint(g);
			theMap.paint(g);  // paint the map
			if(Map.finished){ // if a path has been found, report the cost
				cost.setText("Cost: "+salesLength);
				Map.finished = false; // reset finished to false
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand() == "costyears"){
			Map.costyr = true;
			theMap.updateCost();
		}
		else{
			Map.costyr = false;
			theMap.updateCost();
		}
	}
}