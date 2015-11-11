package tools;

import nl.tue.id.oocsi.OOCSI;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class TestClientLister extends PApplet {

	// reference to local OOCSI
	OOCSI oocsi;

	public void setup() {
		size(200, 200);
		noStroke();

		// connect to OOCSI server running on the same machine (localhost)
		// with the ID "clientLister"
		oocsi = new OOCSI(this, "clientLister", "probe.id.tue.nl");

		// frame rate of 1 let's this sketch check for clients every second
		frameRate(1);
	}

	public void draw() {
		println("-----------------------------");

		// retrieve and immediately print list of clients
		println(oocsi.getClients());
	}

}
