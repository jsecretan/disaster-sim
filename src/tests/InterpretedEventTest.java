package tests;

import sim.util.Double2D;
import disastersim.Event;
import disastersim.InterpretedEvent;
import disastersim.Observation;
import junit.framework.TestCase;

public class InterpretedEventTest extends TestCase {

	private InterpretedEvent e;

	public InterpretedEventTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_conditionalProb() {

		InterpretedEvent e = new InterpretedEvent();
		e.addObservation(new Observation("EVENT-TYPE-1", new Double2D(1.0, 1.0), 1.0, new Event()));
		e.addObservation(new Observation("EVENT-TYPE-1", new Double2D(5.0, 2.5), 2.0, new Event()));

		Observation obsToTest = new Observation("EVENT-TYPE-1", new Double2D(2.0, 3.0), 3.0, new Event());

		assertEquals(0.00682, e.conditionalProb(obsToTest), 0.0001);
	}

}
