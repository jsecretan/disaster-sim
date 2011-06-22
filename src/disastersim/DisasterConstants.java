/* A set of constants for the disaster simulation
 *
  Copyright 2010 by Jimmy Secretan
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package disastersim;

public class DisasterConstants {

	//All distances are in meters and times are in minutes unless otherwise specified
	public static double TRANSMIT_DISTANCE = 20.0;

	//m/s, a as specified here: http://en.wikipedia.org/wiki/Walking
	public static double PEDESTRIAN_SPEED = 1.3;

	//The probability that, if an agent is within a certain distance of an event
	//he will actually observe it (i.e. it won't escape his notice)
	public static double EVENT_OBSERVATION_PROBABILITY = 1.0;

	//The probability that, if an agent indeed observes an event, he will enter it into
	//his system as a report
	public static double EVENT_REPORTING_PROBABILITY = 0.5;

	//Number of bytes in an observation data packet
	public static int BYTES_PER_OBS = 20;

	//Number of bytes specifically for the observation id
	public static int BYTES_PER_OBS_ID = 8;

	//Whether or not to regularly expire the observation cache to save space
	public static final boolean EXPIRE_OBSERVATION_CACHE = true;

	//Number of standard deviations to kick out of cache
	//TODO: Replace this with a more Bayesian metric
	public static final double STD_DEVS_FROM_AVG_TO_REMOVE_FROM_CACHE = 3.0;

	//Sizes of the simulation environment
	public static final double XMIN = 0;
	public static final double XMAX = 400;
	public static final double YMIN = 0;
	public static final double YMAX = 400;

	//The maximum timescale we care about in clustering events
	public static final double MAX_TIMESCALE_FOR_CLUSTERING = 24*60; //Minutes in one days

	//TODO: Replace with event size
	public static final double AGENT_DIAMETER = 1;

	//Number of "denizens of disaster"
	public static final int NUM_AGENTS = 150;

	//Amount of time to wait before resynchronizing with another agent in minutes
	public static final double MINS_BEFORE_RESYNC = 5.0;

	//Cutoff probability to create a new observed event if one isn't found
	//in the current list
	public static final double CREATE_NEW_OBS_CUTOFF = 0.95;

}
