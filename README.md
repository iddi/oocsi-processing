# oocsi-processing

Processing Library for the [OOCSI](https://github.com/iddi/oocsi) platform.

This platform can connect Windows, Mac and Linux computers (running Java and Processing), devices (Arduino, Raspberry Pi and Gadgeteer),
Web brosers (via websockets) and mobile devices (iOS and Android).
Please refer to the general documentation to know more about connection possibilities.  

## Download

Find the latest version of the library here: [oocsi-processing.zip](dist/oocsi-processing.zip) (21kB)

Alternatively, you can browse the source code on GitHub or clone the GitHub repository and get started with the code.

## Installation

1. Extract the zip file into the Processing libraries directory (in Windows processing-x.x.x\modes\java\libraries\)
2. Restart Processing
3. Open the examples browser in Processing, look for the Libraries >> oocsi folder 


## How to use

Either use one of the examples from the Processing examples browser, or follow the short tutorial below.

Before starting with an OOCSI client running in Processing, you need to know how the OOCSI network looks like.
You will need an OOCSI server running either on your computer (_localhost_) or available from the network.
Also, any OOCSI client in the network is identified by a _unique_name_, which serves also as an address if other clients in the OOCSI network want to messages. 


### Create an OOCSI client

Before you can send or receive messages, you will need to create an OOCSI client that connects to an OOCSI server (running at a specific address).
When creating the client, you will need to supply also a _unique_name_, which can be used as a handle if others want to send messages to your client. 

Create a client that connects to an OOCSI server running on the local computer (running at _localhost_, see [here](https://https://github.com/iddi/oocsi/readme.md#running_local)):

````Java
	OOCSI oocsi = new OOCSI(this, "unique_name", "localhost");
````

Create a client that connects to an OOCSI server running at the address _oocsi.example.net_:

````Java
	OOCSI oocsi = new OOCSI(this, "unique_name", "oocsi.example.net");
````

After this statement, the OOCSI client _oocsi_ can be used in Processing code to send or subscribe for messages. Please keep an eye on the Processing console where OOCSI will print start messages and also error, in case something goes wrong. 


### Subscribe to OOCSI channel

OOCSI communications base on messages which are sent to channels or individual clients. For simplicity, clients are regarded as channels as well.
OOCSI clients like the one created above can subscribe to channels, and from then on will receive all messages that are sent to the chosen channels.
Also, clients will receive all messages that are sent to their specific channel.

````Java
	oocsi.subscribe("channel_red"); 
````

This line will subscribe the client to the channel _channel_red_. The client will receive all messages sent to that channel.
To actually receive something, the _handleOOCSIEvent_ function has to be in place: 

````Java
	void handleOOCSIEvent(OOCSIEvent message) {
		// print out all values in message
		println(message.keys());
	}
````

In this example, all contents of a message are printed to the Processing console. These _keys_ can be used to retrieve values from the message, for example:

````Java
	void handleOOCSIEvent(OOCSIEvent message) {
		// print out the "intensity" value in the message
		println(message.get("intensity"));
	}
````

Instead of using "handleOOCSIEvent" as the default hub of all incoming OOCSI events, you can create a new function with the same name as the channel you would like to subscribe to, and then this function will be called for incoming evens from that channel:

````Java
	...
	oocsi.subscribe("testchannel");
	...

	void testchannel(OOCSIEvent message) {
		// print out the "intensity" value in the message from channel "testchannel"
		println(message.get("intensity"));
	}
````

Note that for this only channel names without punctuation and whitespace characters are possible.
	

### Send data to OOCSI channel

Sending data to the OOCSI network, for instance, to one specific channel or client is even easier: 

````Java
	oocsi.channel("channel_red").data("intensity", 100).send();
````
 
Essentially, sending messages follows three steps: 

1. Select a channel, for example: "channel_red"
2. Add data to the message, for example: "intensity" = 100
3. Send the message to OOCSI
 
This composed message will then be send via the connected OOCSI server to the respective channel or client, in this case to "channel_red". 


### Getting data from events

Events in the OOCSI system contain both data and meta-data items that are sent from one client to another client or to a channel (with multiple subscribers).

#### Event data: general items

OOCSIEvents have a data payload that is freely definable and realized as a key-value store (Map<String, Object>). Such key-value pairs can be accessed with helper mthods that will convert the data type of the value accordingly: 

````Java
	// from a given OOCSIEvent event
	String stringValue = event.getString("mykey");
	Object objectValue = event.getObject("mykey");
````

Events do not guarantee that specific keys and values are contained. For these cases, default values can be used in the retrieval of event data. These default values (with the correct data type) are added to the retrieval call as a second parameter, and they will be assigned if (1) the key could not be found, or (2) if the value could not converted to the specified data type.  	

````Java
	// retrieval with an additional default value
	// from a given OOCSIEvent event
	boolean booleanValue = event.getInt("mykey", false);
	int intValue = event.getInt("mykey", 0);
	long longValue = event.getLong("mykey", 0);
	float floatValue = event.getFloat("mykey", 0.0f);
	double doubleValue = event.getDouble("mykey", 0.0d);
	String stringValue = event.getString("mykey", "default");
````

#### Event data: array items

The same works for arrays (fields of values of the same type). You can retrieve them by adding "Array" to the retrieval method. The default value is always an array of the same type. If you don't need a default value, you can use `null`.

````Java
	// retrieval without default array value
	boolean[] booleanArray = event.getIntArray("mykey", null);
	int[] intArray = event.getIntArray("mykey", null);
	long[] longArray = event.getLongArray("mykey", null);
	float[] floatArray = event.getFloatArray("mykey", null);
	double[] doubleArray = event.getDoubleArray("mykey", null);
	String[] stringArray = event.getStringArray("mykey", null);
````

If you want to retrieve the data with a default value, you need to use an array of the same type as what you want to get out. Arrays of specific types can be created as `new String[] {"hello", "world"}` for the type `String`, or `new int[] { 1, 2, 3 }` for the type `int`. They can be empty as well: `new long[]{}` or `new boolean[]{}`.

````Java
	// retrieval with an additional default array value
	boolean[] booleanArray = event.getBooleanArray("mykey", new boolean[] {true, false, true, false });
	int[] intArray = event.getIntArray("mykey", new int[] { 1, 2, 3 });
	long[] longArray = event.getLongArray("mykey", new long[]{ 100000l, 200000l });
	float[] floatArray = event.getFloatArray("mykey", new float[]{ 1.30l, 20.3l });
	double[] doubleArray = event.getDoubleArray("mykey", new double[]{ 100000l, 200000l });
	String[] stringArray = event.getStringArray("mykey", new String[] {});
````

#### Event data: check availability of items

As an alternative to using default values, one can also check whether the key is contained in the event:

````Java
	// with a given OOCSIEvent event
	if(event.has("mykey")) {
		// retrieve value (String, for example)
		event.getString("mykey", "");
	}
````

Events can provide a list of contained keys, which can be used to dump all contained data or to systematically retrieve all data.

````Java
	// from a given OOCSIEvent event
	String[] keys = event.keys();
````


#### Event meta-data

Apart from that, an OOCSIEvent has built-in meta-data fields such as _sender_, _timestamp_, and _channel_. In addition, the _recipient_ field is provided for some client implementations. Each of these fields can be access with a dedicated getter method:

````Java
	// from a given OOCSIEvent event	
	// sender and receiver
	String sender = event.getSender();
	String channel = event.getChannel();
	String recipient = event.getRecipient();
	
	// time
	Date timestamp = event.getTimestamp();
	long unixTime = event.getTime();
````


### Full example

As a full example, we build a simple counter that will count from 0 up till the sketch is stopped.
In the _setup_ function, a connection to the OOCSI network is established with the handle "counterA", and after that, an initial message with counter = 0 is sent to "counterA" via the OOCSI network. In the _handleOOCSIEvent_ function, every time a message with a counter value is received, the sketch will send it out to handle "counterA" again with the counter increased by 1.
When pasting the following code into Processing and running it, the Processing console should show a fast sequence of increasing numbers.

````Java
	import nl.tue.id.oocsi.*;
	
	OOCSI oocsi;
	
	void setup () {	
	  oocsi = new OOCSI(this, "counterA", "localhost");
	  oocsi.channel("counterA").data("count", 0).send();
	}
	
	void handleOOCSIEvent(OOCSIEvent e) {
	  int count = e.getInt("count", 0);
	  println(count);
	  oocsi.channel("counterA").data("count", count + 1).send();
	}
````


### Other examples 

The OOCSI Processing plugin comes with 3 examples that demonstrate parts of the functionality.
All examples require an OOCSI server running on the same computer (running at _localhost_, see [here](https://github.com/iddi/oocsi/readme.md#running_local)).
The examples are available from the Processing examples browser, or below:

1. Client to client message sending _via a direct link_:
	- [DirectReceiver](dist/oocsi/examples/Data/DirectReceiver/DirectReceiver.pde) (start this first)
	- [DirectSender](dist/oocsi/examples/Data/DirectSender/DirectSender.pde) (move mouse over the window, receiver window should show a moving square)


2. Client to client message sending and receiving _via a channel_:
	- [ChannelReceiver](dist/oocsi/examples/Data/ChannelReceiver/ChannelReceiver.pde) (start this first)
	- [ChannelSender](dist/oocsi/examples/Data/ChannelSender/ChannelSender.pde) (move mouse over the window, receiver window should show a moving square)


3. Getting an updated list of all connected clients
	- [Tools_ClientLister](dist/oocsi/examples/Tools/ClientLister/Tools/ClientLister.pde)
	
