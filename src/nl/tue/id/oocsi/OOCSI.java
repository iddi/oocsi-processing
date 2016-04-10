package nl.tue.id.oocsi;

import nl.tue.id.oocsi.client.protocol.OOCSIMessage;
import nl.tue.id.oocsi.client.services.OOCSICall;

/**
 * central OOCSI connector for Processing
 * 
 * @author matsfunk
 */
public class OOCSI {

	private OOCSICommunicator oocsi;
	private String previousLogMessage;
	private int logRepetitionCount = 0;

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 */
	public OOCSI(Object parent, String name) {
		init(parent, name, null, -1, false);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param reconnect
	 */
	public OOCSI(Object parent, String name, boolean reconnect) {
		init(parent, name, null, -1, reconnect);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 */
	public OOCSI(Object parent, String name, String hostname) {
		init(parent, name, hostname, 4444, false);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param reconnect
	 */
	public OOCSI(Object parent, String name, String hostname, boolean reconnect) {
		init(parent, name, hostname, 4444, reconnect);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param port
	 */
	public OOCSI(Object parent, String name, String hostname, int port) {
		init(parent, name, hostname, port, false);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param port
	 * @param reconnect
	 */
	public OOCSI(Object parent, String name, String hostname, int port, boolean reconnect) {
		init(parent, name, hostname, port, reconnect);
	}

	/**
	 * create a new OOCSI network connection with client handle (<name>), server host, and port
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param port
	 * @param reconnect
	 */
	private void init(Object parent, String name, String hostname, int port, boolean reconnect) {
		startOOCSIConnection(parent, name, hostname, port, reconnect);
	}

	/**
	 * returns whether the OOCSI client was able to connect to a server (already)
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return oocsi.isConnected();
	}

	/**
	 * send data through a channel given by the channelName
	 * 
	 * @param channelName
	 * @return
	 */
	public OOCSIMessage channel(String channelName) {
		return oocsi.channel(channelName);
	}

	/**
	 * create a call for service method <callName>
	 * 
	 * @param callName
	 * @return
	 */
	public OOCSICall call(String callName) {
		return oocsi.call(callName);
	}

	/**
	 * create a call for service method <callName>
	 * 
	 * @param callName
	 * @param timeoutMS
	 * @return
	 */
	public OOCSICall call(String callName, int timeoutMS) {
		return oocsi.call(callName, timeoutMS);
	}

	/**
	 * create a call for service method <callName>
	 * 
	 * @param callName
	 * @param timeoutMS
	 * @param maxResponses
	 * @return
	 */
	public OOCSICall call(String callName, int timeoutMS, int maxResponses) {
		return oocsi.call(callName, timeoutMS, maxResponses);
	}

	/**
	 * send raw data to given channel
	 * 
	 * @param channel
	 * @param data
	 */
	public void sendRaw(String channel, String data) {
		oocsi.send(channel, data);
	}

	/**
	 * subscribe to a channel with a given handler method name <handlerName>
	 * 
	 * @param channelName
	 * @param handlerName
	 */
	public void subscribe(String channelName, String handlerName) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.subscribe(channelName, handlerName);
	}

	/**
	 * subscribe to a channel
	 * 
	 * @param channelName
	 */
	public void subscribe(String channelName) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.subscribe(channelName);
	}

	/**
	 * register a responder; requires a method with the given responderName with parameters (OOCSIEvent, Map<String,
	 * Object>)
	 * 
	 * @param responderName
	 */
	public void register(String responderName) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.register(responderName);
	}

	/**
	 * retrieve the list of clients on the server
	 * 
	 * @return
	 */
	public String getClients() {
		return oocsi.isConnected() ? oocsi.clients() : "";
	}

	/**
	 * retrieve the list of channels on the server
	 * 
	 * @return
	 */
	public String getChannels() {
		return oocsi.isConnected() ? oocsi.channels() : "";
	}

	/**
	 * retrieve the list of sub-channel of the channel with the given name on the server
	 * 
	 * @param channelName
	 * @return
	 */
	public String getChannels(String channelName) {
		return oocsi.isConnected() ? oocsi.channels(channelName) : "";
	}

	/**
	 * retrieve the internal OOCSICommunicator
	 * 
	 * @return
	 */
	OOCSICommunicator getCommunicator() {
		return oocsi;
	}

	/**
	 * start OOCSI with a connection and handlers
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param port
	 * @param reconnect
	 */
	private void startOOCSIConnection(Object parent, String name, String hostname, int port, boolean reconnect) {

		// create OOCSI client instance, with logging rerouted to console
		oocsi = new OOCSICommunicator(parent, name) {
			public void log(String message) {
				OOCSI.this.log(message);
			}
		};

		if (reconnect) {
			log(" - reconnecting switched on");
			oocsi.setReconnect(reconnect);
		}

		if (hostname != null) {
			log(" - connecting to " + hostname + ":" + port);
			oocsi.connect(hostname, port);
		} else {
			log(" - connecting with autoconf");
			oocsi.connect();
		}
	}

	/**
	 * log a message on console, will express repeated messages by adding periods after the first instance
	 * 
	 * @param logMessage
	 */
	private void log(String logMessage) {
		if (!logMessage.equals(previousLogMessage)) {
			previousLogMessage = logMessage;
			System.out.println();
			System.out.print(logMessage);
			logRepetitionCount = logMessage.length() - 4;
		} else {
			if (logRepetitionCount++ > 60) {
				System.out.println();
				System.out.print("   ");
				logRepetitionCount = 0;
			}
			System.out.print(".");
		}
	}
}
