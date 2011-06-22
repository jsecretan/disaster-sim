/*
  Copyright 2010 by Jimmy Secretan
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package disastersim;

import sim.field.continuous.*;
import sim.field.network.Network;
import sim.engine.*;
import sim.util.*;
import java.util.*;
import java.io.File;
import org.jfree.data.xy.XYSeries;



public/* strictfp */class CooperativeObservation extends SimState {

	public Continuous2D environment = null;

	public Network deviceCommunication = null;

	//Helps us easily keep track of all of the events in the system for running awareness computations
	public Vector<Event> allEvents = new Vector<Event>();
	public Vector<DisasterAgent> allAgents = new Vector<DisasterAgent>();

	public StatisticsCalculator statCalc = new StatisticsCalculator();

	public XYSeries timeSeriesAwarenessRate = new XYSeries("Awareness Rate");

	public XYSeries timeSeriesFalsePositives = new XYSeries("False Positives");

	public XYSeries timeSeriesEventsInProgress = new XYSeries("Events In Progress");

	/**
	 * Creates a CooperativeObservation simulation with the given random number
	 * seed.  Also opens the configuration file.
	 * TODO: Make configuration file a parameter
	 */
	public CooperativeObservation(long seed) {
		super(seed);
	}

	boolean conflict(final Object agent1, final Double2D a,
			final Object agent2, final Double2D b, final double diameter) {
		if (((a.x > b.x && a.x < b.x + diameter) || (a.x + diameter > b.x && a.x
				+ diameter < b.x + diameter))
				&& ((a.y > b.y && a.y < b.y + diameter) || (a.y + diameter > b.y && a.y
						+ diameter < b.y + diameter))) {
			return true;
		}
		return false;
	}

	boolean acceptablePosition(final Object agent, final Double2D location, final double diameter) {
		if (location.x < diameter / 2
				|| location.x > (DisasterConstants.XMAX - DisasterConstants.XMIN)/* environment.getXSize() */
						- diameter / 2
				|| location.y < diameter / 2
				|| location.y > (DisasterConstants.YMAX - DisasterConstants.YMIN)/* environment.getYSize() */
						- diameter / 2)
			return false;
		Bag misteriousObjects = environment.getObjectsWithinDistance(location, /* Strict */
				Math.max(2 * diameter, 2 * diameter));
		if (misteriousObjects != null) {
			for (int i = 0; i < misteriousObjects.numObjs; i++) {
				if (misteriousObjects.objs[i] != null
						&& misteriousObjects.objs[i] != agent) {
					if(misteriousObjects.objs[i] instanceof DisasterAgent) {
						Object ta = (DisasterAgent) (misteriousObjects.objs[i]);
						if (conflict(agent, location, ta, environment.getObjectLocation(ta), diameter))
							return false;
					}
				}
			}
		}
		return true;
	}

	public void start() {

		super.start(); // clear out the schedule

		//TODO: Check this discretization
		environment = new Continuous2D(1.0, DisasterConstants.XMAX - DisasterConstants.XMIN, DisasterConstants.YMAX - DisasterConstants.YMIN);

		deviceCommunication = new Network();

		// Place all of the events, and associate them with start time and end times
		for(DisasterEventData d : DisasterConfiguration.getDisasterDataMap().values()) {

			for(int i = 0; i < d.numberOfEvents; i++) {

				double diameter = d.averageDiameter+random.nextGaussian()*d.devDiameter;

				//Create the event and place in a location, making sure it isn't offscreen
				Double2D loc = new Double2D(random.nextDouble()
						* (DisasterConstants.XMAX - DisasterConstants.XMIN - diameter) + DisasterConstants.XMIN + diameter / 2, random
						.nextDouble()
						* (DisasterConstants.YMAX - DisasterConstants.YMIN - diameter) + DisasterConstants.YMIN + diameter / 2);

				Event e = new Event(loc, "Event" + i, d.name, diameter);

				//Create start time and stop time
				//FIXME: Should this be on a different timescale?
				double startTime = random.nextDouble()*DisasterConstants.MAX_TIMESCALE_FOR_CLUSTERING;

				e.setStartTime(startTime);

				e.setEndTime(startTime+d.averageDuration+random.nextGaussian()*d.devDuration);

				e.setLocation(loc);
				//environment.setObjectLocation(e, loc);

				//Add it as a node so we can visualize the agent seeing this event
				//TODO: rename deviceCommunication to make more sense in this context
				deviceCommunication.addNode(e);

				allEvents.add(e);

				//Make sure the event can stop itself when is appropriate
				Stoppable stop = schedule.scheduleRepeating(startTime, e, 1.0);
				e.setStopper(stop);
			}
		}

		//Load the agent data file
		AgentMovementData agentMovements = new AgentMovementData("conf/AgentMovements.txt");

		// Schedule the agents -- we could instead use a RandomSequence, which
		// would be faster,
		// but this is a good test of the scheduler
		for (int x = 0; x < DisasterConstants.NUM_AGENTS; x++) {
			Double2D loc = null;
			DisasterAgent agent = null;
			int times = 0;
			do {

				loc = agentMovements.getMapDataForUser(x).firstElement();

				/*loc = new Double2D(random.nextDouble()
						* (DisasterConstants.XMAX - DisasterConstants.XMIN - DisasterConstants.AGENT_DIAMETER) + DisasterConstants.XMIN + DisasterConstants.AGENT_DIAMETER / 2,
						random.nextDouble() * (DisasterConstants.YMAX - DisasterConstants.YMIN - DisasterConstants.AGENT_DIAMETER) + DisasterConstants.YMIN
								+ DisasterConstants.AGENT_DIAMETER / 2);*/
				agent = new DisasterAgent(loc, "Agent" + x);

				agent.setAgentMovements(agentMovements.getMapDataForUser(x));

				times++;
				if (times == 1000) {
					System.err.println("Cannot place agents. Exiting....");
					System.exit(1);
				}
			} while (!acceptablePosition(agent, loc, DisasterConstants.AGENT_DIAMETER));
			environment.setObjectLocation(agent, loc);

			//So we can represent the communication events
			deviceCommunication.addNode(agent);

			//This helps us organize them more easily
			allAgents.add(agent);

			schedule.scheduleRepeating(agent);
		}

		//Create the statistics calculator

		schedule.scheduleRepeating(statCalc);

	}

	public static void main(String[] args) {
		doLoop(CooperativeObservation.class, args);
		System.exit(0);
	}
}
