package hw3;

import java.util.*;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;

	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		Customer customer;
		
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				synchronized (Simulation.waitOrder) {
					while(Simulation.waitOrder.isEmpty()) {						
						Simulation.waitOrder.wait();
					}
					customer = Simulation.waitOrder.remove(0);
					Simulation.waitOrder.notifyAll();
				}
				Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, customer.order, customer.orderNum));
				if(customer!=null) {
					synchronized (customer) {
						List<Food> order = customer.order;
						List<Machine> machines = Simulation.machines;
						Map<String,Integer> foods = new HashMap<>();
						for(Food f : order) {
							int n = 0;
							if(foods.get(f.name)!=null) {
								n = foods.get(f.name);
							}
							foods.put(f.name, n+1);
						}
						Thread[] threads = new Thread[order.size()];
						int i = 0;
						while(true) {
							for(Machine machine : machines) {
								String foodType = machine.machineFoodType.name;
								if(!machine.full&&foods.get(foodType)!=null) {
									Thread[] t = machine.makeFood(this,customer,foods.get(foodType));
									for (int j = 0; j < t.length; j++) {
										threads[i++] = t[j];
									}
									foods.remove(foodType);
								}
							}
							if(foods.isEmpty()) {
								break;
							}
						}
						for (int j = 0; j < threads.length; j++) {
							threads[j].join();
						}
						Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, customer.orderNum));
						customer.notifyAll();
					}
				}
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}