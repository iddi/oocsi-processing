import nl.tue.id.oocsi.*;

// ******************************************************
// This example requires a running OOCSI server!
//
// How to do that? Check: Examples > Tools > LocalServer
//
// More information how to run an OOCSI server
// can be found here: https://iddi.github.io/oocsi/)
// ******************************************************

// CSV column keys which will be logged from incoming events
String[] keys = {"one", "two", "three", "four"};

// separator (use comma for CSV, and semicolon for Excel format)
String separator = ", ";

// output file
PrintWriter output;

// OOCSI connection to receive events
OOCSI oocsi;

void setup() {

  size(400, 200);
  background(255);
  color(100);
  text("Press a key to write the file and exit.", 50, 100);

  // connect to OOCSI server running on the same machine (localhost)
  // with "receiverName" to be my channel others can send data to
  // (for more information how to run an OOCSI server refer to: https://iddi.github.io/oocsi/)
  oocsi = new OOCSI(this, "logger", "localhost");

  // open output file
  output = createWriter("logoutput.csv");

  // write column names
  for (String key : keys) {
    output.print(key + separator);
  }

  // one more column for timestamp and newline
  output.println("timestamp");
}

void draw() {
  // every 300 frames or 5 seconds, write the file contents out
  if (frameCount % 300 == 0) {
    output.flush();
  }
}

void handleOOCSIEvent(OOCSIEvent event) {
  // whenever a new event is received, loop through all keys...
  for (String key : keys) {
    // ...and check if the key is included in the event...
    if (event.has(key)) {
      // ...if yes, print the value for the key
      output.print(event.getInt(key, 0) + separator);
    } else {
      // if not, still print a comma to preserve the format
      output.print(separator);
    }
  }
  
  // timestamp and new line
  output.println(millis());
}

void keyPressed() {
  // Writes the remaining data to the file
  output.flush();

  // Finishes the file
  output.close();

  // Stops the program
  exit();
}