package nl.tue.id.oocsi;

import nl.tue.id.oocsi.client.behavior.OOCSIAwareness;
import nl.tue.id.oocsi.client.behavior.OOCSIConsensus;
import nl.tue.id.oocsi.client.behavior.OOCSIGather;
import nl.tue.id.oocsi.client.behavior.OOCSISpread;
import nl.tue.id.oocsi.client.behavior.OOCSISync;

public class Constellation {

	/** CONSENSUS */

	static public final OOCSIConsensus<Integer> createIntegerConsensus(OOCSI oocsi, String channelName, String key,
			int timeout) {
		return OOCSIConsensus.createIntegerConsensus(oocsi.getCommunicator(), channelName, key, timeout);
	}

	static public final OOCSIConsensus<Integer> createIntegerConsensus(OOCSI oocsi, String channelName, String key,
			int timeout, String handlerName) {
		return OOCSIConsensus.createIntegerConsensus(oocsi.getCommunicator(), channelName, key, timeout,
				oocsi.getHandlerFor(handlerName));
	}

	static public OOCSIConsensus<Integer> createIntegerAvgConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS) {
		return OOCSIConsensus.createIntegerAvgConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS);
	}

	static public OOCSIConsensus<Integer> createIntegerAvgConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS, String handlerName) {
		return OOCSIConsensus.createIntegerAvgConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS,
				oocsi.getHandlerFor(handlerName));
	}

	static public OOCSIConsensus<Float> createFloatAvgConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS) {
		return OOCSIConsensus.createFloatAvgConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS, null);
	}

	static public OOCSIConsensus<Float> createFloatAvgConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS, String handlerName) {
		return OOCSIConsensus.createFloatAvgConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS,
				oocsi.getHandlerFor(handlerName));
	}

	static public OOCSIConsensus<String> createStringConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS) {
		return OOCSIConsensus.createStringConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS);
	}

	static public OOCSIConsensus<String> createStringConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS, String handlerName) {
		return OOCSIConsensus.createStringConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS,
				oocsi.getHandlerFor(handlerName));
	}

	static public OOCSIConsensus<Boolean> createBooleanConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS) {
		return OOCSIConsensus.createBooleanConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS);
	}

	static public OOCSIConsensus<Boolean> createBooleanConsensus(OOCSI oocsi, String channelName, String key,
			int timeoutMS, String handlerName) {
		return OOCSIConsensus.createBooleanConsensus(oocsi.getCommunicator(), channelName, key, timeoutMS,
				oocsi.getHandlerFor(handlerName));
	}

	/** GATHER */

	public static OOCSIGather<Integer> createIntegerGather(OOCSI oocsi, String channelName, String key, int timeout) {
		return new OOCSIGather<Integer>(oocsi.getCommunicator(), channelName, key, timeout);
	}

	/** SPREAD */

	public static OOCSISpread createSpread(OOCSI oocsi, String channelName, String key, int timeout) {
		return new OOCSISpread(oocsi.getCommunicator(), channelName, key, timeout);
	}

	/** SYNC */

	public static OOCSISync createSync(OOCSI oocsi, String channelName, int timeout, String handlerName) {
		return new OOCSISync(oocsi.getCommunicator(), channelName, timeout, oocsi.getHandlerFor(handlerName));
	}

	/** AWARENESS */

	public static OOCSIAwareness createAwareness(OOCSI oocsi, String channelName, String... keys) {
		return new OOCSIAwareness(oocsi.getCommunicator(), channelName, keys);
	}

	public static OOCSIAwareness createAwareness(OOCSI oocsi, String channelName, int timeout, String... keys) {
		return new OOCSIAwareness(oocsi.getCommunicator(), channelName, timeout, keys);
	}

	/** VARIABLE */

	public static OOCSIBoolean createBoolean(OOCSI oocsi, String channelName, String key) {
		return new OOCSIBoolean(oocsi.getCommunicator(), channelName, key);
	}

	public static OOCSIBoolean createBoolean(OOCSI oocsi, String channelName, String key, Boolean referenceValue) {
		return new OOCSIBoolean(oocsi.getCommunicator(), channelName, key, referenceValue);
	}

	public static OOCSIBoolean createBoolean(OOCSI oocsi, String channelName, String key, Boolean referenceValue,
			int timeout) {
		return new OOCSIBoolean(oocsi.getCommunicator(), channelName, key, referenceValue, timeout);
	}

	public static OOCSIInt createInteger(OOCSI oocsi, String channelName, String key) {
		return new OOCSIInt(oocsi.getCommunicator(), channelName, key);
	}

	public static OOCSIInt createInteger(OOCSI oocsi, String channelName, String key, Integer referenceValue) {
		return new OOCSIInt(oocsi.getCommunicator(), channelName, key, referenceValue);
	}

	public static OOCSIInt createInteger(OOCSI oocsi, String channelName, String key, Integer referenceValue,
			int timeout) {
		return new OOCSIInt(oocsi.getCommunicator(), channelName, key, referenceValue, timeout);
	}

	public static OOCSIFloat createFloat(OOCSI oocsi, String channelName, String key) {
		return new OOCSIFloat(oocsi.getCommunicator(), channelName, key);
	}

	public static OOCSIFloat createFloat(OOCSI oocsi, String channelName, String key, Float referenceValue) {
		return new OOCSIFloat(oocsi.getCommunicator(), channelName, key, referenceValue);
	}

	public static OOCSIFloat createFloat(OOCSI oocsi, String channelName, String key, Float referenceValue,
			int timeout) {
		return new OOCSIFloat(oocsi.getCommunicator(), channelName, key, referenceValue, timeout);
	}

	public static OOCSILong createLong(OOCSI oocsi, String channelName, String key) {
		return new OOCSILong(oocsi.getCommunicator(), channelName, key);
	}

	public static OOCSILong createLong(OOCSI oocsi, String channelName, String key, Long referenceValue) {
		return new OOCSILong(oocsi.getCommunicator(), channelName, key, referenceValue);
	}

	public static OOCSILong createLong(OOCSI oocsi, String channelName, String key, Long referenceValue, int timeout) {
		return new OOCSILong(oocsi.getCommunicator(), channelName, key, referenceValue, timeout);
	}

	public static OOCSIDouble createDouble(OOCSI oocsi, String channelName, String key) {
		return new OOCSIDouble(oocsi.getCommunicator(), channelName, key);
	}

	public static OOCSIDouble createDouble(OOCSI oocsi, String channelName, String key, Double referenceValue) {
		return new OOCSIDouble(oocsi.getCommunicator(), channelName, key, referenceValue);
	}

	public static OOCSIDouble createDouble(OOCSI oocsi, String channelName, String key, Double referenceValue,
			int timeout) {
		return new OOCSIDouble(oocsi.getCommunicator(), channelName, key, referenceValue, timeout);
	}

	public static OOCSIString createString(OOCSI oocsi, String channelName, String key) {
		return new OOCSIString(oocsi.getCommunicator(), channelName, key);
	}

	public static OOCSIString createString(OOCSI oocsi, String channelName, String key, String referenceValue) {
		return new OOCSIString(oocsi.getCommunicator(), channelName, key, referenceValue);
	}

	public static OOCSIString createString(OOCSI oocsi, String channelName, String key, String referenceValue,
			int timeout) {
		return new OOCSIString(oocsi.getCommunicator(), channelName, key, referenceValue, timeout);
	}
}
