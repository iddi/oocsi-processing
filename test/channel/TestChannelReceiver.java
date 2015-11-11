package channel;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import processing.core.PApplet;

/**
 * example application - receives color and position values from OOCSI, on the channel "testchannel"
 * 
 * @author matsfunk
 */
@SuppressWarnings("serial")
public class TestChannelReceiver extends PApplet {

	int color = 255;
	int position = 0;

	public void setup() {
		size(200, 200);

		// open connection to local OOCSI server
		// (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
		OOCSI oocsi = new OOCSI(this, ("receiver_" + Math.random()).toString().substring(0, 12), "localhost");

		// subscribe to channel "testchannel"
		// either the channel name is used for looking for a handler method...
		oocsi.subscribe("testchannel");
		// ... or the handler method name can be given explicitly
		// oocsi.subscribe("testchannel", "testchannel");
	}

	public void draw() {
		background(255);

		stroke(120);
		fill(color);
		rect(20, position, 20, 20);
	}

	public void testchannel(OOCSIEvent event) {

		// save color value
		color = event.getInt("color", 0);

		// save position value
		position = event.getInt("position", 0);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "channel.TestChannelReceiver" });
	}
}
