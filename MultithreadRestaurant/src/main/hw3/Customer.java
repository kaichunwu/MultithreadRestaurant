package hw3;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	public final List<Food> order;
	public final int orderNum;    
	
	private static int runningCounter = 0;
	
	private static int cusCounter = 0;
	public static int maxCus = 40;
	public static Object lock = new Object();

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		synchronized (lock) {
			while(cusCounter>=maxCus) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			cusCounter++;
		}		
		Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
		synchronized (Simulation.waitOrder) {
			Simulation.logEvent((SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum)));
			Simulation.waitOrder.add(this);
			Simulation.waitOrder.notifyAll();
		}
		synchronized (this) {
			try {
				// wait for cook to notify
				this.wait();
				Simulation.logEvent((SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum)));
				// eat time 100ms
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
		synchronized (lock) {
			cusCounter--;
			lock.notifyAll();
		}
	}
}