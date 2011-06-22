package tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import junit.framework.TestCase;
import disastersim.DisasterAgent;
import disastersim.DisasterConfiguration;
import disastersim.DisasterConstants;
import disastersim.DisasterEventData;
import disastersim.Event;
import disastersim.InterpretedEvent;
import disastersim.Observation;
import sim.util.*;

public class DisasterAgentTest extends TestCase {

	public DisasterAgentTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_sendBackObservations() {

		//Make a set of observations for agent A
		Observation obs1 = new Observation("EVENT-TYPE-1", new Double2D(), 1.1, new Event());
		Observation obs2 = new Observation("EVENT-TYPE-2", new Double2D(), 1.5, new Event());
		HashSet<Observation> obsA = new HashSet<Observation>();
		obsA.add(obs1);
		obsA.add(obs2);

		//Set some observations for agent B
		Observation obs3 = new Observation("EVENT-TYPE-1", new Double2D(), 1.2, new Event());
		DisasterAgent agentB = new DisasterAgent(new Double2D(),"AGENT-B");
		agentB.observations.add(obs3);
		agentB.observations.add(obs2);

		//Make sure the function sends back the difference (only obs 3)

		Vector<Observation> retObs = agentB.sendBackObservations(obsA);

		assertEquals(1,retObs.size());

		assertEquals(obs3,retObs.firstElement());

		//Check that the data tallies are correct
		assertEquals(DisasterConstants.BYTES_PER_OBS_ID*2,agentB.dataBytesReceived);
		assertEquals(DisasterConstants.BYTES_PER_OBS,agentB.dataBytesSent);
	}

