package disastersim;

import sim.util.*;

public class Observation {

	public Observation(String eventType, Double2D location, double timeObserved, Event event) {
		this.eventType = eventType;
		this.location = location;
		this.timeObserved = timeObserved;
		this.event = event;
	}

	//TODO: Make a unique observation id based on the agent id and the time observed
	//Right now, we will fudge it with the address of the object


	//The event associated with this observation
	//IMPORTANT: This should only be used to check the accuracy of the
	//algorithm, as this information would not really be synchronized so neatly in real life
	public Event event;

	public String eventType;

	//The location where the event is estimated by the user to be
	//In the beginning, we will assume that this is where the user was
	//when he recorded the event, but this is not necessarily true (i.e. the user
	//could wait until he is in some place safe, and then enter it into a map)
	public Double2D location;

	//The time (simulation time) when the event is observed
	//In the real algorithm, each user will keep his own clock, transmit the number of seconds
	//since the observation, and the receiver will re-normalize the time back
	public double timeObserved;
}
