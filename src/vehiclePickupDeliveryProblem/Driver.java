package vehiclePickupDeliveryProblem;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;


public class Driver {
	public static Location[] point;
	
	public static double time = 0;
	public static Location position;
	public static int capacity = 5;
	public static LinkedList<Location> vehicle = new LinkedList<Location>();
	public static LinkedList<Location> path = new LinkedList<Location>();
	
	public static Node[] node;
	public static Edge[] edge;
	public static SingleGraph VechilePath = new SingleGraph("Vechile Path");


	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		File f = new File("locations.txt");
		Scanner coordinate = new Scanner(f);
		
		File w = new File("timewindows.txt");
		Scanner times = new Scanner(w);
		
		
		int n = coordinate.nextInt();
		
	
		point = new Location[n];
		
		
		point[0] = new Location(coordinate.nextInt(), coordinate.nextInt(), "pnt0", "point[0]", 0);
		path.add(point[0]);
				
		position = point[0];
		
		System.setProperty("org.graphstream.ui", "swing");

		node = new Node[n];
		edge = new Edge[n];

		String styleSheet =
		        "node { "
			        + "fill-color: rgb(34,139,34);"
			        + "size: 13px;"
			        + "text-color: white;"
			        + "text-size: 17;"
			        + "text-alignment: above;"
			        + "}"+
		        "edge"
		        	+ "{ fill-color: rgb(0,100,0);"
		        	+ "size: 3px;"
		        	+ "}"+
		        "graph{ fill-color: black; }";
		
		VechilePath.setAttribute("ui.stylesheet", styleSheet);		
		VechilePath.display();
		

		addNode(point[0] , VechilePath);
		node[0].setAttribute("ui.style", "fill-color: blue;");
		node[0].setAttribute("ui.style", "size: 15px;");


		for (int i=1; i<n; i+=2) {
			
			point[i]   = new Location(coordinate.nextInt(), coordinate.nextInt(), times.nextInt(),  1,  1,   "pnt"+i,   "Point["   +i   +"]", i);
			point[i+1] = new Location(coordinate.nextInt(), coordinate.nextInt(), times.nextInt(), -1, -1, "pnt"+(i+1), "Point[" +(i+1) +"]", i+1);
			
			point[i].pPoint = point[i+1];
			point[i+1].pPoint = point[i];
			
			
			
			addNode( point[i]  , VechilePath);
			addNode(point[i+1] , VechilePath);
		}

		coordinate.close();
		times.close();
		

		boolean np;
		boolean dn = true;
		

		do{
			
			np = pathv(n, point);
			
			
			if(np == true) {
				for(int i=1; i<n; i++){
					if(point[i].status==1 || point[i].status==-1 || point[i].status==2 || point[i].status==-2)		
						dn = false;
				}
				if(dn == true)	System.out.println("done");
				else {
					backTrack(null , null);
					np = false;
				}
				dn = true;
			}
			
						
		}
		while(np == false);

		System.out.println("\n\n");

