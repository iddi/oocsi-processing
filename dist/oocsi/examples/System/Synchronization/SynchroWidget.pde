import nl.tue.id.oocsi.*;
import nl.tue.id.oocsi.client.behavior.*;

public class SynchroWidget {

  // reference to local OOCSI
  OOCSI oocsi;

  OOCSISync os;

  //// main synchronization variable
  //int framesTimeout = 0;

  //// cycle duration
  //int duration = 30;

  boolean pulse = false;

  // colors for locking visualization
  int colorReset = 0;

  // positioning
  PVector pos;
  float width;
  float height;

  public SynchroWidget(PApplet parent, float x, float y, float width, float height) {
    this.pos = new PVector(x, y);
    this.width = width;
    this.height = height;

    // connect to OOCSI server running on the same machine (localhost)
    // with a unique handle which will be the channel others can send data to
    // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
    oocsi = new OOCSI(this, "sync_" + System.currentTimeMillis(), "localhost");

    // create OOCSI synchronization process
    os = Constellation.createSync(oocsi, "syncChannel", 2000, "pulse");
    
    // optional:
    // int randomResolution = 50 + (int) (Math.random() * 40);
    // println(randomResolution);
    // os.setResolution(randomResolution);
  }

  public void display() {
    translate(pos.x, pos.y);

    // on pulse
    if (pulse) {
      pulse = false;

      if (os.isSynced()) {
        fill(255, 0, 100);
      } else {
        fill(255, 0, 255);
      }

      // draw rectangle
      rect(0, 0, 100, 100);
    }

    // show permanent display of synchronization
    fill(0, 120, 255);
    int frames = os.getProgress();
    ellipse(sin(map(frames, 0, os.getResolution(), 0, TWO_PI) * 2) * 70,
        cos(map(frames, 0, os.getResolution(), 0, TWO_PI)) * 70, 10, 10);
  }

  public void pulse() {
    pulse = true;
  }

  public void mousePressed(int x, int y) {
    // check if mouse position is inside the widget
    if (Math.abs(pos.x - x) < 50 && Math.abs(pos.y - y) < 50) {
      // if yes, unsync this widget
      os.reset();
      colorReset = 155;
    }
  }
}
