package direct;

import nl.tue.id.oocsi.OOCSI;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class TestDirectSender extends PApplet {

	OOCSI oocsi;

	public void setup() {
		size(200, 200);
		background(120);

		oocsi = new OOCSI(this, "senderName", "localhost");
		frameRate(10);
	}

	public void draw() {
		oocsi.channel("receiverName").data("color", mouseX)
				.data("position", mouseY).send();
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "direct.TestDirectSender" });
	}
}
