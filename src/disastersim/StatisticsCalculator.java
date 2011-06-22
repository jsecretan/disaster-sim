/**
 * A helper class to periodically calculate statistics in the simulation
 */

package disastersim;

import java.util.Vector;

import sim.engine.SimState;
import sim.engine.Steppable;

public class StatisticsCalculator implements Steppable {

	//The average ratio of awareness of events over existing events
	public Vector<Double> avgAwarenessRatio = new Vector<Double>();

	public Vector<Double> avgFalsePositives = new Vector<Double>();

	public Vector<Double> eventsInProgressVector = new Vector<Double>();

	public Vector<Double> numberOfObservations = new Vector<Double>();

	public StatisticsCalculator() {

	}

	public double calculateAverage(Vector<Double> v) {
		double toReturn = 0.0;

		for(Double d : v) {
			toReturn += d;
		}

		toReturn /= v.size();

		return toReturn;
	}

	public double getAverageDataSent(CooperativeObservation sim) {

		double dataSent = 0;

		for(DisasterAgent agent: sim.allAgents) {
			dataSent += agent.dataBytesSent;
		}

		return dataSent/sim.allAgents.size();

	}

	public double getAverageDataReceived(CooperativeObservation sim) {

		double dataReceived = 0;

		for(DisasterAgent agent: sim.allAgents) {
			dataReceived += agent.dataBytesReceived;
		}

		return dataReceived/sim.allAgents.size();

	}

	public double getAverageNumberOfObservations(CooperativeObservation sim) {

		double numberOfObs = 0;

		for(DisasterAgent agent: sim.allAgents) {
			numberOfObs += agent.observations.size();
		}

		return numberOfObs/sim.allAgents.size();

	}

	public void step(SimState state) {

		CooperativeObservation sim = (CooperativeObservation) state;


		int eventsInProgress = 0;
		//Count the number we are aware of in all of the events
		//TODO: Create a set or something to make this faster
		for(Event e : sim.allEvents) {

			if(e.isInProgress()){
				eventsInProgress++;
			}
		}

		eventsInProgressVector.add((double) eventsInProgress);


		double aaR = 0.0;

		double fp = 0.0;

		for(DisasterAgent agent: sim.allAgents) {
			if(agent.awarenessRatio.size() > 0) {
				aaR += agent.awarenessRatio.lastElement();
				//fp += agent.falsePositives.lastElement();
			}
		}

		aaR /= sim.allAgents.size();

		//fp /= sim.allAgents.size();

		if(eventsInProgress > 0) {
			avgAwarenessRatio.add(aaR);
			numberOfObservations.add(getAverageNumberOfObservations(sim));
		}
		//avgFalsePositives.add(fp);


		sim.timeSeriesAwarenessRate.add(sim.schedule.getTime(), aaR);

		sim.timeSeriesEventsInProgress.add(sim.schedule.getTime(), (double) eventsInProgress);
		//sim.timeSeriesFalsePositives.add(sim.schedule.getTime(), fp);

		double avgNumberOfObs = 0.0;

		if(numberOfObservations.size() > 0) {
			avgNumberOfObs = numberOfObservations.lastElement();
		}

		System.out.println("For t="+sim.schedule.getTime()+", AAR="+calculateAverage(avgAwarenessRatio)+", Nobs="+avgNumberOfObs+
				", Data Sent="+getAverageDataSent(sim)+", Data Received="+getAverageDataReceived(sim));
	}

}
