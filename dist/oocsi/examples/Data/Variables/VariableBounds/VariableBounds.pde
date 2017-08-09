import nl.tue.id.oocsi.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

OOCSIInt ovX = null;
OOCSIInt ovY = null;

void setup() {
  size(300, 300);
  rectMode(CENTER);
  frameRate(30);

  // open connection to local OOCSI
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  OOCSI oocsi = new OOCSI(this, null, "localhost");

  // create OOCSI variable on channel "varChannel" with key "posX" and data type Integer (int)
  ovX = Constellation.createInteger(oocsi, "varChannel", "posX").min(100).max(200);
  // create OOCSI variable on channel "varChannel" with key "posY" and data type Integer (int)
  ovY = Constellation.createInteger(oocsi, "varChannel", "posY").min(100).max(200);
}

void draw() {
  background(0);
  stroke(255);
  fill(255);
  
  // draw rect at the position given by the variables
  // which are set by another OOCSI client and synchronized thru OOCSI automatically
  rect(ovX.get(), ovY.get(), 10, 10);
}