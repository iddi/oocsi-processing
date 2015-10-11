import nl.tue.id.oocsi.*;

OOCSI oocsi;

void setup() {
  size(200, 200);
  background(120);
  frameRate(10);

  // connect ot OOCSI server running on the same machine (localhost)
  // with "senderName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "senderName", "localhost");
}

void draw() {
  // send to OOCSI ...
  oocsi
    // on channel "testchannel"...
  .channel("testchannel")
    // data labeled "color"...
    .data("color", mouseX)
      // data labeled "position"...
      .data("position", mouseY)
        // send finally
        .send();
}

