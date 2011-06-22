package disastersim;

import java.util.Vector;

public interface ObservationClusterer {

	public Vector<InterpretedEvent> clusterObservations(Vector<Observation> obsVec);

}
