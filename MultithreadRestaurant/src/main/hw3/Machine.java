package hw3;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	//YOUR CODE GOES HERE...
	private final int capacity;
	public volatile boolean full;
	private int run;
	public final Object lock = new Object();

	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		//YOUR CODE GOES HERE...
		this.capacity = capacityIn;
		this.full = capacityIn <= 0;
		this.run = 0;
		Simulation.logEvent(SimulationEvent.machineStarting(this,foodIn,capacityIn));
	}
	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public Thread[] makeFood(Cook cook,Customer customer,int num) throws InterruptedException {
		//YOUR CODE GOES HERE...
//		full = true;
		Thread[] threads = new Thread[num];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new CookAnItem(cook,customer));
		}
		synchronized (lock) {
//			if(run+num<capacity) {
//				full = false;
//			}
			for (int i = 0; i < threads.length; i++) {
				while(full) {
					lock.wait();
				}
				if(++run>=capacity) full = true;
				threads[i].start();
			}
		}
		return threads;
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		
		private Cook cook;
		private Customer customer;
		
		public CookAnItem(Cook cook, Customer customer) {
			this.cook = cook;
			this.customer = customer;
		}
		
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				Simulation.logEvent(SimulationEvent.cookStartedFood(cook, machineFoodType, customer.orderNum));
				Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
				Thread.sleep(machineFoodType.cookTimeMS);
				synchronized (lock) {
					run--;
					if(full) full = false;
					lock.notifyAll();
				}
				Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, Machine.this.machineFoodType));
				Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, customer.orderNum));
			} catch(InterruptedException e) { 
				Simulation.logEvent(SimulationEvent.machineEnding(Machine.this));
			}
		}
	}
 

	public String toString() {
		return machineName;
	}
}