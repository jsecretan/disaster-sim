package tests;

import java.util.Map;

import disastersim.DisasterConfiguration;
import disastersim.DisasterEventData;
import junit.framework.TestCase;

public class ConfigurationLoadTest extends TestCase {

	private DisasterConfiguration disasterConfig;

	public ConfigurationLoadTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_readEventDataFromXML() {

		DisasterConfiguration.fileName = "src/tests/DisasterEvents.xml";
		Map<String,DisasterEventData> disasterMap = DisasterConfiguration.getDisasterDataMap();

		assertEquals(3,disasterMap.size());

		DisasterEventData d = disasterMap.get("ELECTRICAL_ACCIDENT");

		assertEquals(24.0,d.getAverageDuration());
		assertEquals(10.0,d.getDevDuration());
		assertEquals(10.0,d.getAverageDiameter());
		assertEquals(5.0,d.getDevDiameter());
		assertEquals(5,d.getNumberOfEvents());

	}

}
