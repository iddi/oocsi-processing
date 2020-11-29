// ******************************************************
// Author: I-Tang(Eden) Chiang, Mathias Funk 
// Date: Nov 30, 2020
// Description: Sample code for accessing Data Foundry
//              to show how to send interaction data to 
//              Data Foundry (IoT dataset) and also
//              access and update an Entity dataset 
// ******************************************************

import java.util.*;
import nl.tue.id.datafoundry.*;

// ------------------------------------------------------------------------
// settings for DataFoundry library
//
// ... :: CHANGE API TOKENS AND IDS YOURSELF! :: ...
//
String host = "url.server.com";
String iot_api_token = "biWGpc96WbiyK+b/vPd03DM75EaoZaadwj029HzF+YnZn38TkbfeqeU18puByNzH";
String entity_api_token = "Gf0IaT7sXif6GdKYL0ygJQcOa5pe8N6K1zQXOg9E/NvIZ2aJ6ktmAllnumKQzFIS";
long iot_id = 2;
long entity_id = 3;
// ------------------------------------------------------------------------

// data foundry connection
DataFoundry df = new DataFoundry(host);
// access to two datasets: iotDS and entityDS
DFDataset iotDS = df.dataset(iot_id, iot_api_token);
DFDataset entityDS = df.dataset(entity_id, entity_api_token);

String uname;
color colorChoice = color(0);
int clicks = 1;
long startTime;
long lastClickTime = 0;

void setup() {
  // initiate canvas in mobile resolution
  size(400, 400);
  noStroke(); 
  rectMode(CENTER);
  frameRate(20);

  // log start interaction event
  submit("start");

  // set interaction start time
  startTime = millis();
}

void draw() {
  background(0);

  // update color
  colorChoice = color(map(mouseX, 0, width, 30, 200), 80, map(mouseY, 0, height, 30, 200));

  // color choice
  fill(colorChoice);
  rect(width/2, height/2, 100, 100);

  // check every 5 seconds
  if (millis() > 5000 && frameCount % 600 == 0) {
    getUserProfile();
  }
}

void mousePressed() {
  clicks++;
  submit("colorChoice");
  lastClickTime = millis();
}

// log data --------------------------------------------------------------------------

public void submit(String act) {
  // send data to both datasets
  logIoTData(act, (millis() - lastClickTime), colorChoice);
  updateUserProfile();
}

// to IoT dataset
void logIoTData(String act, long relativeTime, color tempColor) {
  // set resource id (refId of device in the project)
  iotDS.device(uname);
  // set activity for the log
  iotDS.activity(act);
  // add data, then send off the log
  iotDS.data("time", relativeTime).data("color", hex(tempColor)).log();
}

// update user profile in Entity dataset ---------------------------------------------

void updateUserProfile() {
  float avgTime = (millis() - startTime) / clicks;
  print("\naverage time: " + avgTime);
  print("\npreference color: " + colorChoice);

  // select item with id and token combination
  entityDS.id(uname).token(uname);
  // add data to send (=update)
  entityDS.data("average_time", avgTime).data("last_color", colorChoice)
    .data("plays", clicks).update();
}

// get user profile in Entity dataset ------------------------------------------------

void getUserProfile() {
  // set item
  entityDS.id(uname).token(uname);

  // get item
  Map<String, Object> result = entityDS.get();

  // access the elements of the profile
  if (result.containsKey("last_color")) {
    String colorChoice = (String) result.get("last_color");
  }
}
