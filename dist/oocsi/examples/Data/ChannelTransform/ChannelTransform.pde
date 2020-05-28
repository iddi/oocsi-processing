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
int positionx = 0;
int positiony = 0;

void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  OOCSI oocsi = new OOCSI(this, "transform_test_client", "localhost");

  // subscribe to channel "testchannel" with transform expression that computes a new value
  // position2 from the existing value position
  oocsi.subscribe("testchannel[transform(position2,20+200*(position/200*position/200))]", "testtransform");
}

void draw() {
  background(255);
  fill(fillColor, 120, 120);
  rect(positionx, positiony, 20, 20);
}

void testtransform(OOCSIEvent event) {

  // assign the new fill color from the OOCSI event
  fillColor = event.getInt("color", 0);

  // assign the new y position from the OOCSI event
  positiony = event.getInt("position", 0);
  
  // assign the new x position from the OOCSI event
  positionx = (int) event.getFloat("position2", 0);

}
