package tools;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import nl.tue.id.oocsi.EventRecorder;
import processing.core.PApplet;
import processing.core.PVector;

@SuppressWarnings("serial")
public class TestLooper extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi2;
	EventRecorder oocsiLooper;
	PVector pos = new PVector();

	public void setup() {
		size(200, 200);
		noStroke();

		// connect to OOCSI server running on the same machine (localhost)
		// with the ID "sequencer" for the looper
		OOCSI oocsi = new OOCSI(this, "sequencer", "localhost");
		oocsiLooper = new EventRecorder(oocsi, "sequelChannel");
		oocsiLooper.play();
		oocsiLooper.startRecording();

		// register a second OOCSI client on the same server that can send events and receive recorded events back
		oocsi2 = new OOCSI(this, "player", "localhost");
		oocsi2.subscribe("sequelChannel");
	}

	public void sequelChannel(OOCSIEvent event) {
		// event received from looper
		pos.x = event.getInt("x", 0);
		pos.y = event.getInt("y", 0);

		// plot it out
		drawEvent(pos.x, pos.y);
	}

	public void draw() {

		// fade previous screen
		fill(0, 10);
		rectMode(CORNER);
		rect(0, 0, width, height);

		// restart looper (trigger start and record at the same time)
		if (frameCount % 360 == 0) {
			oocsiLooper.startRecording();
			oocsiLooper.play();
		}
	}

	public void mousePressed() {
		// draw new event
		drawEvent(mouseX, mouseY);

		// save new event to loop
		oocsi2.channel("sequelChannel").data("x", mouseX).data("y", mouseY).send();
	}

	private void drawEvent(float x, float y) {
		// yellow fill for rectangle to plot out
		fill(255, 255, 0, 120);
		rectMode(CENTER);
		rect(x, y, 10, 10);
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "tools.TestLooper" });
	}

}
