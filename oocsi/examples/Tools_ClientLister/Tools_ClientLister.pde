import nl.tue.id.oocsi.*;

OOCSI oocsi;

void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // with the ID "clientLister"
  oocsi = new OOCSI(this, "clientLister", "localhost");
  
  // frame rate of 1 let's this sketch check for clients every second
  frameRate(1);
}

void draw() {
	println("-----------------------------")

	// retrieve and immediately print list of clients
	println(oocsi.clients());
}