	public void test_receiveAndProcessInformation() {
		//Create a vector of observations from different events

		//Create the test event
		DisasterEventData testEventDataA = new DisasterEventData("TEST-EVENT-TYPE-A", 15.0, 3.0, 10.0, 1.0, 1);

		DisasterConfiguration.getDisasterDataMap().put("TEST-EVENT-TYPE-A", testEventDataA);

		//These two events of the same type happen simultaneously but in different places
		Event testEvent1 = new Event(new Double2D(10.0, 100.0), "TEST-EVENT-1A", "TEST-EVENT-TYPE-A", 3.0);
		Event testEvent2 = new Event(new Double2D(100.0, 10.0), "TEST-EVENT-2A", "TEST-EVENT-TYPE-A", 3.0);

		//Create two observations of each event
		Observation obsEvent1User1 = new Observation("TEST-EVENT-TYPE-A", new Double2D(11.0, 101.0), 3.3, testEvent1);
		Observation obsEvent1User2 = new Observation("TEST-EVENT-TYPE-A", new Double2D(9.0, 99.5), 4.0, testEvent1);
		Observation obsEvent2User1 = new Observation("TEST-EVENT-TYPE-A", new Double2D(102.0, 9.5), 3.3, testEvent2);
		Observation obsEvent2User2 = new Observation("TEST-EVENT-TYPE-A", new Double2D(99.1, 8.9), 4.0, testEvent2);

		//Create two agents
		DisasterAgent agentA = new DisasterAgent(new Double2D(25.0, 25.0), "TEST-AGENT-A");
		agentA.observations.add(obsEvent1User1);
		agentA.observations.add(obsEvent2User1);

		DisasterAgent agentB = new DisasterAgent(new Double2D(25.0, 25.0), "TEST-AGENT-B");
		agentB.observations.add(obsEvent1User2);
		agentB.observations.add(obsEvent2User2);

		agentA.receiveAndProcessInformation(agentB);

		Vector<InterpretedEvent> interpretedEvents = agentA.interpretedEvents;

		assertEquals(2,interpretedEvents.size());
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent1User1));
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent1User2));
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent2User1));
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent2User2));

		//Check that the data tallies are correct
		assertEquals(DisasterConstants.BYTES_PER_OBS*2,agentA.dataBytesReceived);

	}

	public void test_recomputeInterpretedEvents() {

		//Create a vector of observations from different events

		//Create the test event
		DisasterEventData testEventDataA = new DisasterEventData("TEST-EVENT-TYPE-A", 15.0, 3.0, 10.0, 1.0, 1);
		DisasterEventData testEventDataB = new DisasterEventData("TEST-EVENT-TYPE-B", 1.0, 0.5, 1.0, 0.5, 1);

		DisasterConfiguration.getDisasterDataMap().put("TEST-EVENT-TYPE-A", testEventDataA);
		DisasterConfiguration.getDisasterDataMap().put("TEST-EVENT-TYPE-B", testEventDataB);

		//These two events of the same type happen simultaneously but in different places
		Event testEvent1 = new Event(new Double2D(10.0, 100.0), "TEST-EVENT-1A", "TEST-EVENT-TYPE-A", 3.0);
		Event testEvent2 = new Event(new Double2D(100.0, 10.0), "TEST-EVENT-2A", "TEST-EVENT-TYPE-A", 3.0);

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
		DisasterAgent agentUnderTest = new DisasterAgent(new Double2D(25.0, 25.0), "TEST-AGENT");
		agentUnderTest.observations.addAll(obsVec);
		agentUnderTest.recomputeInterpretedEvents();
		Vector<InterpretedEvent> interpretedEvents = agentUnderTest.interpretedEvents;

		assertEquals(interpretedEvents.size(), 2);
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent1User1));
		assertTrue(interpretedEvents.lastElement().observations.contains(obsEvent1User2));
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent2User1));
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent2User2));


		//Add a new event that is in a similar place as the others, but is a different type
		Event testEvent4 = new Event(new Double2D(100.0, 10.0), "TEST-EVENT-1B", "TEST-EVENT-TYPE-B", 3.0);
		Observation obsEvent4User1 = new Observation("TEST-EVENT-TYPE-B", new Double2D(99.1, 8.9), 3.1, testEvent4);

		agentUnderTest.observations.add(obsEvent4User1);
		agentUnderTest.recomputeInterpretedEvents();
		interpretedEvents = agentUnderTest.interpretedEvents;

		assertEquals(interpretedEvents.size(), 3);
		assertTrue(interpretedEvents.firstElement().observations.contains(obsEvent4User1));

		Vector<Event> eventVector = new Vector<Event>();
		eventVector.add(testEvent1);
		eventVector.add(testEvent2);
		eventVector.add(testEvent4);

		assertEquals("Awareness ratio calculated incorrectly", 1.0, agentUnderTest.calculateAwarenessRatio(eventVector), 0.05);

	}

	public void test_expireObservationCache() {

		//Create the test event
		DisasterEventData testEventData = new DisasterEventData();
		testEventData.setAverageDiameter(1.0);
		testEventData.setAverageDuration(3.0);
		testEventData.setDevDiameter(0.5);
		testEventData.setDevDuration(0.5);
		testEventData.setName("TEST-EVENT-TYPE");

		DisasterConfiguration.getDisasterDataMap().put("TEST-EVENT-TYPE", testEventData);
		Event testEvent = new Event(new Double2D(), "TEST-EVENT-1", "TEST-EVENT-TYPE", 3.0);

		//Create two observations
		Observation obsExpired = new Observation("TEST-EVENT-TYPE", new Double2D(), 1.0, testEvent);
		Observation obsFresh = new Observation("TEST-EVENT-TYPE", new Double2D(), 3.0, testEvent);
		DisasterAgent agentUnderTest = new DisasterAgent(new Double2D(),"TEST-AGENT");
		agentUnderTest.observations.add(obsFresh);
		agentUnderTest.observations.add(obsExpired);

		//After we expire the cache, one should remain and one should not
		agentUnderTest.expireObservationCache(4.0);

		assertEquals(agentUnderTest.observations.size(),1);
		assertEquals(agentUnderTest.observations.contains(obsFresh),true);

	}
}
