import nl.tue.id.oocsi.*;

// ******************************************************
// This examples requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// event key that should be plotted
String key = "valueX";

// OOCSI connection to receive events
OOCSI oocsi;

// OOCSI variable that is set automatically from the network
OOCSIInt value;

void setup() {

  size(400, 200);
  background(255);
  frameRate(4);
  
  // draw axes
  stroke(100);
  line(48, 20, 48, height-18);
  line(46, height-20, width-48, height-20);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // with "receiverName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "simpledatachart", "localhost");

  // create a variable that receives values from the "chartChannel" under the given key
  // the default value is 0 and the variable will reset to the default value if there 
  // is no input after 1100 milliseconds
  value = Constellation.createInteger(oocsi, "chartChannel", key, 0, 220);
}

void draw() {
  rectMode(CORNER);
  
  // erase bar from previous round (300 samples)
  fill(255);
  rect(50 + frameCount % 300, 0, 1, height-20);

  // calculate plot position for given current value (to fit into plot range)
  // input values need to be between 0 and 500
  float mappedValue = map(value.get(), 0, 500, height - 20, 20);
  
  // calculate bar shade, input again between 0 and 500
  float mappedColor = map(value.get(), 0, 500, 255, 50);

  // plot value
  fill(0);
  rect(50 + frameCount % 300, mappedValue, 1, 1);
  
  // plot bar
  fill(mappedColor, mappedColor, 255, 150);
  rectMode(CORNERS);
  rect(50 + frameCount % 300, mappedValue, 51 + frameCount % 300, height-20);
}