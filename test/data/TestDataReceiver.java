package data;

import java.util.Date;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * example application - receives color and position values from OOCSI, on the channel "datachannel"
 * 
 * @author matsfunk
 */
@SuppressWarnings("serial")
public class TestDataReceiver extends PApplet {

	int numberInt = 0;
	float numberFloat = 0;
	double numberDouble = 0;
	long numberLong = 0;
	String numberString = "";
	PVector numberObject = new PVector(0, 0);
	Date dateObject = new Date();

	public void setup() {
		size(200, 200);
		stroke(120);
		fill(120);
		textSize(10f);

		// open connection to local OOCSI server
		// (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
		OOCSI oocsi = new OOCSI(this, ("receiver_" + Math.random()).toString().substring(0, 12), "localhost");

		// subscribe to channel "datachannel"
		// either the channel name is used for looking for a handler method...
		oocsi.subscribe("datachannel");
		// ... or the handler method name can be given explicitly
		// oocsi.subscribe("datachannel", "datachannel");
	}

	public void draw() {
		background(255);

		fill(120);
		rect(20, numberInt, 20, 20);
		rect(50, numberFloat, 20, 20);
		rect(80, (float) numberDouble, 20, 20);
		rect(110, numberLong, 20, 20);
		text(numberString, 20, 20);
		text(dateObject.toString(), 20, height - 20);
		fill(220);
		rect(numberObject.x, numberObject.y, 20, 20);
	}

	public void datachannel(OOCSIEvent event) {

		// get and save integer value
		numberInt = event.getInt("integer", 0);

		// get and save float value
		numberFloat = event.getFloat("float", 0);

		// get and save double value
		numberDouble = event.getDouble("double", 0);

		// get and save long value
		numberLong = event.getLong("long", 0);

		// get and save string value
		numberString = event.getString("string");

		// get, cast and save array value
		float[] floatArray = (float[]) event.getObject("array");
		numberObject = new PVector(floatArray[0], floatArray[1]);

		// get, cast and save Date object value
		dateObject = (Date) event.getObject("object");
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "data.TestDataReceiver" });
	}
}
