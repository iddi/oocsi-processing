package synchronization;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import processing.core.PApplet;

/**
 * example application - synchronizes with all other clients in the same channel "syncChannel" via OOCSI
 * 
 * @author matsfunk
 */
@SuppressWarnings("serial")
public class TestSynchronization extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi;

	// main synchronization variable
	int framesTimeout = 0;

	// cycle duration
	int duration = 30;

	// colors for locking visualization
	int colorReset = 0;

	public void setup() {
		size(200, 200);
		background(0);
		noStroke();
		frameRate(10);

		// open connection to local OOCSI
		// (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
		oocsi = new OOCSI(this, ("sync_" + Math.random()).toString().substring(0, 10) + System.currentTimeMillis(),
				"localhost");

		oocsi.subscribe("syncChannel", "syncChannel");
	}

	public void draw() {

		// fade away effect
		rectMode(CORNERS);
		fill(0, 20);
		rect(0, 0, width, height);

		// on blink
		if (framesTimeout-- <= 0) {
			// draw rectangle
			fill(255, 0, 100 + Math.max(0, colorReset));
			rectMode(CENTER);
			rect(width / 2, height / 2, 100, 100);

			// send a message to channel "syncChannel" without any data
			oocsi.channel("syncChannel").send();

			// add randomness for variation
			framesTimeout = (int) (duration + random(0, 2));
		}
	}

	// sync channel handler, receives sync events and adjusts own timing accordingly
	public void syncChannel(OOCSIEvent event) {

		// adjust own timing according to sync events sent by other clients
		if (framesTimeout < 5) {
			// subtract [0, 1] if only a little too late
			framesTimeout -= random(0, 1);
		} else if (framesTimeout > duration - 5) {
			// add [0, 1] if a little too early
			framesTimeout += random(0, 1);
		} else {
			// otherwise subtract more to adjust towards other timing
			framesTimeout -= random(1, 2);
		}

		// lock color if in sync with at least one other client
		if (framesTimeout % 30 < 3) {
			colorReset = 0;
		} else {
			// otherwise print out sync offset
			println(framesTimeout);
		}
	}

	// allow for mouse presses to unsync a client
	public void mousePressed() {
		framesTimeout = 0;
		colorReset = 155;
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "synchronization.TestSynchronization" });
	}
}
