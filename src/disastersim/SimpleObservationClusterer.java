package disastersim;

import java.util.HashMap;
import java.util.Vector;

public class SimpleObservationClusterer implements ObservationClusterer {

	public Vector<InterpretedEvent> clusterObservations(Vector<Observation> obsVec) {

		Vector<InterpretedEvent> interpretedObs = new Vector<InterpretedEvent>();

		HashMap<String,Vector<Observation> > obsByEventType = new HashMap<String,Vector<Observation> >();

		//First, break down these observations by event type
		for(Observation obs : obsVec) {

			if(obsByEventType.get(obs.eventType) == null) {
				obsByEventType.put(obs.eventType, new Vector<Observation>());
			}

			obsByEventType.get(obs.eventType).add(obs);
		}

		//For each event type, cluster the events
		for(Vector<Observation> obsForEvent : obsByEventType.values()) {

			Vector<InterpretedEvent> interpretedEventForType = new Vector<InterpretedEvent>();

			for(Observation obs : obsForEvent) {

				//If the observation is within tolerance of an existing event
				//(i.e. the time and space are close enough) then add it to that, otherwise
				//create a new interpreted event
				double bestObsMatch = 0.0;
				InterpretedEvent bestMatchEvent = null;

				for(InterpretedEvent curEvent : interpretedEventForType) {
					double condProb = curEvent.conditionalProb(obs);
					if(condProb > bestObsMatch) {
						bestObsMatch = condProb;
						bestMatchEvent = curEvent;
					}
				}

				if(bestObsMatch > DisasterConstants.CREATE_NEW_OBS_CUTOFF) { //Fit with existing

					bestMatchEvent.addObservation(obs);

				} else { //Create new one

					interpretedEventForType.add(new InterpretedEvent(obs));

				}

			}

			//Add all to the main list
			interpretedObs.addAll(interpretedEventForType);

		}

		return interpretedObs;

	}

}
