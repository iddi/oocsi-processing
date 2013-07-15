package nl.tue.id.oocsi;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import nl.tue.id.oocsi.client.OOCSIClient;
import nl.tue.id.oocsi.client.protocol.DataHandler;
import nl.tue.id.oocsi.client.protocol.Handler;
import processing.core.PApplet;

public class OOCSI {

	private PApplet parent;
	private Method handlerEventMethod;
	private Method handlerEventRawMethod;
	private Method handlerEventRawMethodSender;
	private Method handlerEventDataMethod;
	private Method handlerEventDataMethodSender;

	private String name;
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
	 * send data
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

			log(System.out, " - subscribed to " + channelName + " (raw)");

		} else {
			oocsi.subscribe(channelName, new DataHandler() {

				@Override
				public void receive(String channelName,
						Map<String, Object> data, String sender) {
					makeEvent(channelName, data, sender);
				}
			});

			log(System.out, " - subscribed to " + channelName + " (data)");

		}
	}

	/**
	 * instrument the parent PApplet
	 * 
	 * @param parent
	 */
	private void instrumentPApplet(PApplet parent) {

		log(System.out, "Starting OOCSI...");

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
			log(System.out, " - no handlers found");
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

		this.name = name;
		oocsi = new OOCSIClient(name);

		log(System.out, " - connecting to " + hostname + ":" + port);

		oocsi.connect(hostname, port);
		if (oocsi.isConnected()) {

			log(System.out, " - connected successfully");

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

				log(System.out, " - subscribed to " + name + " (raw)");

			} else {
				oocsi.subscribe(new DataHandler() {

					@Override
					public void receive(String channelName,
							Map<String, Object> data, String sender) {
						makeEvent(channelName, data, sender);
					}
				});

				log(System.out, " - subscribed to " + name + " (data)");

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
	 * @param printStream
	 * @param x
	 */
	private void log(PrintStream printStream, String x) {
		printStream.println(x);
	}
}
