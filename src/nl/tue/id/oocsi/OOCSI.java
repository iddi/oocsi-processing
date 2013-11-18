package nl.tue.id.oocsi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import nl.tue.id.oocsi.client.OOCSIClient;
import nl.tue.id.oocsi.client.protocol.DataHandler;
import nl.tue.id.oocsi.client.protocol.Handler;
import processing.core.PApplet;

/**
 * central OOCSI connector for Processing
 * 
 * @author mfunk
 * 
 */
public class OOCSI {

	private PApplet parent;
	private Method handlerEventMethod;
	private Method handlerEventRawMethod;
	private Method handlerEventRawMethodSender;
	private Method handlerEventDataMethod;
	private Method handlerEventDataMethodSender;

	private OOCSIClient oocsi;

	/**
	 * create a new OOCSI network connection
	 * 
	 * @param parent
	 * @param name
	 * @param hostname
	 */
	public OOCSI(PApplet parent, String name, String hostname) {
		this(parent, name, hostname, 4444);
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
		this.parent = parent;
		instrumentPApplet(parent);
		startOOCSIConnection(name, hostname, port);
	}

	/**
	 * send data through a channel given by the channelName
	 * 
	 * @param channelName
	 * @return
	 */
	public OOCSIMessage channel(String channelName) {
		return new OOCSIMessage(oocsi, channelName);
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

		if (handlerEventRawMethod != null
				|| handlerEventRawMethodSender != null) {
			oocsi.subscribe(channelName, new Handler() {

				@Override
				public void receive(String channelName, String data,
						String sender) {
					makeEvent(channelName, data, sender);
				}
			});

			log(" - subscribed to " + channelName + " (raw)");

		} else {
			oocsi.subscribe(channelName, new DataHandler() {

				@Override
				public void receive(String channelName,
						Map<String, Object> data, String sender) {
					makeEvent(channelName, data, sender);
				}
			});

			log(" - subscribed to " + channelName);

		}
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
	 * retrieve the list of sub-channel of the channel with the given name on
	 * the server
	 * 
	 * @param channelName
	 * @return
	 */
	public String getChannels(String channelName) {
		return oocsi.channels(channelName);
	}

	/**
	 * instrument the parent PApplet
	 * 
	 * @param parent
	 */
	private void instrumentPApplet(PApplet parent) {

		log("Starting OOCSI...");

		try {
			handlerEventRawMethod = parent.getClass().getDeclaredMethod(
					"handleOOCSIEvent",
					new Class[] { String.class, String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			handlerEventRawMethod = null;
		}
		try {
			handlerEventDataMethod = parent.getClass()
					.getDeclaredMethod("handleOOCSIEvent",
							new Class[] { String.class, Map.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			handlerEventDataMethod = null;
		}

		try {
			handlerEventMethod = parent.getClass().getDeclaredMethod(
					"handleOOCSIEvent", new Class[] { OOCSIEvent.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			handlerEventMethod = null;
		}
		try {
			handlerEventRawMethodSender = parent.getClass().getDeclaredMethod(
					"handleOOCSIEvent",
					new Class[] { String.class, String.class, String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			handlerEventRawMethodSender = null;
		}
		try {
			handlerEventDataMethodSender = parent.getClass().getDeclaredMethod(
					"handleOOCSIEvent",
					new Class[] { String.class, Map.class, String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			handlerEventDataMethodSender = null;
		}

		// check for no handlers
		if (handlerEventMethod == null && handlerEventDataMethod == null
				&& handlerEventDataMethodSender == null
				&& handlerEventRawMethod == null
				&& handlerEventRawMethodSender == null) {
			log(" - no handlers found");
		}
	}

	/**
	 * start OOCSI with a connection and handlers
	 * 
	 * @param name
	 * @param hostname
	 * @param port
	 */
	private void startOOCSIConnection(String name, String hostname, int port) {

		oocsi = new OOCSIClient(name);

		log(" - connecting to " + hostname + ":" + port);

		oocsi.connect(hostname, port);
		if (oocsi.isConnected()) {

			log(" - connected successfully");

			// subscribe to self handler
			if (handlerEventRawMethod != null
					|| handlerEventRawMethodSender != null) {
				oocsi.subscribe(new Handler() {

					@Override
					public void receive(String channelName, String data,
							String sender) {
						makeEvent(channelName, data, sender);
					}
				});

				log(" - subscribed to " + name + " (raw)");

			} else {
				oocsi.subscribe(new DataHandler() {

					@Override
					public void receive(String channelName,
							Map<String, Object> data, String sender) {
						makeEvent(channelName, data, sender);
					}
				});

				log(" - subscribed to " + name + " (data)");

			}
		}
	}

	/**
	 * dispatch event with raw data
	 * 
	 * @param channelName
	 * @param data
	 * @param sender
	 */
	private void makeEvent(String channelName, String data, String sender) {
		if (handlerEventRawMethodSender != null) {
			try {
				handlerEventRawMethodSender.invoke(parent, new Object[] {
						channelName, data, sender });
			} catch (IllegalAccessException e) {
				handlerEventRawMethodSender = null;
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				handlerEventRawMethodSender = null;
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				handlerEventRawMethodSender = null;
				e.printStackTrace();
			}
		} else if (handlerEventRawMethod != null) {
			try {
				handlerEventRawMethod.invoke(parent, new Object[] {
						channelName, data, sender });
			} catch (IllegalAccessException e) {
				handlerEventRawMethod = null;
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				handlerEventRawMethod = null;
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				handlerEventRawMethod = null;
				e.printStackTrace();
			}
		}
	}

	/**
	 * dispatch event with structured data
	 * 
	 * @param channelName
	 * @param data
	 * @param sender
	 */
	private void makeEvent(String channelName, Map<String, Object> data,
			String sender) {
		if (handlerEventMethod != null) {
			try {
				handlerEventMethod
						.invoke(parent, new Object[] { new OOCSIEvent(
								channelName, data, sender) });
			} catch (IllegalAccessException e) {
				handlerEventMethod = null;
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				handlerEventMethod = null;
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				handlerEventMethod = null;
				e.printStackTrace();
			}
		} else if (handlerEventDataMethodSender != null) {
			try {
				handlerEventDataMethodSender.invoke(parent, new Object[] {
						channelName, data, sender });
			} catch (IllegalAccessException e) {
				handlerEventDataMethodSender = null;
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				handlerEventDataMethodSender = null;
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				handlerEventDataMethodSender = null;
				e.printStackTrace();
			}
		} else if (handlerEventDataMethod != null) {
			try {
				handlerEventDataMethod.invoke(parent, new Object[] {
						channelName, data, sender });
			} catch (IllegalAccessException e) {
				handlerEventDataMethod = null;
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				handlerEventDataMethod = null;
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				handlerEventDataMethod = null;
				e.printStackTrace();
			}
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
