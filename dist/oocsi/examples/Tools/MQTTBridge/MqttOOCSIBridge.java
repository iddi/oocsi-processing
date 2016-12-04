package tools.mqtt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import nl.tue.id.oocsi.OOCSI;

/**
 * Bridge between MQTT and OOCSI (uni-direction from several MQTT topics to OOCSI channels)
 * 
 * @author matsfunk
 *
 */
public class MqttOOCSIBridge implements MqttCallback {

	// explicit mapping incoming MTQQ topics to OOCSI channels
	private final Map<String, String> bridgingMap = new HashMap<String, String>();

	// OOCSI client
	private final OOCSI oocsi;

	// MTQQ client
	private MqttClient mqttClient;

	/**
	 * create a bridging MTQQ - OOCSI client that will forward all messages on a specified MTQQ topic to a specified
	 * OOCSI channel
	 * 
	 * @param mqttBroker
	 * @param mqttClientId
	 * @param oocsi
	 */
	public MqttOOCSIBridge(String mqttBroker, String mqttClientId, OOCSI oocsi) {

		this.oocsi = oocsi;

		try {
			MemoryPersistence persistence = new MemoryPersistence();
			mqttClient = new MqttClient(mqttBroker, mqttClientId, persistence);
			mqttClient.setCallback(this);

			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			System.out.println();
			System.out.println("Connecting to broker: " + mqttBroker);
			mqttClient.connect(connOpts);
			System.out.println("Connected");
		} catch (MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}

	/**
	 * create a mapping between an MTQQ topic to an OOCSI channel with all incoming messages on that topic being
	 * forwarded to the OOCSI channel; note that the payload of the MTQQ message will be forwarded as a single "message"
	 * attribute in the outgoing OOCSI message
	 * 
	 * @param mqttTopic
	 * @param oocsiChannel
	 */
	public void bridge(String mqttTopic, String oocsiChannel) {
		try {
			// block until connection is established
			while (!mqttClient.isConnected()) {
				Thread.sleep(50);
			}

			// first subscribe
			mqttClient.subscribe(mqttTopic);

			// add bridge to map
			bridgingMap.put(mqttTopic, oocsiChannel);

		} catch (MqttException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String,
	 * org.eclipse.paho.client.mqttv3.MqttMessage)
	 */
	public void messageArrived(String topic, MqttMessage m) throws Exception {
		if (bridgingMap.containsKey(topic)) {
			oocsi.channel(bridgingMap.get(topic)).data("message", new String(m.getPayload())).send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
	 */
	public void connectionLost(Throwable arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
	 */
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}
}
