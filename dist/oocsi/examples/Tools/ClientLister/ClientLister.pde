import nl.tue.id.oocsi.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

OOCSI oocsi;

void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // with the ID "clientLister"
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "clientLister", "localhost");
  
  // frame rate of 1 let's this sketch check for clients every second
  frameRate(1);
}

void draw() {
	println("-----------------------------");

	// retrieve and immediately print list of clients
	println(oocsi.getClients());
}