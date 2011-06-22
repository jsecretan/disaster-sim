package disastersim;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root
public class DisasterConfiguration {

	@ElementList
	public List<DisasterEventData> disasterEvents;

	public static String fileName = "conf/DisasterEvents.xml";

	private static Map<String,DisasterEventData> disasterDataMap = new HashMap<String,DisasterEventData>();
	private static boolean disasterMapLoaded = false;

	public List<DisasterEventData> getDisasterEvents() {
		return disasterEvents;
	}

	public void setDisasterEvents(List<DisasterEventData> disasterEvents) {
		this.disasterEvents = disasterEvents;
	}

	private static void loadDisasterDataMap() {

		Serializer serializer = new Persister();

	    try {
	    	DisasterConfiguration disasterConfiguration = serializer.read(DisasterConfiguration.class, new File(fileName));

	    	for(DisasterEventData d : disasterConfiguration.getDisasterEvents()) {
	    		disasterDataMap.put(d.getName(), d);
	    	}
	    }
        catch(Exception e) {
        	e.printStackTrace();
        }
	}

	public static Map<String,DisasterEventData> getDisasterDataMap() {

		if(!disasterMapLoaded) {
			loadDisasterDataMap();
		}

		return disasterDataMap;
	}

}
