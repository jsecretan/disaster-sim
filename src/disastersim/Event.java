/*
  Copyright 2010 by Jimmy Secretan
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package disastersim;

import sim.util.*;
import sim.engine.*;
import java.awt.*;

public/* strictfp */class Event extends sim.portrayal.simple.OvalPortrayal2D
		implements Steppable {
	public String id;

	private double startTime;

	private double endTime;

	public boolean inProgress;

	public String eventType;

	private Double2D location;

	//My own stopper
	private Stoppable stopper = null;

	public Event() {

	}

	public Event(final Double2D location, final String id, final String eventType, final double diameter) {
		//Note: we expand the visibility of the agent through this
		super(diameter, false);

		this.eventType = eventType;
		this.agentLocation = location;
		this.id = id;

		inProgress = false;

		paint = new Color(255, 0, 0);
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public void step(final SimState state) {

		CooperativeObservation co = (CooperativeObservation) state;

		//If we're stepping we should be in progress
		if(!inProgress && state.schedule.time() <= endTime) {
			inProgress = true;

			//Add our portrayal
			co.environment.setObjectLocation(this, location);
		}

		//If we've passed our time, stop
		if(state.schedule.time() >= endTime) {
			inProgress = false;

			if(stopper != null) {
				stopper.stop();
			}

			//Now we can remove the portrayal of ourselves
			co.environment.remove(this);

		}
	}

	// for Object2D
	public Double2D agentLocation = null;

	public Stoppable getStopper() {
		return stopper;
	}

	public void setStopper(Stoppable stopper) {
		this.stopper = stopper;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}

	public Double2D getLocation() {
		return location;
	}

	public void setLocation(Double2D location) {
		this.location = location;
	}

}
