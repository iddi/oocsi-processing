import nl.tue.id.oocsi.*;

// **************************************************
// This examples requires a running OOCSI server!
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// **************************************************

int fillColor = 255;
int position = 0;

void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // with "receiverName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  OOCSI oocsi = new OOCSI(this, "receiverName", "localhost");

  // subscribe to channel "testchannel"
  // either the channel name is used for looking for a handler method...
  oocsi.subscribe("testchannel");
  // ... or the handler method name can be given explicitly
  // oocsi.subscribe("testchannel", "testchannel");
}

void draw() {
  background(255);
  fill(fillColor, 120, 120);
  rect(20, position, 20, 20);
}

void testchannel(OOCSIEvent event) {

  // assign the new fill color from the OOCSI event
  color = event.getInt("color", 0);

  // assign the new y position from the OOCSI event
  position = event.getInt("position", 0);
}
