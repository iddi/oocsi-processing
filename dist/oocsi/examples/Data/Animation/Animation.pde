import nl.tue.id.oocsi.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

OOCSI oocsi;
Animator animator;

void setup() {
  size(200, 200);
  background(120);
  frameRate(10);

  // connect to OOCSI server running on the same machine (localhost)
  // with "senderName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "animationSender", "localhost");
  
  // create the animator with a channel and message attribute to animate
  // here, we send animation messages to "animChannel" and the attribute
  // "servo1" will receive the different values
  animator = new Animator(oocsi, "animChannel", "servo1");
  
  // add steps (delay, value)
  animator.addStep(500, 100);
  // you can add a step with a label that will be added as "step" to the message
  animator.addStep(500, 200, "just a normal step");
  animator.addStep(500, 300);
  animator.addStep(500, 200);
  // you can add random steps with a min and max delay and 
  // a min and max value (and finally a name)
  animator.addRandomStep(500, 800, 500, 1500, "random step here");
  animator.addStep(500, 100);
  animator.addStep(500, 0);
  // switch on looping for the animator
  animator.loop(true);
  // start the animator 
  animator.start();
}

void draw() {
  if(mouseY < height/2) {
    // start the animator from first step
    animator.start();
    
    // alternative: resume the animator from its last position
    // animator.resume();
  }
}

void mousePressed() {
  // stop the animator on click
  animator.stop();
}
