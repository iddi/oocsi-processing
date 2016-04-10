package state;

import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.StateMachine;
import processing.core.PApplet;

/**
 * example application - proposes a leader to be elected and waits for votes from other clients in the same channel
 * "electionChannel" via OOCSI
 * 
 * @author matsfunk
 */
@SuppressWarnings("serial")
public class TestState extends PApplet {

	OOCSI oocsi;
	StateMachine sm;

	int brightness = 255;

	public void setup() {
		size(200, 200);
		noStroke();
		background(0);
		frameRate(30);

		// open connection to local OOCSI
		// (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
		oocsi = new OOCSI(this, ("state_" + Math.random()).toString().substring(0, 20), "localhost");

		// create state machine
		sm = new StateMachine(oocsi);

		// add different states

		// add state normal constructor version
		sm.addState("rect", "blueBackground", "drawRect", "lightFill").connect("state_channel", "cornered");
		sm.addState("triangle", "redBackground", "drawTriangle", "darkFill").connect("state_channel", "pointy");

		// add state, telescope version
		sm.addState("ellipse").enter("redBackground").execute("drawEllipse").connect("state_channel", "round");

		// set starting state to "ellipse" state
		sm.set("ellipse");
	}

	public void draw() {
		// fade
		fill(0, 10);
		rect(0, 0, width, height);

		// move to center and rotate
		translate(width / 2f, height / 2f);
		rotate(radians(frameCount));
		fill(brightness, 70);

		// call update of state machine
		sm.execute();
	}

	public void redBackground() {
		background(255, 10, 10);
	}

	public void blueBackground() {
		background(10, 10, 250);
	}

	public void lightFill() {
		brightness = 255;
	}

	public void darkFill() {
		brightness = 110;
	}

	public void drawEllipse() {
		ellipse(15, 0, 30, 30);
	}

	public void drawRect() {
		rect(0, 0, 30, 30);
	}

	public void drawTriangle() {
		triangle(0, 0, 30, 30, 0, 30);
	}

	// allow for mouse presses to forget all roles for this client
	public void mousePressed() {
		if (frameCount % 3 == 0) {
			oocsi.channel("state_channel").data("cornered", "").send();
			sm.set("rect");
		} else if (frameCount % 3 == 1) {
			oocsi.channel("state_channel").data("round", "").send();
			sm.set("ellipse");
		} else {
			oocsi.channel("state_channel").data("pointy", "").send();
			sm.set("triangle");
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "--location=200,100", "state.TestState" });
		PApplet.main(new String[] { "--location=200,320", "state.TestState" });
		PApplet.main(new String[] { "--location=400,100", "state.TestState" });
		PApplet.main(new String[] { "--location=400,320", "state.TestState" });
		PApplet.main(new String[] { "--location=600,100", "state.TestState" });
		PApplet.main(new String[] { "--location=600,320", "state.TestState" });
		PApplet.main(new String[] { "--location=800,100", "state.TestState" });
		PApplet.main(new String[] { "--location=800,320", "state.TestState" });
	}
}
