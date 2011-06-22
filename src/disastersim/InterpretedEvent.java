package disastersim;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**An event interpreted to exist, based on observations
 *
 */

public class InterpretedEvent {

	public Vector<Observation> observations = new Vector<Observation>();

	//A set of events which the observations represent.  If this interpreted event
	//is correct, this should only contain one item
	public Set<Event> setOfTrueEvents = new HashSet<Event>();

	public InterpretedEvent() {

	}

	public InterpretedEvent(Observation obs) {
		addObservation(obs);
	}

	public void addObservation(Observation obs) {
		observations.add(obs);
		setOfTrueEvents.add(obs.event);
	}

	//Inspired by the PNN and density based clustering methods
	public double conditionalProb(Observation obs) {

		double condProb = 0.0;

		//TODO: Should this have weighting factor for time and distance?
		for(Observation otherObs : observations) {
			double distance = Math.pow((obs.timeObserved-otherObs.timeObserved)/DisasterConstants.MAX_TIMESCALE_FOR_CLUSTERING,2);
			distance += Math.pow((obs.location.x-otherObs.location.x)/(DisasterConstants.XMAX-DisasterConstants.XMIN),2);
			distance += Math.pow((obs.location.y-otherObs.location.y)/(DisasterConstants.YMAX-DisasterConstants.YMIN),2);
			condProb += Math.exp(-distance);
		}

		//Get conditional probability, making sure to normalize by the size
		return condProb/observations.size();
	}
}
