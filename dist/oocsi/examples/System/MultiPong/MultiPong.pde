import java.util.*;
import java.util.concurrent.*;
import nl.tue.id.oocsi.*;

// ******************************************************
// This examples requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// position of ball
PVector pos = new PVector(100, 100);

// speed and direction of ball
PVector speed = new PVector(0.6, 0.62);

// center point
PVector center;
float bevelSpace;

// number of players
int PLAYERS = 0;

// map of players
Map<String, Player> players = new ConcurrentHashMap<String, Player>();

// OOCSI connection
OOCSI oocsi;

// my own player name
String name;

void setup() {
  size(600, 400);
  frameRate(30);
  center = new PVector(height/2., height/2.);

  // connect to OOCSI with randomly generated name
  name = "pong" + random(100);
  oocsi = new OOCSI(this, name, "localhost");
  
  // subscribe to multipong channel
  oocsi.subscribe("multipong");

  // add myself as a player
  addPlayer(name);
}

void draw() {

  // erase background
  fill(0, 20);
  rect(0, 0, width, height);

  // abort if there are no players (at the beginning of the game)
  if (players.size() == 0) {
    return;
  }

  // player legend
  int count = 0;
  for (String pName : players.keySet()) {
    noStroke();
    Player p = players.get(pName);
    stroke(p.getColor());
    if (p.isActive()) {
      fill(p.getColor());
      rect(width * 0.75, 100 + count * 20, 10, 10);
    } else {
      fill(p.getColor(), 10);
      rect(width * 0.75, 105 + count * 20, 10, 1);
    }

    fill(p.getColor());
    textSize(10);
    text(pName, width * 0.8, 110 + count++ * 20);
  }

  // rotate playing field
  translate(height/2., height/2.);
  rotate(radians(frameCount/20.));
  translate(-height/2., -height/2.);

  // basic motion of ball
  pos.add(speed);

  // draw different players (own player in white)
  for (Player p : players.values()) {
    p.draw();
  }

  // draw ball
  stroke(#999999);
  strokeWeight(1);
  if (pos.dist(center) >= height/1.9) {
    fill(250 - 10*(pos.dist(center) - height/1.9));
    ellipse(pos.x, pos.y, 2*(pos.dist(center) - height/1.9), 2*(pos.dist(center) - height/1.9));
  } else {
    fill(255);
    ellipse(pos.x, pos.y, 10, 10);
  }

  // out of bounds check, reset to center point
  PVector center = new PVector(height/2., height/2.);
  if (pos.dist(center) >= height/1.5) {

    // check who lost it
    for (Player p : players.values()) {
      if (p.isActive()) {
        p.lost();
        break;
      }
    }

    // generate new ball with random direction and motion
    pos.set(height/2. + random(-30, 30), height/2. + random(-30, 30));
    speed.rotate(radians(random(30, 80)));
  }

  // update local data to OOCSI network every 10 frames
  if (frameCount % 10 == 0) {
    oocsi.channel("multipong")
      .data("posx", pos.x)
      .data("posy", pos.y)
      .data("spdx", speed.x)
      .data("spdy", speed.y)
      .data("bevel", map(mouseX, 10, width-10, 0, bevelSpace)).send();
  }

  // either add a new player
  if (players.containsKey(name)) {
    players.get(name).bevelControl = map(mouseX, 10, width-10, 0, bevelSpace);
    players.get(name).lastUpdate = millis();
  } 
  // or update an existing player
  else {
    println("update" + name);
  }

  // clean players
  for (Player p : players.values()) {
    if (p.lastUpdate < millis() - 3000) {
      removePlayer(p);
      break;
    }
  }
}

// receive messages about the multipong game 
void multipong(OOCSIEvent event) {

  // global
  pos.x = event.getFloat("posx", pos.x);
  pos.y = event.getFloat("posy", pos.y);
  speed.x = event.getFloat("spdx", speed.x);
  speed.y = event.getFloat("spdy", speed.y);

  // player bevel locations
  String sender = event.getSender();
  if (players.containsKey(sender)) {
    float bevel = event.getFloat("bevel", 0);
    players.get(sender).bevelControl = bevel;
    players.get(sender).lastUpdate = millis();
  } else {
    addPlayer(sender);
  }
}

void addPlayer(String name) {

  // erase backgrond if there is a new player
  background(0);

  // add new player
  players.put(name, new Player(PLAYERS));
  println("player added");

  // how many players are now active?
  PLAYERS++;

  // update all players
  int count = 0;
  for (Player p : players.values()) {
    p.layout(count++);
  }
}

void removePlayer(Player remove) {

  // find and remove player
  for (String key : players.keySet()) {
    if (players.get(key) == remove) {
      players.remove(key);
      println("player removed");
      break;
    }
  }

  // update all remaining players
  int count = 0;
  for (Player p : players.values()) {
    p.layout(count++);
  }
}

///////////////////////////////////////////////////////////////////////

class Player extends PVector {

  // location of player's bevel
  PVector bevel;
  
  // is this player still online?
  long lastUpdate;
  
  // bevel control value
  float bevelControl;
  
  // lives left for this player
  int lives = 3;
  
  // color of player details
  int col;
  
  // color array
  color [] cols;

  // initialize a player 
  public Player(int col) {
    super(0, 0, 0);
    this.col = col;
    bevel = new PVector(0, 0);
    cols = new color[] {#ffffff, #a043a9, #0aa3e9, #d3d099, #00e3e9, #ee0472, #4410dd, #04f7fe};
    lastUpdate = millis();
  }

  public void draw() {

    noFill();
    stroke(getColor());

    // play field
    if (isActive()) {
      fill(getColor(), 10);
    }
    strokeWeight(1);
    for (int i = 0; i < lives; i++) {
      if (i > 0) {
        noFill(); 
        stroke(getColor(), 60 - i*10);
      } 
      arc(height/2., height/2., height*0.9 + i * 15, height*0.9 + i * 15, radians(x - 90), radians(y - 90));
    }

    PVector b = bevel;
    PVector cspeed = PVector.sub(pos, center);

    float angle = (degrees(cspeed.heading()) + 90 + 360) % 360;
    if (angle > b.x + bevelControl && angle < b.y + bevelControl) {
      // check bounce
      float dist = pos.dist(center);
      if (dist >= height * 0.45 - 5 && dist < height * 0.5) {
        float a = PVector.angleBetween(speed, cspeed);
        speed.rotate(radians(180 + 2*degrees(a) + random(-5, 5)));
      }
    } 

    noFill();
    stroke(getColor());
    strokeWeight(6);
    arc(height/2., height/2., height*0.9, height*0.9, radians(b.x + bevelControl - 90), radians(b.y + bevelControl - 90));
  }

  // calculate the location and extent of this player's belvel and other details
  public void layout(int number) {    
    float segment = 360 / players.size();
    float space = segment / 20;
    bevelSpace = segment - space*6;

    x = (number) * segment + space;
    y = (number+1) * segment - space;
    bevel = new PVector(number * segment + space, number * segment + space * 5);
  }

  // is this player still active?
  public boolean isActive() {
    PVector cspeed = PVector.sub(pos, center);
    float angle = (degrees(cspeed.heading()) + 90 + 360) % 360;
    return angle > x && angle < y;
  }

  // invoke if player has lost a ball
  public void lost() {
    if (lives > 1) {
      lives--;
    }
  }

  // get player color
  color getColor() {
    return cols[int(col) % cols.length];
  }
}