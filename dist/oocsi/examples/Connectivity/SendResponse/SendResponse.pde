import nl.tue.id.oocsi.*;
import nl.tue.id.oocsi.client.services.*;

// ******************************************************
// This examples requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// variables for fill color and size of rectangle
int fillColor = 255;
int size = 3;

OOCSI oocsi1;

// second OOCSI instance for making calls to the registered handlers
OOCSI oocsi2;

void setup() {
  size(200, 200);
  noStroke();
  rectMode(CENTER);

  // connect to OOCSI server running on the same machine (localhost)
  // with "receiverName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi1 = new OOCSI(this, "responder", "localhost");

  // register this first OOCSI client for responses
  // to call "numberNumer"
  oocsi1.register("newNumber");
  // and "newColor"
  oocsi1.register("newColor");

  // register can take also the name of the function as an optional second parameter:
  // oocsi1.register("newColor", "generateColor");

  // ---------------------------------------------------------------------------------

  // connect with a second OOCSI client for calling the first one
  oocsi2 = new OOCSI(this, "caller", "localhost");
}

// responder for calls to "newNumber", will receive the call message and then put the response data into 
// the response OOCSIData object, which is same as for OOCSI events and messages
void newNumber(OOCSIEvent event, OOCSIData response) {
  response.data("number", frameCount  % 20);
}

// responder for calls to "newColor", will receive the call message and then put the response data into 
// the response OOCSIData object, which is same as for OOCSI events and messages
void newColor(OOCSIEvent event, OOCSIData response) {
  int col = event.getInt("color", 0);
  response.data("color", (fillColor + col) % 255);
}

void draw() {
  background(0);

  // draw a rect with the given size and fill color
  fill(fillColor, 120, 120);
  rect(width/2., height/2., 5 * size, 5 * size);
}

void mousePressed() {

  // on mouse press call two different services and get new data...

  // first one for getting a new size
  // 1: create call
  OOCSICall call1 = oocsi2.call("newNumber", 200);
  // 2: send out and wait until either there is a response or the timeout has passed
  call1.sendAndWait();
  // 3: check for response
  if (call1.hasResponse()) {
    // 4: get data out of the first response
    size = call1.getFirstResponse().getInt("number", 0);
  }

  // second one for getting a new color
  // 1: create call with parameter "color", similar to normal OOCSI events
  OOCSICall call2 = oocsi2.call("newColor", 200).data("color", frameCount);
  // 2: send out and wait until either there is a response or the timeout has passed
  call2.sendAndWait();
  // 3: check for response
  if (call2.hasResponse()) {
    // 4: get data out of the first response
    fillColor = call2.getFirstResponse().getInt("color", 0);
  }
}