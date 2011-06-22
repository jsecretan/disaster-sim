/*
  Copyright 2010 by Jimmy Secretan
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package disastersim;

import sim.util.*;
import sim.engine.*;
import sim.field.network.Edge;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

import sim.portrayal.simple.OvalPortrayal2D;

public/* strictfp */class DisasterAgent extends
		sim.portrayal.simple.CircledPortrayal2D implements Steppable,Proxiable {

	public String id;

	// for Object2D
	public Double2D agentLocation = null;

	//For tracking bandwidth load
	public int dataBytesSent = 0;
	public int dataBytesReceived = 0;

	//Events that the agent has observed
	//It is important that the agents don't simply tell each
	//other the exact ids of the events observed.  In the real world, they would be guessing
	//and just passing observations.  This set is simply to prevent users from reporting the same
	//event twice.  We assume that they have a good enough memory to avoid doing that.
	private Set<Event> observedEvents = new HashSet<Event>();

	//A map recording the time that we last synced with a particular agent.
	//Ensures that we do not resync too often.
	private Map<DisasterAgent,Double> lastSyncByAgent = new HashMap<DisasterAgent,Double>();

	//Agents with whom we have exchanged information
	private Set<DisasterAgent> connectedAgents = new HashSet<DisasterAgent>();

	//The observations as would be recorded by the device
	public Set<Observation> observations = new HashSet<Observation>();

	//The events we have interpreted to exist, based on our data
	public Vector<InterpretedEvent> interpretedEvents = new Vector<InterpretedEvent>();

	//At every step in the simulation, keeps track of how many events I am aware of
	//over how many there are total in progress
	public Vector<Double> awarenessRatio = new Vector<Double>();

	public Vector<Double> falsePositives = new Vector<Double>();

	//The list of movements that the agent must make
	private Vector<Double2D> agentMovements = null;

	public DisasterAgent(final Double2D location, String id) {

		//TODO: Put the visibility expansion factor in the configuration
		//We probably can't keep it like this as it will affect the agents' ability to view one another
		super(new OvalPortrayal2D(DisasterConstants.AGENT_DIAMETER));

		this.agentLocation = location;
		this.id = id;

		paint = new Color(0, 0, 0);
	}


	/**
	 * After receiving a string of ids, the emergency device checks to see which it has that the other does not
	 * and sends those observations over.  Right now we send observations, in the real thing we would send only ids
	 * @param ids
	 * @return
	 */
	public Vector<Observation> sendBackObservations(Set<Observation> ids) {

		Vector<Observation> toSend = new Vector<Observation>();

		//Go through all of our observations, and add to the vector
		//every one we have that they don't
		for(Observation obs : observations) {
			if(!ids.contains(obs)) {
				toSend.add(obs);
			}
		}

		dataBytesReceived += ids.size() * DisasterConstants.BYTES_PER_OBS_ID;
		dataBytesSent += toSend.size() * DisasterConstants.BYTES_PER_OBS;

		return toSend;
	}

	/**
	 * Exchanges information with another transmitter and updates our internal data
	 */
	public void receiveAndProcessInformation(DisasterAgent otherAgent) {
		//System.out.println("Agent id: "+id+" exchanging information with "+otherAgent.id);

		//Receive any observations we don't have yet
		Vector<Observation> otherObs = otherAgent.sendBackObservations(observations);

		//Account for bandwidth
		dataBytesReceived += otherObs.size() * DisasterConstants.BYTES_PER_OBS;
		dataBytesSent += observations.size() * DisasterConstants.BYTES_PER_OBS_ID;

		//Merge them with ours
		observations.addAll(otherObs);

		//Recompute our interpreted events, if we received any
		if(otherObs.size() != 0) {
			recomputeInterpretedEvents();
		}
	}

	/** Recomputes our interpreted events, based on all of our current observations
	 *
	 */
	public void recomputeInterpretedEvents() {

		//First, organize by event type (the first string)
		//TODO Store this more efficiently somewhere else, so we're not resorting every time
		HashMap<String,Vector<Observation>> obsByEventType = new HashMap<String,Vector<Observation>>();

		for(Observation obs : observations) {

			if(obsByEventType.get(obs.eventType) == null) {
				obsByEventType.put(obs.eventType, new Vector<Observation>());
			}

			obsByEventType.get(obs.eventType).add(obs);
		}

		//Clear out interpreted events because we are now recomputing them
		interpretedEvents.clear();

		//Then take each event vector, and try to make sense out of it
		for(Vector<Observation> obsVec : obsByEventType.values()) {

			clusterEvents(obsVec);

		}
	}

	/**
	 * Clusters events, assuming they are of a identical type
	 * We must cluster in two dimensions of space and one dimension of time, based
	 * on the expected radius of the event type
	 * @param obsVec
	 */
	private void clusterEvents(Vector<Observation> obsVec) {

		//TODO: Get mean and std. deviation of both the time and the area for the event time
		ObservationClusterer clusterer = new SimpleObservationClusterer();

		interpretedEvents.addAll(clusterer.clusterObservations(obsVec));
	}

	public double calculateAwarenessRatio(final Vector<Event> events) {

		double aR = 0.0;

		int eventsInProgress = 0;
		//Count the number we are aware of in all of the events
		//TODO: Create a set or something to make this faster
		for(Event e : events) {

			if(e.isInProgress()){

				boolean eventFound = false;

				for(Iterator<InterpretedEvent> iter = interpretedEvents.iterator(); iter.hasNext() && !eventFound; ) {

					if(iter.next().setOfTrueEvents.contains(e)) {
						aR += 1.0;
						eventFound = true;
					}
				}
				eventsInProgress++;
			}
		}

		if(eventsInProgress > 0) {
			aR /= eventsInProgress;
		}
		else {
			aR = 0;
		}

		return aR;
	}

	public double calculateFalsePositives(final Vector<Event> events) {
		double fp = 0.0;

		//Tally for each event type, how many we have
		int eventsInProgress = 0;
		//Count the number we are aware of in all of the events
		//TODO: Create a set or something to make this faster
		for(Event e : events) {

			if(e.isInProgress()){
				eventsInProgress++;
			}
		}

		if(interpretedEvents.size() > eventsInProgress) {
			fp = interpretedEvents.size()-eventsInProgress;
		}
		else {
			fp = 0;
		}

		return fp;
	}

	public void step(final SimState state) {

		CooperativeObservation sim = (CooperativeObservation) state;

		//Remove any edges I currently have every 5 timesteps
		//TODO: This should be more elegant
		/*if(((int)sim.schedule.getTime())%5 == 0 ) {
			for(Object e : sim.deviceCommunication.getEdges(this, null)) {
				sim.deviceCommunication.removeEdge((Edge) e);
			}
		}*/

		//First, we want to move the agent in a random walk

		Double2D location = agentLocation;// hb.environment.getObjectLocation(this);


		int timeStep = (int)sim.schedule.getTime();

		//Read from our list of movements, or just be random
		if(agentMovements != null && agentMovements.size() > timeStep) {
			agentLocation = agentMovements.elementAt(timeStep);
		}
		else {

			agentLocation = new Double2D(location.x + DisasterConstants.PEDESTRIAN_SPEED*(2.0*(Math.random()-0.5)), location.y + DisasterConstants.PEDESTRIAN_SPEED*(2.0*(Math.random()-0.5)));
		}


		sim.environment.setObjectLocation(this, agentLocation);


		//Next, see if any other transmitter has come within our transmission distance
		Bag neighbors = sim.environment.getObjectsExactlyWithinDistance(agentLocation,DisasterConstants.TRANSMIT_DISTANCE);


		//For all fellow pedestrians with transmitters, exchange messages
		for(int i = 0 ; i < neighbors.size(); i++) {

			//If the neighbor is another transmitter, exchange information
			if(neighbors.getValue(i) instanceof DisasterAgent) {
				DisasterAgent otherAgent = (DisasterAgent)neighbors.getValue(i);

				//Represent this as an edge in the communication graph. if we don't already have one
				/*if(!connectedAgents.contains(otherAgent)) {
					sim.deviceCommunication.addEdge(this, otherAgent, null);
					connectedAgents.add(otherAgent);
				}*/

				Double timeOfLastSync = lastSyncByAgent.get(otherAgent);

				if(otherAgent != this && //exclude ourselves and make sure we haven't resynced very recently
						(timeOfLastSync == null || (timeStep-timeOfLastSync) >= DisasterConstants.MINS_BEFORE_RESYNC)) {
					receiveAndProcessInformation(otherAgent);

					//Make sure we record the last time we synced
					lastSyncByAgent.put(otherAgent, (double) timeStep);
				}
			} //TODO: Assumes the communication distance is the same as event observation difference, they could differ
			else if(neighbors.getValue(i) instanceof Event) { //if the neighbor is an event we may want to observe it

				Event e = (Event) neighbors.getValue(i);

				//Make sure to represent that we are viewing this event
				//sim.deviceCommunication.addEdge(this, e, null);

				if(e.inProgress && Math.random() <= DisasterConstants.EVENT_OBSERVATION_PROBABILITY) {

					//If we have observed the event before, there is nothing we would report
					//otherwise report it with certain probability
					if(!observedEvents.contains(e) && Math.random() <= DisasterConstants.EVENT_REPORTING_PROBABILITY) {

						observedEvents.add(e);

						//Assume that we record the observation
						//instantaneously upon seeing it
						recordObservation(e,state.schedule.time());
					}
				}
			}
		}

		if(DisasterConstants.EXPIRE_OBSERVATION_CACHE) {
			expireObservationCache(state.schedule.time());
		}

		//Update statistics
		awarenessRatio.add(calculateAwarenessRatio(sim.allEvents));
		//falsePositives.add(calculateFalsePositives(sim.allEvents));
	}

	/**
	 * Kicks observations past a certain age out of our current store.  This is to minimize traffic
	 * and make sure we aren't reporting or storing things that could no longer be pertinent
	 */
	public void expireObservationCache(double time) {

		for(Iterator<Observation> obsIter = observations.iterator(); obsIter.hasNext(); ) {

			Observation obs = obsIter.next();

			double avg = DisasterConfiguration.getDisasterDataMap().get(obs.eventType).getAverageDuration();
			double stdDev = DisasterConfiguration.getDisasterDataMap().get(obs.eventType).getDevDuration();

			//If the time observed is beyond our threshold, kick it out
			if(obs.timeObserved < (time-(avg-DisasterConstants.STD_DEVS_FROM_AVG_TO_REMOVE_FROM_CACHE*stdDev))) {
				obsIter.remove();
			}
		}
	}

	/*
	 * Records the observed event into the device, in a way approximating the person
	 * Assumes the recording is instantaneous
	 */
	private void recordObservation(Event e, double time) {
		observations.add(new Observation(e.eventType,agentLocation,time,e));

		//Recompute our interpreted events because this observation may change them
		recomputeInterpretedEvents();
	}

	public class MyProxy {
		public double getAwareness() {
			double toReturn = 0.0;
			if(awarenessRatio.size() > 0) {
				toReturn = awarenessRatio.lastElement();
			}

			return toReturn;
		}

		public int getDataBytesSent() {
			return dataBytesSent;
		}

		public int getDataBytesReceived() {
			return dataBytesReceived;
		}

		public int getTotalNumberOfObservations() {
			return observations.size();
		}

		public double getTotalNumberOfInterpretedEvents() {
			return interpretedEvents.size();
		}

		 // because we are a non-static inner class
        static final long serialVersionUID = -2815745192429358606L;
	}

	static final long serialVersionUID = 7720089824883511682L;

	public Object propertiesProxy() {
		return new MyProxy();
	}


	public Vector<Double2D> getAgentMovements() {
		return agentMovements;
	}


	public void setAgentMovements(Vector<Double2D> agentMovements) {
		this.agentMovements = agentMovements;
	}
}
