import nl.tue.id.oocsi.*;
import java.util.*;

// **************************************************
// This examples requires a running OOCSI server!
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// **************************************************

// different variables for different data types:
// numbers
int numberInt = 0;
float numberFloat = 0;
double numberDouble = 0;
long numberLong = 0;

// string
String numberString = "";

// array
float[] numberArray = new float[] { 0, 0 };

// object
Date dateObject = new Date();

void setup() {
  size(200, 200);
  stroke(120);
  fill(120);
  textSize(10f);

  // connect to OOCSI server running on the same machine (localhost)
  // with "receiverName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  OOCSI oocsi = new OOCSI(this, "receiver_" + System.currentTimeMillis(), "localhost");

  // subscribe to channel "testchannel"
  // either the channel name is used for looking for a handler method...
  oocsi.subscribe("datachannel");
  // ... or the handler method name can be given explicitly
  // oocsi.subscribe("testchannel", "testchannel");
}

void draw() {
  background(255);

  // visualize the received data
  fill(120);
  rect(20, numberInt, 20, 20);
  rect(50, numberFloat, 20, 20);
  rect(80, (float) numberDouble, 20, 20);
  rect(110, numberLong, 20, 20);
  text(numberString, 20, 20);
  text(dateObject.toString(), 20, height - 20);
  fill(220);
  rect(numberArray[0], numberArray[1], 20, 20);
}

void datachannel(OOCSIEvent event) {

  // get and save integer value
  numberInt = event.getInt("integer", 0);

  // get and save float value
  numberFloat = event.getFloat("float", 0);

  // get and save double value
  numberDouble = event.getDouble("double", 0);

  // get and save long value
  numberLong = event.getLong("long", 0);

  // get and save string value
  numberString = event.getString("string");

  // get, cast and save array value
  numberArray = (float[]) event.getObject("array");

  // get, cast and save Date object value
  dateObject = (Date) event.getObject("object");
}