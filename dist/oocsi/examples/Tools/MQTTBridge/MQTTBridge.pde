// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************
void setup() {

  // connect to OOCSI server running on the same machine (localhost)
  // with the ID "MQTTClient" for sending events from the MQTT network to OOCSI
  OOCSI oocsi = new OOCSI(new String(), "MQTTClient", "localhost");

  // connect to an MQTT network, here to 'iot.eclipse.org', identify there as "OOCSIBridgeClient"
  // and listen for events on MQTT that will be channeled to the OOCSI network
  MqttOOCSIBridge tmob = new MqttOOCSIBridge("tcp://iot.eclipse.org:1883", "OOCSIBridgeClient", oocsi);

  // bridge a specific MQTT topic to OOCSI channel
  tmob.bridge("MQTT Examples", "testchannel");
}

void draw() {
}