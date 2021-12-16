package nl.tue.id.oocsi;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.id.oocsi.client.data.OOCSIDevice;
import nl.tue.id.oocsi.client.protocol.Handler;
import nl.tue.id.oocsi.client.protocol.OOCSIMessage;
import nl.tue.id.oocsi.client.services.OOCSICall;
import nl.tue.id.oocsi.client.services.Responder;

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
	 */
	public OOCSI(Object parent) {
		init(parent, null, null, -1, true);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 */
	public OOCSI(Object parent, String name) {
		init(parent, name, null, -1, true);
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
		init(parent, name, hostname, 4444, true);
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
		init(parent, name, hostname, port, true);
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
	 * create a new OOCSI network connection that is initialized with an existing communicator instance
	 * 
	 * @param parent
	 * @param communicator
	 */
	private OOCSI(Object parent, OOCSICommunicator communicator) {
		oocsi = communicator;
	}

	/**
	 * create a new OOCSI network connection with client handle ("name"), server host, and port
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
	 * create a local instance of OOCSI, without a connection
	 * 
	 * @param parent
	 * @return
	 */
	public static OOCSI localInstance(Object parent) {

		// create local communicator
		OOCSICommunicator oc = new OOCSICommunicator(parent, null) {

			private static final String SELF = "SELF";
			private List<OOCSICall> openCalls = new LinkedList<OOCSICall>();

			@Override
			public void send(String channelName, Map<String, Object> data) {
				if (channelName != null && channelName.trim().length() > 0) {

					Handler c = channels.get(channelName);
					if (c == null && channelName.equals(name.replaceFirst(":.*", ""))) {
						c = channels.get(SELF);
					}

					// try to find a responder
					if (data.containsKey(OOCSICall.MESSAGE_HANDLE)) {
						Responder r = services.get((String) data.get(OOCSICall.MESSAGE_HANDLE));
						if (r != null) {
							try {
								r.receive(SELF, data, System.currentTimeMillis(), channelName, name);
							} catch (Exception e) {
							}
						}

						return;
					}

					// try to find an open call
					if (!openCalls.isEmpty() && data.containsKey(OOCSICall.MESSAGE_ID)) {
						String id = (String) data.get(OOCSICall.MESSAGE_ID);

						// walk from back to allow for removal
						for (int i = openCalls.size() - 1; i >= 0; i--) {
							OOCSICall call = openCalls.get(i);
							if (!call.isValid()) {
								openCalls.remove(i);
							} else if (call.getId().equals(id)) {
								call.respond(data);
								break;
							}
						}

						return;
					}

					// if no responder or call and channel ready waiting
					if (c != null) {
						c.receive(SELF, data, System.currentTimeMillis(), channelName, name);
					}
				}
			}

			/**
			 * register a call in the list of open calls
			 * 
			 * @param call
			 */
			public void register(OOCSICall call) {
				openCalls.add(call);
			}

			@Override
			public boolean isConnected() {
				return true;
			}

			@Override
			public void log(String message) {
			}
		};

		// create a local instance
		OOCSI o = new OOCSI(parent, oc) {
			@Override
			public void sendRaw(String channel, String data) {
				// do nothing
			}
		};

		return o;
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
	 * disconnect this client from the OOCSI network
	 * 
	 */
	public void disconnect() {
		oocsi.disconnect();
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
	 * create a call for service method "callName"
	 * 
	 * @param callName
	 * @return
	 */
	public OOCSICall call(String callName) {
		return oocsi.call(callName);
	}

	/**
	 * create a call for service method "callName" on channel "channelName"
	 * 
	 * @param channelName
	 * @param callName
	 * @return
	 */
	public OOCSICall call(String channelName, String callName) {
		return oocsi.call(channelName, callName);
	}

	/**
	 * create a call for service method "callName" with a specific timeout
	 * 
	 * @param callName
	 * @param timeoutMS
	 * @return
	 */
	public OOCSICall call(String callName, int timeoutMS) {
		return oocsi.call(callName, timeoutMS);
	}

	/**
	 * create a call for service method "callName" with a specific timeout on channel "channelName"
	 * 
	 * @param channelName
	 * @param callName
	 * @param timeoutMS
	 * @return
	 */
	public OOCSICall call(String channelName, String callName, int timeoutMS) {
		return oocsi.call(channelName, callName, timeoutMS);
	}

	/**
	 * create a call for service method "callName" with a specific timeout
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
	 * create a call for service method "callName" with a specific timeout on channel "channelName"
	 * 
	 * @param channelName
	 * @param callName
	 * @param timeoutMS
	 * @param maxResponses
	 * @return
	 */
	public OOCSICall call(String channelName, String callName, int timeoutMS, int maxResponses) {
		return oocsi.call(channelName, callName, timeoutMS, maxResponses);
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
	 * subscribe to a channel with a given handler method name "handlerName"
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
	 * subscribe to a channel with a given handler method name "handlerName"; limits the rate of incoming events to
	 * "rate" events per "seconds" secs
	 * 
	 * @param channelName
	 * @param handlerName
	 * @param rate
	 * @param seconds
	 */
	public void subscribe(String channelName, String handlerName, int rate, int seconds) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.subscribe(channelName, handlerName, rate, seconds);
	}

	/**
	 * subscribe to a channel with a given handler method name "handlerName"; limits the rate of incoming events to
	 * "rate" events per "seconds" secs; "ratePerSender" controls whether we limit the rate of incoming event per sender
	 * or for all events coming in from all senders
	 * 
	 * @param channelName
	 * @param handlerName
	 * @param rate
	 * @param seconds
	 * @param ratePerSender
	 */
	public void subscribe(String channelName, String handlerName, int rate, int seconds, boolean ratePerSender) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.subscribe(channelName, handlerName, rate, seconds, ratePerSender);
	}

	/**
	 * register a responder; requires a method with the given responderName with parameters (OOCSIEvent, OOCSIData)
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
	 * register a responder "responderName"; requires a method with the given name "responderName" with parameters
	 * (OOCSIEvent, OOCSIData)
	 * 
	 * @param responderName
	 * @param responderFunctionName
	 */
	public void register(String responderName, String responderFunctionName) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.register(responderName, responderFunctionName);
	}

	/**
	 * register a responder "responderName" on channel "channelName"; requires a method with the responder name
	 * "responderName" with parameters (OOCSIEvent, OOCSIData)
	 * 
	 * @param channelName
	 * @param responderName
	 */
	public void registerChannel(String channelName, String responderName) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.registerChannel(channelName, responderName);
	}

	/**
	 * register a responder "responderName" on channel "channelName"; requires a method with the given name
	 * "responderName" with parameters (OOCSIEvent, OOCSIData)
	 * 
	 * @param channelName
	 * @param responderName
	 * @param responderFunctionName
	 */
	public void registerChannel(String channelName, String responderName, String responderFunctionName) {

		if (!oocsi.isConnected()) {
			return;
		}

		oocsi.registerChannel(channelName, responderName, responderFunctionName);
	}

	/**
	 * create an OOCSI device instance with the client's name that can be configured and then submitted to the OOCSI
	 * server
	 * 
	 * @return
	 */
	public OOCSIDevice heyOOCSI() {
		return oocsi.heyOOCSI();
	}

	/**
	 * create a named OOCSI device that can be configured and then submitted to the OOCSI server
	 * 
	 * @param deviceName
	 * @return
	 */
	public OOCSIDevice heyOOCSI(String deviceName) {
		return oocsi.heyOOCSI(deviceName);
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
	 * create a handler for the given method name (handlerName), which must be an existing method in the parent class
	 * 
	 * @param handlerName
	 * @return
	 */
	Handler getHandlerFor(String handlerName) {
		return getCommunicator().createSimpleCallerHandler(handlerName);
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