		for(int i=0; i<path.size(); i++){

			System.out.println("visited " +path.get(i).name +" at minute " +path.get(i).timeArrived);
			
			if(path.get(i).timeWaited > 0) 		System.out.println("Time Waited in " +path.get(i).name +": " +path.get(i).timeWaited);		
		}
	}
	

	public static void addNode(Location point , SingleGraph VechilePath){
		
		node[point.id] = VechilePath.addNode(point.nName);
		node[point.id].setAttribute("xyz", point.x, point.y, 0);	
		node[point.id].setAttribute("layout.frozen");
		node[point.id].setAttribute("ui.label", point.nName);
	}
	
	public static void addEdge(Location point , SingleGraph VechilePath){
		
		if(point.id!=0)	edge[point.id] = VechilePath.addEdge(point.name, path.getLast().nName, point.nName);
	}
	

	public static boolean pathv(int n, Location[] point) throws InterruptedException {
		
		
		Location variable = selectVar(n, point);

		if(variable == null)	return true;
		

		Location variable2 = selectVar2(n, point, variable);
		
		if(variable2 != null) {
			variable.sameGoRoundPoint = variable2;
			variable2.sameGoRoundPoint = variable;
			
		}
		
		if(variable2 == null)	 
			return go(null , variable);
		
		
		else {

			boolean z = go(null , variable2);

			if(variable.status!=2 && variable.status!=-2) {
				return z;
			}
						
			return go(variable2 , variable);
		}
	}
	
	
	public static Location selectVar(int n, Location[] point) {
		
		Location variable = null;
		int time9 = 99999999;
		int length = 99999999;
		
		int counter = 0;
		
		do {
			
			
			variable = null;
			time9 = 99999999;
			length = 99999999;
			

			for(int i=1; i<n; i++){
				
				if(point[i].status == 3 && point[i].pPoint.ignore==false && Location.Euclidian(Driver.position , point[i])<length) {

					variable = point[i].pPoint;
					
					length = (int)Location.Euclidian(Driver.position , point[i]);
				}
			}

			for(int i=1; i<n; i++){
				
				if(point[i].status == -1 && point[i].pPoint.ignore==false && vehicle.size()<Driver.capacity-2 && point[i].pPoint.status != 3  && point[i].timeLimit < time9){
					
					variable = point[i].pPoint;
					
					time9 = point[i].timeLimit;
				}
			}

			if(counter < Driver.position.backtrackDegreeDigit2){
				
				variable.ignore = true;
				
				counter++;
			}
			
			
			if(variable == null) break;
			
		}while(variable.ignore == true);
		
		for(int i=1; i<n; i++)	 point[i].ignore = false;

		if(variable != null) {
			
			variable.varNumAssign = 1;
			
			switch(variable.status){
			
			case 1: variable.status = 2;
				break;
				
			case -1: variable.status = -2;
				break;
			}
		}
		return variable;
	}

	public static Location selectVar2(int n, Location[] point, Location var1) {

		Location variable2 = null;
		double time9 = 999999999;
		double time7 = 999999999;
		double timeToWait = 0;
		
		int counter = 0;
		
		do {

			variable2 = null;
			time9 = 999999999;
			time7 = 999999999;
			timeToWait = 0;

			if(var1.status==2) {
				
				for(int i=1; i<n; i++){
					
					if(point[i].status==3 && point[i].pPoint.ignore==false && ((Location.Euclidian(Driver.position, point[i].pPoint)+Location.Euclidian(point[i].pPoint, var1)+Location.Euclidian(var1, var1.pPoint)+180)<(var1.pPoint.timeLimit-Driver.time)) && (Location.Euclidian(Driver.position, point[i].pPoint)+Location.Euclidian(point[i].pPoint, var1)+Location.Euclidian(var1, var1.pPoint)<time9)  ){
						
						variable2 = point[i].pPoint;
						
						time9 = Location.Euclidian(Driver.position, point[i])+Location.Euclidian(point[i], var1)+Location.Euclidian(var1, var1.pPoint);
						
					}
					
					
					if(point[i].status==1 && point[i].ignore==false && vehicle.size()<Driver.capacity && (Location.Euclidian(Driver.position, point[i])+Driver.time)<point[i].timeLimit) 		timeToWait = point[i].timeLimit - (Location.Euclidian(Driver.position, point[i])+Driver.time);
						
					if(point[i].status==1 && point[i].ignore==false && ((Location.Euclidian(Driver.position, point[i])+timeToWait+Location.Euclidian(point[i], var1)+Location.Euclidian(var1, var1.pPoint)+180)<(var1.pPoint.timeLimit-Driver.time)) && (Location.Euclidian(Driver.position, point[i])+timeToWait+Location.Euclidian(point[i], var1)+Location.Euclidian(var1, var1.pPoint)<time9)  ){               
											
						variable2 = point[i];
						
						time9 = Location.Euclidian(Driver.position, point[i])+timeToWait+Location.Euclidian(point[i], var1)+Location.Euclidian(var1, var1.pPoint);
					}
					timeToWait = 0;
				}
			}

			else if(var1.status==-2) {
				
				for(int i=1; i<n; i++){
					
					if(point[i].status==3 && point[i].pPoint.ignore==false && point[i].pPoint!=var1 && ((Location.Euclidian(Driver.position, point[i].pPoint)+Location.Euclidian(point[i].pPoint, var1)+180)<(var1.timeLimit-Driver.time)) && (Location.Euclidian(Driver.position, point[i].pPoint)+Location.Euclidian(point[i].pPoint, var1)<time7)  ){
						
						variable2 = point[i].pPoint;
						
						time7 = Location.Euclidian(Driver.position, point[i])+Location.Euclidian(point[i], var1);
						
					}
					
					
					if(point[i].status==1 && point[i].ignore==false && (Location.Euclidian(Driver.position, point[i])+Driver.time)<point[i].timeLimit) 		timeToWait = point[i].timeLimit - (Location.Euclidian(Driver.position, point[i])+Driver.time);
					
					
					if(point[i].status==1 && point[i].ignore==false && vehicle.size()<Driver.capacity && ((Location.Euclidian(Driver.position, point[i])+timeToWait+Location.Euclidian(point[i], var1)+180)<(var1.timeLimit-Driver.time)) && (Location.Euclidian(Driver.position, point[i])+timeToWait+Location.Euclidian(point[i], var1)<time7)   ){
											
						variable2 = point[i];
						
						time7 = Location.Euclidian(Driver.position, point[i])+timeToWait+Location.Euclidian(point[i], var1);
					}
					timeToWait = 0;
				}
			}

			if(counter < Driver.position.backtrackDegreeDigit1){
				
				variable2.ignore = true;
				
				counter++;
			}
			
			
			if(variable2 == null) break;
			
		}while(variable2.ignore == true);

		for(int i=1; i<n; i++)	 point[i].ignore = false;

		if(variable2 != null) {
			
			variable2.varNumAssign = 2;
			
			switch(variable2.status){
			
			case 1: variable2.status = 2;
				break;
				
			case -1: variable2.status = -2;
				break;
			}
		}
		return variable2;
	}

	public static boolean go (Location var2 , Location variable) throws InterruptedException {
		
		Thread.sleep(100);
		
		double timeWaitedd = 0;
		
		Driver.time += Location.Euclidian(Driver.position, variable);
		Driver.position = variable;
		
		variable.timeArrived = (int)Math.round(Driver.time);
		
		
				
		switch(variable.status){
		
		case 2: System.out.println("visited " +variable.name +" at minute " +variable.timeArrived);
		
			addEdge(variable , VechilePath);
			path.add(variable);
			
			if(Driver.time < variable.timeLimit) {
			
			timeWaitedd = variable.timeLimit - Driver.time;
			Driver.time += timeWaitedd;
			
			variable.timeWaited = (int)Math.round(timeWaitedd);
			
			System.out.println("Time Waited in " +variable.name +": " +variable.timeWaited);
		}
				variable.status = 3;
				vehicle.add(variable);
			break;
			
			
		case -2: if(Driver.time > variable.timeLimit) {
			
						addEdge(variable , VechilePath);		edge[variable.id].setAttribute("ui.style", "fill-color: red;");
						Thread.sleep(100);						edge[variable.id].setAttribute("ui.style", "fill-color: rgb(0,100,0);");
						VechilePath.removeEdge(edge[variable.id]);
						
						System.out.println("Tried to reach " +variable.name +" and failed.");			
						backTrack(var2 , variable);			
						return false;
				 }		
			
				 System.out.println("visited " +variable.name +" at minute " +variable.timeArrived);
				
				 addEdge(variable , VechilePath);
				 path.add(variable);
		
				 variable.status = -3;
				 variable.pPoint.status = 4;
				 vehicle.remove(variable.pPoint);
			break;
			
		}
		
		return false;
	}

	public static void backTrack(Location var2 , Location variable) throws InterruptedException {
		
		System.out.println("backtrack ...");
		
		
		
		if(var2==null && variable==null) {
			
			Thread.sleep(100);
			
			if(path.getLast()==null  ||  path.getLast()==point[0]) {
				System.out.println("there is no solution.");
				System.exit(0);
			}
			else {
				VechilePath.removeEdge(edge[path.getLast().id]);
				variable = path.getLast();
				path.removeLast();
				
				
				switch(variable.status) {
				
				case  3: variable.status = 1;
						 vehicle.remove(variable);
					break;
					
				case -3: variable.status = -1;
						 variable.pPoint.status = 3;
						 vehicle.add(variable.pPoint);
					break;
				}
				
				
				if(path.getLast() != null    &&    path.getLast() != point[0]) {
					if(path.getLast().varNumAssign == 2) 	var2 = path.getLast();
				}
			}
		}
	
		if(var2==null && variable.varNumAssign==2) {
			
			
			Driver.position = path.getLast();
			Driver.time = Driver.position.timeArrived + Driver.position.timeWaited;
			
			
			
			switch(variable.sameGoRoundPoint.status) {
			case  2: variable.sameGoRoundPoint.status = 1;
				break;
			case -2: variable.sameGoRoundPoint.status = -1;
				break;
			}
			
			
			Driver.position.backtrackDegreeDigit1++;
			
			return;
		}
	
		variable.backtrackDegreeDigit1 = 0;
		variable.backtrackDegreeDigit2 = 0;
		
		
		if(var2 != null){
			var2.backtrackDegreeDigit1 = 0;
			var2.backtrackDegreeDigit2 = 0;

			VechilePath.removeEdge(edge[path.getLast().id]);
			path.removeLast();
	
			switch(var2.status) {
			
			case  3: var2.status = 1;
					 vehicle.remove(var2);
				break;
				
			case -3: var2.status = -1;
					 var2.pPoint.status = 3;
					 vehicle.add(var2.pPoint);
				break;
			}
		}

		Driver.position = path.getLast();
		Driver.time = Driver.position.timeArrived + Driver.position.timeWaited;
		
		
		
		Driver.position.backtrackDegreeDigit1++;
		
		if(var2 == null) {
			Driver.position.backtrackDegreeDigit2++;
			
			Driver.position.backtrackDegreeDigit1 = 0;
		}
	}
}