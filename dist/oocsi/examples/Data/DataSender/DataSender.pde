import nl.tue.id.oocsi.*;
import java.util.*;

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
  background(120);
  frameRate(10);

  // connect to OOCSI server running on the same machine (localhost)
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "senderName_" + System.currentTimeMillis(), "localhost");
}

void draw() {

  float number = map(mouseY, 0, height, 50, 150);

  // send a message to channel "datachannel" with data items of different types
  oocsi.channel("datachannel")
    // integer type number
    .data("integer", (int) number)
    // float type number
    .data("float", (float) number)
    // double type number
    .data("double", (double) number)
    // long type number
    .data("long", (long) number)
    // number as string
    .data("string", "" + number)
    // number and mouseX as array
    .data("array", new float[] { map(mouseX, 0, width, 20, width - 20), number })
    // number and mouseX as object
    .data("object", new Date())
    // send all
    .send();
}
