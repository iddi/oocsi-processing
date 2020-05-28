import nl.tue.id.oocsi.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

int fillColor = 255;
int position = 0;

void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  OOCSI oocsi = new OOCSI(this, "filter_test_client", "localhost");

  // subscribe to channel "testchannel" with a filter on the event 
  // attribute 'position': position needs to be larger than 120 to receive the event
  oocsi.subscribe("testchannel[filter(position<120)]", "testfilter");
}

void draw() {
  background(255);
  fill(fillColor, 120, 120);
  rect(20, position, 20, 20);
}

void testfilter(OOCSIEvent event) {

  // assign the new fill color from the OOCSI event
  fillColor = event.getInt("color", 0);

  // assign the new y position from the OOCSI event
  position = event.getInt("position", 0);
  
}
