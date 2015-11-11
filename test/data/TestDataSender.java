package data;

import java.util.Date;

import nl.tue.id.oocsi.OOCSI;
import processing.core.PApplet;

/**
 * example application - sends the mouse coordinates as color and position values to OOCSI, on the channel "datachannel"
 * 
 * @author matsfunk
 */
@SuppressWarnings("serial")
public class TestDataSender extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi;

	public void setup() {
		size(200, 200);
		background(120);
		frameRate(10);

		// open connection to local OOCSI
		// (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
		oocsi = new OOCSI(this, ("sender_" + Math.random()).toString().substring(0, 10), "localhost");
	}

	public void draw() {

		float number = map(mouseY, 0, height, 50, 150);

		// send a message to channel "datachannel" with the data items "color"
		// and "position"
		oocsi.channel("datachannel")
		// integer type number
				.data("integer", (int) number)
				// float type number
				.data("float", (float) number)
				// double type number
				.data("double", (double) number)
				// long type number
				.data("long", (long) number)
				// number as string
				.data("string", "" + number)
				// number and mouseX as array
				.data("array", new float[] { map(mouseX, 0, width, 20, width - 20), number })
				// number and mouseX as object
				.data("object", new Date())
				// send all
				.send();
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "data.TestDataSender" });
	}
}
