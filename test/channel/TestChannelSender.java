package channel;

import nl.tue.id.oocsi.OOCSI;
import processing.core.PApplet;

/**
 * example application - sends the mouse coordinates as color and position
 * values to OOCSI, on the channel "testchannel"
 * 
 * @author mfunk
 * 
 */
@SuppressWarnings("serial")
public class TestChannelSender extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi;

	public void setup() {
		size(200, 200);
		background(120);
		frameRate(10);

		// open connection to local OOCSI
		oocsi = new OOCSI(this, ("sender_" + Math.random()).toString()
				.substring(0, 10), "localhost");
	}

	public void draw() {

		// send a message to channel "testchannel" with the data items "color"
		// and "position"
		oocsi.channel("testchannel").data("color", mouseX)
				.data("position", mouseY).send();
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "channel.TestChannelSender" });
	}
}
