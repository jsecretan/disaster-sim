package disastersim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;
import sim.util.Double2D;


import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class AgentMovementData {

	public AgentMovementData() {}

	public AgentMovementData(String fileName) {
		loadAgentMovements(fileName);
	}

	//Each agent gets a separate vector.  Each vector contains its own vector of locations
	//Their position in the vector represents the timestep of the simulation
	private Vector< Vector<Double2D> > agentMovements = new Vector< Vector < Double2D >>();

	/**
	 *
	 * @param i : Id of user for which to retrieve data
	 * @return : The entire time series of movements
	 */
	public Vector<Double2D> getMapDataForUser(int i) {
		return agentMovements.get(i);
	}

	public void loadAgentMovements(String fileName) {

		try{
			BufferedReader bufRdr = new BufferedReader(new FileReader(fileName));
			String line = null;

			while((line = bufRdr.readLine()) != null) {
				Vector<Double2D> currentAgentMovements = new Vector<Double2D>();

				StringTokenizer st = new StringTokenizer(line,",");

				while (st.hasMoreTokens()) {
					currentAgentMovements.add(new Double2D(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken())));

					//For now, burn off the time item
					st.nextToken();
				}

				agentMovements.add(currentAgentMovements);
			}

			//close the file
			bufRdr.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
