package tests;

import java.util.Vector;

import sim.util.Double2D;

import junit.framework.TestCase;
import disastersim.DisasterAgent;
import disastersim.DisasterConfiguration;
import disastersim.DisasterEventData;
import disastersim.Event;
import disastersim.InterpretedEvent;
import disastersim.Observation;
import disastersim.SimpleObservationClusterer;

public class SOCTest extends TestCase {

	private SimpleObservationClusterer soc;

	public SOCTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		soc = new SimpleObservationClusterer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_clusterObservations() {

		//Create a vector of observations from different events

		//Create the test event
		DisasterEventData testEventDataA = new DisasterEventData("TEST-EVENT-TYPE-A", 15.0, 3.0, 10.0, 1.0, 1);

		DisasterConfiguration.getDisasterDataMap().put("TEST-EVENT-TYPE-A", testEventDataA);

		//These two events of the same type happen simultaneously but in different places
		Event testEvent1 = new Event(new Double2D(10.0, 100.0), "TEST-EVENT-1A", "TEST-EVENT-TYPE-A", 5.0);
		Event testEvent2 = new Event(new Double2D(100.0, 10.0), "TEST-EVENT-2A", "TEST-EVENT-TYPE-A", 5.0);

		//Create two observations of each event
		Observation obsEvent1User1 = new Observation("TEST-EVENT-TYPE-A", new Double2D(11.0, 101.0), 3.3, testEvent1);
		Observation obsEvent1User2 = new Observation("TEST-EVENT-TYPE-A", new Double2D(9.0, 99.5), 4.0, testEvent1);
		Observation obsEvent2User1 = new Observation("TEST-EVENT-TYPE-A", new Double2D(102.0, 9.5), 3.3, testEvent2);
		Observation obsEvent2User2 = new Observation("TEST-EVENT-TYPE-A", new Double2D(99.1, 8.9), 4.0, testEvent2);

		Vector<Observation> obsVec = new Vector<Observation>();
		obsVec.add(obsEvent1User1);
		obsVec.add(obsEvent1User2);
		obsVec.add(obsEvent2User1);
		obsVec.add(obsEvent2User2);

		//Run the clustering and make sure we get two interpreted events
		Vector<InterpretedEvent> interpretedEvents = soc.clusterObservations(obsVec);

		assertEquals(2, interpretedEvents.size());
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent1User1));
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent1User2));
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent2User1));
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent2User2));

		//Add some observations that are of the same event type, in a similar place, but a while later

		Event testEvent3 = new Event(new Double2D(100.0, 10.0), "TEST-EVENT-3A", "TEST-EVENT-TYPE-A", 5.0);
		Observation obsEvent3User1 = new Observation("TEST-EVENT-TYPE-A", new Double2D(99.1, 8.9), 500.0, testEvent3);
		obsVec.add(obsEvent3User1);

		interpretedEvents = soc.clusterObservations(obsVec);
		assertEquals(3, interpretedEvents.size());
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent3User1));

	}


}
