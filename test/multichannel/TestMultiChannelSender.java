package multichannel;

import nl.tue.id.oocsi.OOCSI;
import processing.core.PApplet;

/**
 * example application - sends the mouse coordinates as color and position values to OOCSI, on the channel "testchannel"
 * 
 * @author mfunk
 * 
 */
@SuppressWarnings("serial")
public class TestMultiChannelSender extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi;

	public void setup() {
		size(200, 200);
		background(120);
		frameRate(10);

		// open connection to local OOCSI
		oocsi = new OOCSI(this, ("sender_" + Math.random()).toString().substring(0, 10), "localhost");
	}

	public void draw() {

		// send a message to channel "testchannel1" with the data items "color"
		// and "position"
		oocsi.channel("testchannel1").data("color", (int) map(sin(frameCount / 30.f), -1, 1, 100, 255))
				.data("position", mouseY).send();
		// send a message to channel "testchannel2" with the data items "color"
		// and "position"
		oocsi.channel("testchannel2").data("color", (int) map(cos(frameCount / 30.f), -1, 1, 100, 255))
				.data("position", mouseX).send();
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "multichannel.TestMultiChannelSender" });
	}
}
