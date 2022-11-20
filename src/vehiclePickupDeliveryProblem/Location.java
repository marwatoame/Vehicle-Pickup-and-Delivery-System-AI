package vehiclePickupDeliveryProblem;

public class Location {
	
	double x;
	double y;

	String name;
	String nName;
	int id;
	
	int weight;
	int timeLimit;
	int timeArrived;
	int timeWaited = 0;
	int status;
	Location pPoint;
	
	int varNumAssign;
	int backtrackDegreeDigit1 = 0;
	int backtrackDegreeDigit2 = 0;
	boolean ignore = false;
	
	Location sameGoRoundPoint;
	
	
	Location(double x, double y, String nName, String name, int id) {
		this.x = x;
		this.y = y;
		this.nName = nName;
		this.name = name;
		this.id = id;
	}
	

	Location(double x, double y, int timeLimit, int weight, int status, String nName, String name, int id) {
		this.x = x;
		this.y = y;
		this.timeLimit = timeLimit;
		this.weight = weight;
		this.status = status;
		this.nName = nName;
		this.name = name;
		this.id = id;
	}

	
	public static double Euclidian(Location p1, Location p2) {

		return Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
	}

}


// status encode:

// 1 : not picked
//-1 : not delivered

// 2 : in queue (pickup)
//-2 : in queue (delivery)

// 3 : picked
//-3 : delivered

// 4 : pickup point that picked and delivered

//-4 : available to deliver

