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

void setup() {
  size(200, 200);
  noStroke();

  // connect to OOCSI server running on the same machine (localhost)
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  // This client can send events and receive recorded events back
  oocsi = new OOCSI(this, "player", "localhost");
  // subscribe to the sequencer channel, so events will be sent to the function "recPlayChannel" (see below)
  oocsi.subscribe("recPlayChannel");

  // register a second OOCSI client with the ID "recorder" for the recorder
  OOCSI oocsiSeq = new OOCSI(this, "recorder", "localhost");
  // initialize event recorder to use this second OOCSI client and record from channel "recPlayChannel" 
  oocsiEventRecorder = new EventRecorder(oocsiSeq, "recPlayChannel");
  oocsiEventRecorder.startRecording();

  // print one empty line
  println();
  println("----------------------------------------------");
  println("recording events");
  println();
}

void draw() {

  // print out ping dot
  if (frameCount % 100 == 0) {
    print(".");
  }

  // sequence different events to be recorded
  if (frameCount == 100) {
    oocsi.channel("recPlayChannel").data("text", "one 1").send();
  } else if (frameCount == 200) {
    oocsi.channel("recPlayChannel").data("text", "two 2").send();
  } else if (frameCount == 240) {
    oocsi.channel("recPlayChannel").data("text", "three 3").send();
  } else if (frameCount == 400) {
    oocsi.channel("recPlayChannel").data("text", "four 4").send();
  } else if (frameCount == 800) {
    oocsi.channel("recPlayChannel").data("text", "five 5").send();
  }
  // start play-back
  else if (frameCount == 1200) {
    println();
    println("----------------------------------------------");
    println("let's play");
    println();
    oocsiEventRecorder.play();
  }
  // start second play-back
  else if (frameCount == 2200) {
    oocsiEventRecorder.saveSequence("midifile.mid");
    println();
    println("----------------------------------------------");
    println("let's play again");
    println();
    println();
    oocsiEventRecorder.play();
  }
  // start play-back from saved file
  else if (frameCount == 3500) {
    // load and play
    oocsiEventRecorder.loadSequence("midifile.mid");
  }
}

void recPlayChannel(OOCSIEvent event) {
  // print out event that is played back and received here
  println("recorded event: " + event.getString("text"));
}