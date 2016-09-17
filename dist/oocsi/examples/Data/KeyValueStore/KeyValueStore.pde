import nl.tue.id.oocsi.*;
import nl.tue.id.oocsi.client.services.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// variables for fill color and size of rectangle
int fillColor = 255;
int size = 3;

OOCSI oocsi1;
OOCSIData store = new OOCSIData();

// second OOCSI instance for making calls to the registered handlers
OOCSI oocsi2;

void setup() {
  //size(200, 200);
  //noStroke();
  //rectMode(CENTER);

  // connect to OOCSI server running on the same machine (localhost)
  // with "receiverName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi1 = new OOCSI(this, "responder", "localhost");

  // register this first OOCSI client for responses
  // to store values to cache
  oocsi1.register("cache_set", "setValue");

  // and to get values from cache
  oocsi1.register("cache_get", "getValue");

  // ---------------------------------------------------------------------------------
  // ---------------------------------------------------------------------------------

  // connect with a second OOCSI client for calling the first one
  oocsi2 = new OOCSI(this, "caller", "localhost");

  // run test
  testKeyValueStore();
}

// responder for calls to "cache_set". will receive the call message and put the included data into the store.
// a status is returned in the response (OK or NOK)
void setValue(OOCSIEvent event, OOCSIData response) {
  String key = event.getString("key");
  Object value = event.getObject("value");
  if (key != null && key.length() > 0 && value != null) {
    store.put(key, value);
    response.data("status", "OK");
  } else {
    response.data("status", "NOK");
  }
}

// responder for calls to "cache_get". will receive the call message and get the requested data from the store.
// a status is returned in the response (OK or NOK)
void getValue(OOCSIEvent event, OOCSIData response) {
  String key = event.getString("key");
  if (key != null && key.length() > 0) {
    Object o = store.get(key);

    if (o != null) {
      response.data("value", o);
      response.data("status", "OK");
    } else {
      response.data("status", "NOK");
    }
  } else {
    response.data("status", "NOK");
  }
}


void testKeyValueStore() {

  // clear line
  println("");

  // NUMBERS
  {
    // 1: create call to set data with key "first" and value 1
    oocsi2.call("cache_set", 200).data("key", "first").data("value", 1).sendAndWait();
    println("set: first = 1");

    // second one for getting a new color
    // 1: send a call with parameter "color", similar to normal OOCSI events
    OOCSICall call = oocsi2.call("cache_get", 200).data("key", "first").sendAndWait();
    // 2: check for response
    if (call.hasResponse()) {
      // 3: get data out of the first response
      print("get first = ");
      println(call.getFirstResponse().getInt("value", 0));
    }
  }
  {
    // 1: create call to set data with key "first" and value 1
    oocsi2.call("cache_set", 200).data("key", "first").data("value", 2).sendAndWait();
    println("set: first = 2");

    // second one for getting a new color
    // 1: send a call with parameter "color", similar to normal OOCSI events
    OOCSICall call = oocsi2.call("cache_get", 200).data("key", "first").sendAndWait();
    // 2: check for response
    if (call.hasResponse()) {
      // 3: get data out of the first response
      print("get first = ");
      println(call.getFirstResponse().getInt("value", 0));
    }
  }

  {
    // 1: create call to set data with key "first" and value 1
    oocsi2.call("cache_set", 200).data("key", "second").data("value", 3.1415f).sendAndWait();
    println("set: second = 3.1415");

    // second one for getting a new color
    // 1: create call with parameter "color", similar to normal OOCSI events
    OOCSICall call = oocsi2.call("cache_get", 200).data("key", "second").sendAndWait();
    // 3: check for response
    if (call.hasResponse()) {
      // 4: get data out of the first response
      print("get second = ");
      println(call.getFirstResponse().getFloat("value", 0));
    }
  }

  // BOOLEAN
  {
    // 1: create call to set data with key "first" and value 1
    oocsi2.call("cache_set", 200).data("key", "third").data("value", true).sendAndWait();
    println("set: third = true");

    // second one for getting a new color
    // 1: create call with parameter "color", similar to normal OOCSI events
    OOCSICall call = oocsi2.call("cache_get", 200).data("key", "third").sendAndWait();
    // 3: check for response
    if (call.hasResponse()) {
      // 4: get data out of the first response
      print("get third = ");
      println(call.getFirstResponse().getBoolean("value", false));
    }
  }

  // STRING
  {
    // 1: create call to set data with key "first" and value 1
    oocsi2.call("cache_set", 200).data("key", "fourth").data("value", "test123").sendAndWait();
    println("set: fourth = 'test123'");

    // second one for getting a new color
    // 1: create call with parameter "color", similar to normal OOCSI events
    OOCSICall call = oocsi2.call("cache_get", 200).data("key", "fourth").sendAndWait();
    // 3: check for response
    if (call.hasResponse()) {
      // 4: get data out of the first response
      print("get fourth = ");
      println(call.getFirstResponse().getString("value"));
    }
  }
  {
    // 1: create call to set data with key "first" and value 1
    oocsi2.call("cache_set", 200).data("key", "fourth").data("value", "Test124").sendAndWait();
    println("set: fourth = 'Test124'");

    // second one for getting a new color
    // 1: create call with parameter "color", similar to normal OOCSI events
    OOCSICall call = oocsi2.call("cache_get", 200).data("key", "fourth").sendAndWait();
    // 3: check for response
    if (call.hasResponse()) {
      // 4: get data out of the first response
      print("get fourth = ");
      println(call.getFirstResponse().getString("value"));
    }
  }
}