import javax.sound.midi.MidiChannel;
import nl.tue.id.oocsi.EventRecorder;
import nl.tue.id.oocsi.OOCSI;
import nl.tue.id.oocsi.OOCSIEvent;
import processing.core.PApplet;
import processing.core.PVector;
import themidibus.MidiBus;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// reference to local OOCSI
OOCSI oocsiPlayer;

// OOCSI event recorder 
EventRecorder oocsiLooper;

// MIDI
MidiChannel channel;
MidiBus mb;

// visualization
PVector pos = new PVector();
int lastPitch = 0;

public void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // with the ID "sequencer" for the looper which listens for events and plays them back
  OOCSI oocsiSequencer = new OOCSI(this, "sequencer", "localhost");
  oocsiLooper = new EventRecorder(oocsiSequencer, "sequelChannel");
  
  // autostart
  oocsiLooper.play();
  oocsiLooper.startRecording();

  // register a second OOCSI client on the same server that can send events and
  // receive recorded events back
  oocsiPlayer = new OOCSI(this, "player", "localhost");
  oocsiPlayer.subscribe("sequelChannel");

  // connect MIDI sub system, first list MIDI devices
  MidiBus.list();
  // then connect to one of them
  mb = new MidiBus(this, -1, "Bus 1");
}

public void sequelChannel(OOCSIEvent event) {
  // event received from looper
  pos.x = event.getInt("x", 0);
  pos.y = event.getInt("y", 0);

  // plot it out
  drawEvent(pos.x, pos.y);
}

public void draw() {

  // fade previous screen
  fill(0, 10);
  rectMode(CORNER);
  rect(0, 0, width, height);

  // restart looper after a certain amount of frames (trigger start and record at the same time)
  if (frameCount % 360 == 0) {
    oocsiLooper.startRecording();
    oocsiLooper.play();
  }
}

public void mousePressed() {
  
  // play new event
  playEvent(mouseX);
  
  // draw new event
  drawEvent(mouseX, mouseY);

  // save new event to loop
  oocsiPlayer.channel("sequelChannel").data("x", mouseX).data("y", mouseY).send();
}

// play MIDI event
void playEvent(float x) {
  int channel = 1;
  int pitch = (int) map(x, 0, width, 40, 90);
  int velocity = 80;
  mb.sendNoteOff(channel, lastPitch, velocity);
  mb.sendNoteOn(channel, pitch, velocity);
  lastPitch = pitch;
}

// draw MIDI event
void drawEvent(float x, float y) {
  // yellow fill for rectangle to plot out
  fill(255, 255, 0, 120);
  rectMode(CENTER);
  rect(x, y, 10, 10);
}