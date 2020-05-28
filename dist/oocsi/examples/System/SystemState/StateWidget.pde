import nl.tue.id.oocsi.*;

public class StateWidget {

  // reference to local OOCSI
  OOCSI oocsi;

  // state machine
  StateMachine sm;

  // brightness 
  int brightness = 255;

  // positioning
  PVector pos;
  float width;
  float height;

  // widget colors
  float red, green, blue, rotation, backgroundStyle;

  public StateWidget(float x, float y, float width, float height) {
    this.pos = new PVector(x, y);
    this.width = width;
    this.height = height;

    // generate random color components for thsi widget
    red = random(110, 250);
    green = random(80, 190);
    blue = random(200, 255);

    // generate random rotation
    rotation = random(10, 1000);

    // connect to OOCSI server running on the same machine (localhost)
    // with a unique handle which will be the channel others can send data to
    // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
    oocsi = new OOCSI(this, "state_" + System.currentTimeMillis(), "localhost");

    // create state machine
    sm = new StateMachine(oocsi);

    // add different states -----------------------------------------------------------

    // add state normal constructor version
    sm.addState("rect", "blueBackground", "drawRect", "lightFill")
      // connect this state to an event key "cornered" to be received on state_channel
      .connect("state_channel", "cornered");
      
    // add, telescope version
    sm.addState("triangle")
      // function to call when entering this state (once per state change)
      .enter("redBackground")
      // function to call when in this state (every frame) 
      .execute("drawTriangle")
      // function to call when exiting from this state (once per state change)
      .exit("darkFill")
      // connect this state to an event key "pointy" to be received on state_channel
      .connect("state_channel", "pointy");

    // add state, telescope version
    sm.addState("ellipse").enter("redBackground").execute("drawEllipse")
      // connect this state to an event key "round" to be received on state_channel
      .connect("state_channel", "round");

    // set starting state to "ellipse" state
    sm.set("ellipse");
  }

  public void display() {
    translate(pos.x, pos.y);

    // draw background rectangle
    rectMode(CENTER);
    if (backgroundStyle > 0) {
      fill(red, green, 0, 30);
    }
    if (backgroundStyle < 0) {
      fill(250, green, blue, 30);
    }
    rect(0, 0, width, height);

    // move to center and rotate
    rotate(radians(rotation++));
    fill(red, brightness, 0);
    noStroke();

    // call update of state machine
    sm.execute();
  }

  public void redBackground() {
    // change background style to reddish
    backgroundStyle = -1;
  }

  public void blueBackground() {
    // change background style to greenish
    backgroundStyle = 1;
  }

  public void lightFill() {
    // change the fill style of inner shapes
    brightness = 140;
  }

  public void darkFill() {
    // change the fill style of inner shapes
    brightness = 10;
  }

  public void drawEllipse() {
    // draw ellipse in "round" mode (with variable fill style and background)
    rect(0, 0, 30, 30);
    ellipse(30 + sin(frameCount/10.)*10, 0, 10, 10);
  }

  public void drawRect() {
    // draw rectangle in "cornered" mode
    rect(0, 0, 30, 30);
    fill(0);
    rect(0, 0, 16, 16);
  }

  public void drawTriangle() {
    // draw rect + triangle in "pointy" mode (with variable fill style and background)
    rect(0, 0, 30, 30);
    triangle(-15, -15, 15, 15, -30 + cos(frameCount/10.) * 10, 30 + sin(frameCount/10.) * 10);
  }

  public void mousePressed(int x, int y) {
    // send signal to change system state, depending on current frame count
    // (to introduce a bit of non-linear behavior)
    if (frameCount % 3 == 0) {
      oocsi.channel("state_channel").data("cornered", "").send();
    } else if (frameCount % 3 == 1) {
      oocsi.channel("state_channel").data("round", "").send();
    } else {
      oocsi.channel("state_channel").data("pointy", "").send();
    }
  }
}
