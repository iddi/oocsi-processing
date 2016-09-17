package tools;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import nl.tue.id.oocsi.EventRecorder;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class TestRecorder extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi2;
	EventRecorder oocsiSequencer;

	public void setup() {
		size(200, 200);
		noStroke();

		// connect to OOCSI server running on the same machine (localhost)
		// with the ID "sequencer" for the looper
		OOCSI oocsi = new OOCSI(this, "sequencer", "localhost");
		oocsiSequencer = new EventRecorder(oocsi, "sequelChannel");
		oocsiSequencer.startRecording();

		// register a second OOCSI client on the same server that can send events and receive recorded events back
		oocsi2 = new OOCSI(this, "player", "localhost");
		oocsi2.subscribe("sequelChannel");
	}

	public void sequelChannel(OOCSIEvent event) {
		println("recorded event: " + event.getString("text"));
	}

	public void draw() {

		// print out ping dot
		if (frameCount % 100 == 0) {
			System.out.print(".");
		}

		// sequence different events to be recorded
		if (frameCount == 100) {
			oocsi2.channel("sequelChannel").data("text", "one 1").send();
		} else if (frameCount == 200) {
			oocsi2.channel("sequelChannel").data("text", "two 2").send();
		} else if (frameCount == 240) {
			oocsi2.channel("sequelChannel").data("text", "three 3").send();
		} else if (frameCount == 400) {
			oocsi2.channel("sequelChannel").data("text", "four 4").send();
		} else if (frameCount == 800) {
			oocsi2.channel("sequelChannel").data("text", "five 5").send();
		}
		// start play-back
		else if (frameCount == 1200) {
			System.out.println("----------------------------------------------");
			System.out.println("let's play");
			System.out.println();
			oocsiSequencer.play();
		}
		// start second play-back
		else if (frameCount == 2200) {
			oocsiSequencer.saveSequence("midifile.mid");
			System.out.println("----------------------------------------------");
			System.out.println("let's play again");
			System.out.println();
			System.out.println();
			oocsiSequencer.play();
		}
		// start play-back from saved file
		else if (frameCount == 3500) {
			// load and play
			oocsiSequencer.loadSequence("midifile.mid");
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "tools.TestRecorder" });
	}

}
