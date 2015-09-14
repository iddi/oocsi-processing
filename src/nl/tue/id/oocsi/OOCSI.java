package nl.tue.id.oocsi;

import nl.tue.id.oocsi.client.protocol.OOCSIMessage;
import processing.core.PApplet;

/**
 * central OOCSI connector for Processing
 * 
 * @author matsfunk
 * 
 */
public class OOCSI {

	private OOCSICommunicator oocsi;

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 */
	public OOCSI(PApplet parent, String name) {
		init(parent, name, null, -1);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 */
	public OOCSI(PApplet parent, String name, String hostname) {
		init(parent, name, hostname, 4444);
	}

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param port
	 */
	public OOCSI(PApplet parent, String name, String hostname, int port) {
		init(parent, name, hostname, port);
	}

	/**
	 * create a new OOCSI network connection with client handle (<name>), server host, and port
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 * @param port
	 */
	private void init(PApplet parent, String name, String hostname, int port) {
		startOOCSIConnection(parent, name, hostname, port);
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

		subscribe(channelName, "handleOOCSIEvent");
	}

	/**
	 * retrieve the list of clients on the server
	 * 
	 * @return
	 */
	public String getClients() {
		return oocsi.clients();
	}

	/**
	 * retrieve the list of channels on the server
	 * 
	 * @return
	 */
	public String getChannels() {
		return oocsi.channels();
	}

	/**
	 * retrieve the list of sub-channel of the channel with the given name on the server
	 * 
	 * @param channelName
	 * @return
	 */
	public String getChannels(String channelName) {
		return oocsi.channels(channelName);
	}

	/**
	 * start OOCSI with a connection and handlers
	 * 
	 * @param name
	 * @param hostname
	 * @param port
	 */
	private void startOOCSIConnection(Object parent, String name, String hostname, int port) {

		// create OOCSI client instance, with logging rerouted to console
		oocsi = new OOCSICommunicator(parent, name) {
			public void log(String message) {
				OOCSI.this.log(message);
			}
		};

		log(" - connecting to " + hostname + ":" + port);

		if (hostname != null) {
			oocsi.connect(hostname, port);
		} else {
			oocsi.connect();
		}
	}

	/**
	 * log a message on console
	 * 
	 * @param logMessage
	 */
	private void log(String logMessage) {
		System.out.println(logMessage);
	}
}
