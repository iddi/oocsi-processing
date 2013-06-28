import java.util.Map;

import nl.tue.id.oocsi.OOCSI;
import processing.core.PApplet;

@SuppressWarnings("serial")
public class ExampleApplet extends PApplet {

	int color = 255;

	public void setup() {
		size(200, 200);

		new OOCSI(this, "test", "localhost");
	}

	public void draw() {
		background(255);

		stroke(120);
		fill(color);
		rect(20, 20, 20, 20);
	}

	void handleOOCSIEvent(String channel, Map<String, Object> data,
			String recipient) {
		color = 80;
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { "ExampleApplet" });
	}
}
