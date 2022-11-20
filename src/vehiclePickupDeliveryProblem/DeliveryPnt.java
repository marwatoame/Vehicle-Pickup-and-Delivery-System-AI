package vehiclePickupDeliveryProblem;

public class DeliveryPnt extends Location{

	int weight = -1;
	int maxTime;
	int timeDelivered;
	
	
	DeliveryPnt(double x, double y, int maxTime) {
		super(x, y);
		
		this.maxTime = maxTime;
	}

}
