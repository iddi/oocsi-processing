import nl.tue.id.oocsi.*;
import nl.tue.id.oocsi.client.behavior.*;

public class DiscoveryWidget {

  // reference to local OOCSI
  OOCSI oocsi;

  // discovery process
  OOCSIGather og;

  // brightness 
  int brightness = 255;

  // positioning
  PVector pos;
  float width;
  float height;

  public DiscoveryWidget(float x, float y, float width, float height) {
    this.pos = new PVector(x, y);
    this.width = width;
    this.height = height;

    // connect to OOCSI server running on the same machine (localhost)
    // with a unique handle which will be the channel others can send data to
    // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
    oocsi = new OOCSI(this, "brightness_" + System.currentTimeMillis(), "oocsi.id.tue.nl");
    og = Constellation.createIntegerGather(oocsi, "discoveryChannel", "state", 1000);
    
    brightness = int(random(1, 6));
    og.set(brightness);
  }

  public void display() {
    translate(pos.x, pos.y);

    // draw background rectangle
    rectMode(CENTER);
    fill(0);
    rect(0, 0, width, height);

    // move to center and rotate
    rotate(radians(frameCount++));
    fill(100 + (brightness * 20));
    noStroke();
    rect(width/2, height/2, 30, 30);
  }

  public void mousePressed(int x, int y) {
    // randomize my brightness
    brightness = int(random(1, 6));

    // send out my new brightness value to awareness process
    og.set(brightness);
  }
}
