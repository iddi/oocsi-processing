package nl.tue.id.oocsi;

import java.lang.reflect.Method;
import java.util.Map;

import nl.tue.id.oocsi.client.Handler;
import nl.tue.id.oocsi.client.OOCSIClient;
import processing.core.PApplet;

public class OOCSI {

	private Method handlerEventMethod;
	private Method handlerEventDataMethod;
	private PApplet parent;

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

	private void startOOCSIConnection(String name, String hostname, int port) {
		oocsi = new OOCSIClient(name);
		oocsi.connect(hostname, port);

		if (oocsi.isConnected()) {

			// subscribe to self handler
			oocsi.subscribe(new Handler() {

				@Override
				public void receive(String arg0, String arg1, String arg2) {

				}
			});
		}
	}

	private void instrumentPApplet(PApplet parent) {
		try {
			handlerEventMethod = parent.getClass().getDeclaredMethod(
					"handleOOCSIEvent",
					new Class[] { String.class, String.class, String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
		try {
			handlerEventDataMethod = parent.getClass().getDeclaredMethod(
					"handleOOCSIEvent",
					new Class[] { String.class, Map.class, String.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// makeEvent("channel", "data", "recipient");
		// }
		// }).start();
	}

	private void makeEvent(String channelName, String data, String sender) {
		if (handlerEventMethod != null) {
			try {
				handlerEventMethod.invoke(parent, new Object[] { channelName,
						data, sender });
			} catch (Exception e) {
				handlerEventMethod = null;
				e.printStackTrace();
			}
		}
	}

	private void makeEvent(String channelName, Map<String, Object> data,
			String sender) {
		if (handlerEventMethod != null) {
			try {
				handlerEventMethod.invoke(parent, new Object[] { channelName,
						data, sender });
			} catch (Exception e) {
				handlerEventMethod = null;
				e.printStackTrace();
			}
		}
	}
}
