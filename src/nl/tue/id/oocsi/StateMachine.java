package nl.tue.id.oocsi;

import nl.tue.id.oocsi.client.protocol.EventHandler;
import nl.tue.id.oocsi.client.protocol.Handler;

public class StateMachine {

	private nl.tue.id.oocsi.client.behavior.state.OOCSIStateMachine sm;

	private OOCSICommunicator oocsi;

	public StateMachine(OOCSI oocsi) {
		this.oocsi = oocsi.getCommunicator();
		sm = new nl.tue.id.oocsi.client.behavior.state.OOCSIStateMachine();
	}

	public State addState(String name, String enterAction, String executeAction, String exitAction) {
		Handler enter = oocsi.createSimpleCallerHandler(enterAction);
		Handler execute = oocsi.createSimpleCallerHandler(executeAction);
		Handler exit = oocsi.createSimpleCallerHandler(exitAction);

		sm.addState(name, enter, execute, exit);
		return new State(name);
	}

	public State addState(String name) {
		sm.addState(name, null, null, null);
		return new State(name);
	}

	public void execute() {
		sm.execute();
	}

	public boolean isInState(String state) {
		return sm.isInState(state);
	}

	public String get() {
		return sm.get();
	}

	public void set(String newState) {
		sm.set(newState);
	}

	public class State {

		private String name;

		public State(String name) {
			this.name = name;
		}

		public State enter(String enterAction) {
			sm.get(name).setEnter(oocsi.createSimpleCallerHandler(enterAction));
			return this;
		}

		public State execute(String executeAction) {
			sm.get(name).setExecute(oocsi.createSimpleCallerHandler(executeAction));
			return this;
		}

		public State exit(String exitAction) {
			sm.get(name).setExit(oocsi.createSimpleCallerHandler(exitAction));
			return this;
		}

		public void connect(String channelName, final String key) {
			oocsi.subscribe(channelName, new EventHandler() {
				@Override
				public void receive(OOCSIEvent event) {
					if (event.has(key)) {
						sm.set(name);
					}
				}
			});
		}

		public void connect(String channelName, final String key, final Number value) {
			oocsi.subscribe(channelName, new EventHandler() {
				@Override
				public void receive(OOCSIEvent event) {
					if (event.has(key) && event.getDouble(key, Double.MAX_VALUE) == value.doubleValue()) {
						sm.set(name);
					}
				}
			});
		}

		public void connect(String channelName, final String key, final String value) {
			oocsi.subscribe(channelName, new EventHandler() {
				@Override
				public void receive(OOCSIEvent event) {
					if (event.has(key) && event.getString(key, "").equals(value)) {
						sm.set(name);
					}
				}
			});
		}
	}
}
