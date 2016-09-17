import nl.tue.id.oocsi.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// reference to local OOCSI
OOCSI oocsi;
EventRecorder oocsiEventRecorder;

// drawing helper
PVector pos = new PVector();

public void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  // This client can send events and receive recorded events back
  oocsi = new OOCSI(this, "player", "localhost");
  // subscribe to the sequencer channel, so events will be sent to the function "sequelChannel" (see below)
  oocsi.subscribe("sequelChannel");

  // register a second OOCSI client with the ID "recorder" for the recorder
  OOCSI oocsiSeq = new OOCSI(this, "recorder", "localhost");
  // initialize event recorder to use this second OOCSI client and record from channel "sequelChannel" 
  oocsiEventRecorder = new EventRecorder(oocsiSeq, "sequelChannel");

  // immediately start play-back and recording
  oocsiEventRecorder.play();
  oocsiEventRecorder.startRecording();
}

void draw() {
  // fade previous screen
  fill(0, 10);
  rectMode(CORNER);
  rect(0, 0, width, height);

  // draw rect at current position
  drawEvent(pos.x, pos.y);

  // set position to something off-screen, so it won't show
  pos.x = -10;
  pos.y = -10;

  // loop the looper (trigger start and record at the same time)
  if (frameCount % 360 == 0) {
    oocsiEventRecorder.startRecording();
    oocsiEventRecorder.play();
  }
}

void mousePressed() {
  // draw new event
  drawEvent(mouseX, mouseY);

  // save new event to loop
  oocsi.channel("sequelChannel").data("x", mouseX).data("y", mouseY).send();
}

// receive events from OOCSI (sent by the recorder upon play-back)
void sequelChannel(OOCSIEvent event) {
  // save x and y from event received from looper
  pos.x = event.getInt("x", 0);
  pos.y = event.getInt("y", 0);
}

void drawEvent(float x, float y) {
  // yellow fill for rectangle to plot out
  fill(255, 255, 0, 200);
  rectMode(CENTER);
  rect(x, y, 10, 10);
}