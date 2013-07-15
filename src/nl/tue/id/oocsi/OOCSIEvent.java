package nl.tue.id.oocsi;

import java.util.Map;

/**
 * event class for receiving events from OOCSI
 * 
 * @author mfunk
 * 
 */
public class OOCSIEvent {

	public String channelName;
	public String sender;

	private Map<String, Object> data;

	OOCSIEvent(String channelName, Map<String, Object> data, String sender) {
		this.channelName = channelName;
		this.data = data;
		this.sender = sender;
	}

	/**
	 * get value for the given key as int
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String key, int defaultValue) {
		Object result = this.data.get(key);
		if (result != null) {
			if (result instanceof Integer) {
				return ((Integer) result).intValue();
			} else {
				try {
					return Integer.parseInt(result.toString());
				} catch (NumberFormatException nfe) {
					return defaultValue;
				}
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * get value for the given key as long
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String key, long defaultValue) {
		Object result = this.data.get(key);
		if (result != null) {
			if (result instanceof Long) {
				return ((Long) result).longValue();
			} else {
				try {
					return Long.parseLong(result.toString());
				} catch (NumberFormatException nfe) {
					return defaultValue;
				}
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * get the value for the given key as Object
	 * 
	 * @param key
	 * @return
	 */
	public Object getObject(String key) {
		return this.data.get(key);
	}
}
