package nl.tue.id.oocsi;

import java.util.HashMap;
import java.util.Map;

import nl.tue.id.oocsi.client.OOCSIClient;

public class OOCSIMessage extends OOCSIEvent {

	private OOCSIClient oocsi;
	private Map<String, Object> data;
	private boolean isSent = false;

	/**
	 * create a new message
	 * 
	 * @param oocsi
	 * @param channelName
	 */
	public OOCSIMessage(OOCSIClient oocsi, String channelName) {
		super(channelName, null, "");

		this.oocsi = oocsi;
		this.data = new HashMap<String, Object>();
	}

	/**
	 * store data in message
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public OOCSIMessage data(String key, String value) {

		// store data
		this.data.put(key, value);

		return this;
	}

	/**
	 * store data in message
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public OOCSIMessage data(String key, int value) {

		// store data
		this.data.put(key, value);

		return this;
	}

	/**
	 * store data in message
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public OOCSIMessage data(String key, long value) {

		// store data
		this.data.put(key, value);

		return this;
	}

	/**
	 * send message
	 * 
	 */
	public void send() {

		// but send only once
		if (!isSent) {
			isSent = true;
			oocsi.send(channelName, data);
		}
	}
}
