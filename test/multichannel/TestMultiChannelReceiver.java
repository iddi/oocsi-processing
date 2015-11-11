package multichannel;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import processing.core.PApplet;

/**
 * example application - receives color and position values from OOCSI, on the channel "testchannel"
 * 
 * @author mfunk
 * 
 */
@SuppressWarnings("serial")
public class TestMultiChannelReceiver extends PApplet {

	int color1 = 255;
	int color2 = 255;
	int position1 = 0;
	int position2 = 0;

	public void setup() {
		size(200, 200);

		// open connection to local OOCSI
		OOCSI oocsi = new OOCSI(this, ("receiver_" + Math.random()).toString().substring(0, 12), "localhost");

		// subscribe to channel "testchannel1" and "testchannel2"
		oocsi.subscribe("testchannel1");
		oocsi.subscribe("testchannel2");
	}

	public void draw() {
		background(255);

		stroke(120);
		fill(color1);
		rect(20, position1, 20, 20);
		fill(color2);
		rect(width - 40, position2, 20, 20);
	}

	public void testchannel1(OOCSIEvent event) {

		// save color value
		color1 = event.getInt("color", 100);

		// save position value
		position1 = event.getInt("position", 0);
	}

	public void testchannel2(OOCSIEvent event) {

		// save color value
		color2 = event.getInt("color", 0);

		// save position value
		position2 = event.getInt("position", 0);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "multichannel.TestMultiChannelReceiver" });
	}
}
