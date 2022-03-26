import nl.tue.id.oocsi.*;
import nl.tue.id.oocsi.client.data.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

OOCSIDevice hallwayLight, doorSensor, doorLock, multisensor; 

void setup() {
  size(400, 400);

  // -----------------------------------------------------------------------
  // 
  // Introduction to heyOOCSI!:
  // https://github.com/iddi/oocsi/wiki/heyOOCSI!
  //
  // Reference of OOCSI Device entities:
  // https://github.com/iddi/oocsi/wiki/heyOOCSI!-entities
  // 
  // -----------------------------------------------------------------------

  // first OOCSI client...
  // connect to OOCSI server running on the same machine (localhost)
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  OOCSI o1 = new OOCSI(this, "test_hallway_light", "localhost");
  // create a heyOOCSI device for this client and store it in hallwayLight 
  hallwayLight = o1.heyOOCSI();

  // add the location for this device
  //  the first parameter is a textual label such as "hallway" or "kitchen"
  //  the second and third parameters are pixel locations if you want to
  //  use a data canvas on the OOCSI server
  hallwayLight.addLocation("hallway", 150, 250);
  // configure this device with a light component
  // first parameter: name of the device component
  // second parameter: channel on which this component receives input and sends output
  // third parameter: LED type (RGB, RGBW, RGBWW, CCT, DIMMABLE, ONOFF)
  // fourth: light spectrum (CCT, WHITE, RGB)
  // fifth and sixth: min and max brightness values
  // seventh: default state (true/false)
  // eighth: icon name (see: https://materialdesignicons.com/)
  hallwayLight.addLight("hallwaylight", "hallway_channel", OOCSIDevice.LedType.RGBW, OOCSIDevice.LightSpectrum.RGB, 0, 255, false, 255, "lightbulb");

  // register the device
  hallwayLight.sayHi();

  // -----------------------------------------------------------------------

  // second OOCSI client...
  OOCSI o2 = new OOCSI(this, "test_door_sensor", "localhost");
  // create a heyOOCSI device for this client
  doorSensor = o2.heyOOCSI();
  
  // add location as tag and pixel coordinates
  doorSensor.addLocation("hallway", 100, 100);
  // configure this device with a binary sensor component
  // first parameter: name of the device component
  // second parameter: channel on which this component receives input and sends output
  // third parameter: binary sensor type (see: https://developers.home-assistant.io/docs/core/entity/sensor/#available-device-classes)  
  // fourth: default state (true/false)
  // fifth: icon name (see: https://materialdesignicons.com/)
  doorSensor.addBinarySensor("front_door_open", "hallway_channel", OOCSIDevice.BinarySensorType.lock, false, "door");

  // register the device
  doorSensor.sayHi();

  // -----------------------------------------------------------------------

  // third OOCSI client...
  OOCSI o3 = new OOCSI(this, "test_door_lock", "localhost");
  doorLock  = o3.heyOOCSI();
  doorLock.addLocation("hallway", 200, 200);

  // configure this device with a switch 
  // first parameter: name of the device
  // second parameter: channel on which this component receives input and sends output
  // third paramter: switch type (OUTLET, SWITCH)
  // fourth: default state (true/false) --> here it's true to show that the door is by default locked
  // fifth: icon name (see: https://materialdesignicons.com/)
  doorLock.addSwitch("front_door_lock", "hallway_channel", OOCSIDevice.SwitchType.SWITCH, true, "door");

  // register the device
  doorLock.sayHi();
  
  // -----------------------------------------------------------------------

  // fourth OOCSI client...
  OOCSI o4 = new OOCSI(this, "test_multisensor", "localhost");
  multisensor = o4.heyOOCSI();
  multisensor.addLocation("home", 100, 100);
  
  // you can add any kinds of properties to an OOCSI device
  multisensor.addProperty("brand", "FABulous IoT Devices, Inc.");
  multisensor.addProperty("price", "99.99");
  multisensor.addProperty("size", "40 x 40 x 12 cm");
  
  // configure this device with a number input
  // first parameter: name of the device
  // second parameter: channel on which this component receives input and sends output
  // third and fourth: min and max values of the number input
  // fifth: number unit (from sensor units)
  // sixth: default value
  // seventh: icon name (see: https://materialdesignicons.com/)
  multisensor.addNumber("sensitity_input", "multisensor_channel", 0, 100, 50, "co2");
  // co2 sensor
  // first parameter: name of the device
  // second parameter: channel on which this component receives input and sends output
  // third: 
  // icon name (see: https://materialdesignicons.com/)
  multisensor.addSensor("co2_sensor", "multisensor_channel", OOCSIDevice.SensorType.carbon_dioxide, "ppm", 30, "air");
  
  // register the device
  multisensor.sayHi();
  
  // -----------------------------------------------------------------------

  // update the sensors and other components every 2 seconds (see draw())
  frameRate(0.5);
}

void draw() {

  // set value
  doorSensor.setValue(round(random(0, 1)));
  
  // set state
  doorLock.setState(true);
  
  // set value based on previous value (brightness)
  float brightness = hallwayLight.getValue("brightness", 0);
  // randomly generate a new brightness value, constrain it to the interval [0, 255]
  brightness = constrain(brightness + random(-2, 2), 0, 255);
  hallwayLight.setValue("brightness", brightness);

  // toggle light state (= invert the current state or default false)
  hallwayLight.setState(!hallwayLight.getState(false));
  
  // set values for different components of the same device
  multisensor.setValueForComponent("sensitivity_input", "value", random(0, 100));
  multisensor.setValueForComponent("co2_sensor", "value", random(30, 90));

}
