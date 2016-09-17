import nl.tue.id.oocsi.server.*;

// **************************************************
// This example provides a running OOCSI server!
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// **************************************************

void setup() {
  
  // start an OOCSI server from within Processing
  // stop the Processing sketch to also stop the server
  OOCSIServer.main(new String[] {});
  
  // use this line to enable logging in the Processing console for the server
  //OOCSIServer.main(new String[] {"-logging"});

}