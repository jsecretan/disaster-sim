package disastersim;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Element
public class DisasterEventData {

	@Attribute
	public String name;

	@Attribute
	public double averageDuration;

	@Attribute
	public double devDuration;

	@Attribute
	public double averageDiameter;

	@Attribute
	public double devDiameter;

	@Attribute
	public int numberOfEvents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAverageDuration() {
		return averageDuration;
	}

	public void setAverageDuration(double averageDuration) {
		this.averageDuration = averageDuration;
	}

	public double getDevDuration() {
		return devDuration;
	}

	public void setDevDuration(double devDuration) {
		this.devDuration = devDuration;
	}

	public double getAverageDiameter() {
		return averageDiameter;
	}

	public void setAverageDiameter(double averageDiameter) {
		this.averageDiameter = averageDiameter;
	}

	public double getDevDiameter() {
		return devDiameter;
	}

	public void setDevDiameter(double devDiameter) {
		this.devDiameter = devDiameter;
	}

	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	public DisasterEventData() {}

	public DisasterEventData(String name, double averageDuration,
			double devDuration, double averageDiameter, double devDiameter,
			int numberOfEvents) {
		super();
		this.name = name;
		this.averageDuration = averageDuration;
		this.devDuration = devDuration;
		this.averageDiameter = averageDiameter;
		this.devDiameter = devDiameter;
		this.numberOfEvents = numberOfEvents;
	}
}
