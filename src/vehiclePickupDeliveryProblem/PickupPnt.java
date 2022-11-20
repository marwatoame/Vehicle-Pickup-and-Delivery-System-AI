package vehiclePickupDeliveryProblem;

public class PickupPnt extends Location{

	int weight = 1;
	int minTime;
	int timePicked;
	DeliveryPnt toPoint;
	
	
	PickupPnt(double x, double y, int minTime) {
		super(x, y);
		
		this.minTime = minTime;
	}

}
