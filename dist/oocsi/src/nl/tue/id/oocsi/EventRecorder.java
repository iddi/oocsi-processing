package nl.tue.id.oocsi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import nl.tue.id.oocsi.client.protocol.EventHandler;
import nl.tue.id.oocsi.client.socket.Base64Coder;

/**
 * OOCSI looper is an event recorder client that listens on an OOCSI channel and records all incoming events on that
 * channel. Later on, the OOCSI looper can play recorded event back through the same channel. The looper can also store
 * recorded events to a file (in MIDI format) and load them back from file.
 *
 * @author matsfunk
 */
public class EventRecorder {

	// time resolution for recording and play-back
	private static final int PPQ = 8;

	// internal recording
	private Sequencer sequencer;
	private Sequence activeSequence;
	private Date start;

	public EventRecorder(final OOCSI oocsi, final String channelName) {
		OOCSI oocsiConnection = oocsi;

		// install MIDI sub-system
		try {
			// get default sequencing object
			sequencer = MidiSystem.getSequencer();
			if (sequencer == null) {
				oocsiConnection = null;
			} else {
				sequencer.open();
				sequencer.addMetaEventListener(new MetaEventListener() {
					@Override
					public void meta(MetaMessage meta) {
						String x = new String(meta.getData());
						String[] pieces = x.split(",", 2);
						if (pieces[0] != null && pieces[0].length() > 0 && pieces[1] != null
								&& pieces[1].length() > 0) {
							Map<String, Object> data = deserialize(pieces[1]);
							if (data != null) {
								oocsi.channel(channelName).data(data).send();
							}
						}
					}
				});
			}
		} catch (MidiUnavailableException e) {
			oocsiConnection = null;
		}

		// register OOCSI
		if (oocsiConnection != null) {
			oocsiConnection.getCommunicator().subscribe(channelName, new EventHandler() {

				public void receive(OOCSIEvent event) {
					recordEvent(event.getRecipient(), serialize(event.data));
				}

			});
		}
	}

	/**
	 * plays back the active sequence from position 0
	 * 
	 */
	public void play() {
		initMIDISequence();

		sequencer.stop();
		sequencer.setTickPosition(0);
		sequencer.start();
	}

	/**
	 * pause play-back
	 * 
	 */
	public void pause() {
		initMIDISequence();

		sequencer.stop();
	}

	/**
	 * resume play-back
	 * 
	 */
	public void resume() {
		initMIDISequence();

		sequencer.start();
	}

	/**
	 * stop play-back and reset current position to beginning of sequence (tick 0)
	 */
	public void stop() {
		initMIDISequence();

		sequencer.stop();
		sequencer.setTickPosition(0);
	}

	/**
	 * record an event to the active sequence and track; needs record to be called before
	 * 
	 * @param track
	 * @param message
	 */
	public void recordEvent(String track, String message) {

		// check conditions
		if (activeSequence == null || activeSequence.getTracks().length == 0) {
			return;
		}

		// create message for MIDI format
		byte[] b = (track + "," + message).getBytes();
		try {
			float tick = (System.currentTimeMillis() - start.getTime()) / 1000f;
			activeSequence.getTracks()[0].add(new MidiEvent(new MetaMessage(1, b, b.length), (int) (tick * PPQ * 2.0)));
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * activate recording to a new sequence with a new single track; subsequent calls to recordEvent will record an
	 * event to that sequence and track
	 * 
	 * @return
	 */
	public boolean startRecording() {
		initMIDISequence();
		if (activeSequence == null) {
			start = null;
			return false;
		} else {
			start = new Date();
			return true;
		}
	}

	/**
	 * deactivate recording; subsequent calls to recordEvent will not anymore record an event to that sequence and track
	 * until recording is started again
	 * 
	 */
	public void stopRecording() {
		start = null;
	}

	/**
	 * saves the active sequence in a MIDI file with given file name
	 * 
	 * @param filename
	 */
	public void saveSequence(String filename) {
		try {
			File f = new File(filename);
			MidiSystem.write(activeSequence, 1, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * loads the active sequence from a MIDI file with given file name
	 * 
	 * @param filename
	 */
	public void loadSequence(String filename) {
		try {
			File f = new File(filename);
			Sequence s = MidiSystem.getSequence(f);
			sequencer.setSequence(s);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * initialize a MIDI sequence with a track to record to
	 * 
	 */
	private void initMIDISequence() {
		try {
			if (activeSequence == null) {
				activeSequence = new Sequence(Sequence.PPQ, PPQ);
			}

			if (activeSequence.getTracks().length == 0) {
				activeSequence.createTrack();
				sequencer.setSequence(activeSequence);
			}
		} catch (InvalidMidiDataException e) {
			activeSequence = null;
		}
	}

	/**
	 * serialize a map of key value pairs
	 * 
	 * @param data
	 * @return
	 */
	private String serialize(Map<String, Object> data) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			byte[] rawData = baos.toByteArray();
			return new String(Base64Coder.encode(rawData));
		} catch (IOException e) {
			// in case of problems we return no data
			return "";
		}
	}

	/**
	 * deserialize a map of key value pairs
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> deserialize(String data) {
		try {
			// parsing of serialized object
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decode(data));
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (Map<String, Object>) ois.readObject();
		} catch (ClassNotFoundException e) {
			// in case of problems we return no data
		} catch (IOException e) {
			// in case of problems we return no data
		}
		return null;
	}
}
