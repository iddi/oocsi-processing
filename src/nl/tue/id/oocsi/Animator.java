package nl.tue.id.oocsi;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import nl.tue.id.oocsi.client.protocol.OOCSIMessage;

public class Animator {

	final OOCSI oocsi;
	final String channel;
	final String attribute;

	final List<Step<?>> steps = new LinkedList<>();
	int nextStep = 0;
	boolean looping;
	boolean running;
	final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	/**
	 * create an animator with an OOCSI reference, a channel name where the animation messages should be sent and a
	 * message attribute as a key for the values that are sent
	 * 
	 * @param oocsi
	 * @param channel
	 * @param attribute
	 */
	public Animator(OOCSI oocsi, String channel, String attribute) {
		this.oocsi = oocsi;
		this.channel = channel;
		this.attribute = attribute;
	}

	/**
	 * start the animator with the first step
	 * 
	 */
	public synchronized void start() {
		if (!running) {
			running = true;
			nextStep = 0;
			nextStep();
		}
	}

	/**
	 * proceed to the next step, unless the animator was stopped before
	 * 
	 */
	private void nextStep() {
		if (!running) {
			return;
		}

		if (steps.size() > nextStep) {
			scheduler.schedule(steps.get(nextStep++), 0, TimeUnit.MILLISECONDS);
		} else if (looping) {
			nextStep = 0;
			scheduler.schedule(steps.get(nextStep++), 0, TimeUnit.MILLISECONDS);
		} else {
			running = false;
		}
	}

	/**
	 * stop the animator (will not reset the steps, so you can resume the animation later)
	 * 
	 */
	public synchronized void stop() {
		running = false;
	}

	/**
	 * resume the animation
	 * 
	 */
	public synchronized void resume() {
		if (!running) {
			running = true;
			nextStep();
		}
	}

	/**
	 * set the animation behavior to looping (true) or not (false)
	 * 
	 * @param looping
	 */
	public void loop(boolean looping) {
		this.looping = looping;
	}

	/**
	 * add an animation step with delay in milliseconds and a value that will be sent after the delay has passed
	 * 
	 * @param delay
	 * @param value
	 */
	public <K> void addStep(long delay, K value) {
		steps.add(new Step<K>(delay, value, null));
	}

	/**
	 * add a named animation step with delay in milliseconds and a value that will be sent after the delay has passed.
	 * the name will be sent as well
	 * 
	 * @param delay
	 * @param value
	 * @param name
	 */
	public <K> void addStep(long delay, K value, String name) {
		steps.add(new Step<K>(delay, value, name));
	}

	/**
	 * add an animation step with random delay in milliseconds between <code>delayMin</code> and <code>delayMax</code>.
	 * a random value between <code>valueMin</code> and <code>valueMax</code> will be sent after the delay has passed
	 * 
	 * @param delayMin
	 * @param delayMax
	 * @param valueMin
	 * @param valueMax
	 */
	public void addRandomStep(long delayMin, long delayMax, float valueMin, float valueMax) {
		steps.add(new RandomStep(delayMin, delayMax, valueMin, valueMax, null));
	}

	/**
	 * add an animation step with random delay in milliseconds between <code>delayMin</code> and <code>delayMax</code>.
	 * a random value between <code>valueMin</code> and <code>valueMax</code> will be sent after the delay has passed.
	 * the name will be sent as well.
	 * 
	 * @param delayMin
	 * @param delayMax
	 * @param valueMin
	 * @param valueMax
	 * @param name
	 */
	public void addRandomStep(long delayMin, long delayMax, float valueMin, float valueMax, String name) {
		steps.add(new RandomStep(delayMin, delayMax, valueMin, valueMax, name));
	}

	/**
	 * internal class to model a single animated step
	 * 
	 */
	class Step<K> implements Runnable {
		long delay;
		K value;
		String name;

		Step(long delay, K value, String name) {
			this.delay = delay;
			this.value = value;
			this.name = name;
		}

		public void run() {
			try {
				Thread.sleep(getDelay());
				OOCSIMessage msg = oocsi.channel(channel).data(attribute, getValue());
				if (name != null && !attribute.equals("step")) {
					msg.data("step", name);
				}
				msg.send();
				nextStep();
			} catch (InterruptedException e) {
			}
		}

		protected long getDelay() {
			return delay;
		}

		protected K getValue() {
			return value;
		}
	}

	class RandomStep extends Step<Float> {

		long delayMax;
		float valueMax;

		RandomStep(long delayMin, long delayMax, float valueMin, float valueMax, String name) {
			super(delayMin, valueMin, name);
			this.delayMax = delayMax;
			this.valueMax = valueMax;
		}

		@Override
		protected long getDelay() {
			return delay + (long) (Math.random() * (delayMax - delay));
		}

		@Override
		protected Float getValue() {
			return value + (float) (Math.random() * (valueMax - value));
		}

	}

}
