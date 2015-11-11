import nl.tue.id.oocsi.*;

// **************************************************
// This examples requires a running OOCSI server!
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// **************************************************

// reference to local OOCSI
OOCSI oocsi;

// main synchronization variable
int framesTimeout = 0;

// cycle duration
int duration = 30;

// colors for locking visualization
int colorReset = 0;

void setup() {
  size(200, 200);
  background(120);
  frameRate(10);

  // connect ot OOCSI server running on the same machine (localhost)
  // with a unique handle which will be the channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "sync_" + System.currentTimeMillis(), "localhost");

  // subscribe to the joint synchronization channel
  oocsi.subscribe("syncChannel", "syncChannel");
}

void draw() {

  // fade away effect
  rectMode(CORNERS);
  fill(0, 20);
  rect(0, 0, width, height);

  // on blink
  if (framesTimeout-- <= 0) {
    // draw rectangle
    fill(255, 0, 100 + Math.max(0, colorReset));
    rectMode(CENTER);
    rect(width / 2, height / 2, 100, 100);

    // send a message to channel "syncChannel" without any data
    oocsi.channel("syncChannel").send();

    // add randomness for variation
    framesTimeout = (int) (duration + random(0, 2));
  }
}

// sync channel handler, receives sync events and adjusts own timing accordingly
public void syncChannel(OOCSIEvent event) {
  
  // adjust own timing according to sync events sent by other clients
  if (framesTimeout < 5) {
    // subtract [0, 1] if only a little too late
    framesTimeout -= random(0, 1);
  } else if (framesTimeout > duration - 5) {
    // add [0, 1] if a little too early
    framesTimeout += random(0, 1);
  } else {
    // otherwise subtract more to adjust towards other timing
    framesTimeout -= random(1, 2);
  }

  // lock color if in sync with at least one other client
  if (framesTimeout % 30 < 3) {
    colorReset = 0;
  }
}

// allow for mouse presses to unsync a client so a short while
public void mousePressed() {
  framesTimeout = 0;
  colorReset = 155;
}