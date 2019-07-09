# Restaurant Simulation
## Introduction
* Use Java Multithread to simulate a restaurant
* Restaurant has three states: Open, Full and Close
* Two roles in restaurant: Customer and Cook
* Only cook way is using Machine
* When all cooks are busy which means all machines are full, the next order should wait util there are some machines free.
* When the number of customers is equal to the capacity, the restaurant is full. The next customer should wait util there is someone finishing his meal and leave.
* Customer States:
  > CustomerStarting, CustomerEntered, CustomerPlacedOrder, CustomerReceivedOrder, CustomerLeaving
* Cook States:
  > CookStarting, CookReceivedOrder, CookStartedFood, CookFinishedFood, CookCompletedOrder, CookEnding
* Machine States:
  > MachineStarting, MachineStartingFood, MachineDoneFood, MachineEnding
* Customers and cooks are multithread. Machines are multithread and each machine contains some threads because each machine can cook multi food.
