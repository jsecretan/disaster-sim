/*
  Copyright 2010 by Jimmy Secretan
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package disastersim;

import sim.portrayal.continuous.*;
import sim.portrayal.network.*;
import sim.util.media.chart.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import java.awt.Color;


public class CooperativeObservationWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;

	ContinuousPortrayal2D coPortrayal = new ContinuousPortrayal2D();
	NetworkPortrayal2D edgePortrayal = new NetworkPortrayal2D();

	public static void main(String[] args) {
		CooperativeObservationWithUI co = new CooperativeObservationWithUI();
		Console c = new Console(co);

		c.setVisible(true);
	}

	public CooperativeObservationWithUI() {
		super(new CooperativeObservation(System.currentTimeMillis()));
	}

	public CooperativeObservationWithUI(SimState state) {
		super(state);
	}

	public static String getName() {
		return "Disaster Sensing Collaborative Filtering";
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {

		CooperativeObservation co = (CooperativeObservation) state;

		// tell the portrayals what to portray and how to portray them
		coPortrayal.setField(co.environment);

        // tell the portrayals what to portray and how to portray them
        edgePortrayal.setField( new SpatialNetwork2D( co.environment, co.deviceCommunication ) );
        edgePortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());


		// reschedule the displayer
		display.reset();
		display.setBackdrop(Color.white);

		// redraw the display
		display.repaint();
	}

	private ChartGenerator getChart() {

		TimeSeriesChartGenerator sg = new TimeSeriesChartGenerator();

		sg.setTitle("Event Occurrence and Awareness through Time");

		CooperativeObservation co = (CooperativeObservation) state;

		sg.addSeries(co.timeSeriesAwarenessRate, null);

		sg.addSeries(co.timeSeriesEventsInProgress, null);

		sg.setRangeAxisLabel("Events");

		sg.setDomainAxisLabel("Time (minutes)");

		//sg.addSeries(co.timeSeriesFalsePositives, null);

		return sg;
	}

	public void init(Controller c) {
		super.init(c);

		// make the displayer
		display = new Display2D(600, 600, this, 1);

		displayFrame = display.createFrame();
		displayFrame.setTitle("Disaster Sensing Collaborative Filtering");
		c.registerFrame(displayFrame); // register the frame so it appears in
										// the "Display" list
		displayFrame.setVisible(true);
		display.attach(coPortrayal, "Agents");
		display.attach( edgePortrayal, "Communication" );

		Console con = (Console) c;

		//Add the charting
		con.getTabPane().add(getChart());
	}

	public void quit() {
		super.quit();

		if (displayFrame != null)
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

}
