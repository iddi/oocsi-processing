package direct;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.client.protocol.OOCSIEvent;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class TestDirectReceiver extends PApplet {

	int color = 255;
	int position = 0;

	public void setup() {
		size(200, 200);

		new OOCSI(this, "receiverName", "localhost");
	}

	public void draw() {
		background(255);

		stroke(120);
		fill(color);
		rect(20, position, 20, 20);
	}

	public void handleOOCSIEvent(OOCSIEvent event) {
		color = event.getInt("color", 0);
		position = event.getInt("position", 0);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "direct.TestDirectReceiver" });
	}
}
