
// ******************************************************
// This examples requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************


StateWidget[] widgets = new StateWidget[8];

void setup() {
  size(800, 350);
  background(120);
  frameRate(10);

  // create new widgets and position them in a grid 
  for (int i = 0; i < widgets.length; i++) {
    widgets[i] = new StateWidget(100 + 200 * i - (800 * round(i/7.)), 100 + round(i/7.) * 150, 100, 100);
  }
}

void draw() {

  // global fade away effect
  rectMode(CORNERS);
  fill(0, 60);
  rect(0, 0, width, height);

  // draw all widgets
  for (int i = 0; i < widgets.length; i++) {
    pushMatrix();
    widgets[i].display();
    popMatrix();
  }
}

// allow for mouse presses to switch a state for all clients
public void mousePressed() {

  // check mouse pressed for all widgets
  for (int i = 0; i < widgets.length; i++) {
    widgets[i].mousePressed(mouseX, mouseY);
  }
  
  // delete all visuals
  background(0);
}