package nl.tue.id.datafoundry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dataset class for all operations on an IoT or Entity dataset.
 *
 * <hr>
 * 
 * <code>
 *   // fill in the Data Foundry server URL, just the domain (add port if needed) <br>
 * 	 DataFoundry df = new DataFoundry("server.url.com"); <br>
 *   // create dataset access <br>
 * 	 DFDataset iot = df.dataset(2, "tokentokentokentokentoken1234567890+++"); <br>
 *   <br>
 *   // log to IoT dataset <br>
 *   iot.device("d123456789").activity("indoor_measurement").data("temperature", 34).data("door", "open").log(); <br>
 *   <br><br>
 *   // create dataset access <br>
 * 	 DFDataset entity = df.dataset(15, "tokentokentokentokentoken1234567890+++"); <br>
 *   // create dataset item access <br>
 * 	 DFDataset item = df.dataset(15, "tokentokentokentokentoken1234567890+++").id("userX").token("nosecrets"); <br>
 *   <br>
 *   // access Entity dataset <br>
 *   // add an item <br>
 *   item.data("temperature", 34).data("door", "open").add(); <br>
 *   <br>
 *   // get an item <br>
 *   Map&lt;String, Object&gt; itemData = item.get(); <br>
 *   <br>
 *   // update an item <br>
 *   item.data("temperature", 32).update(); <br>
 *   <br>
 *   // delete an item <br>
 *   item.delete(); <br>
 * </code>
 * 
 * <hr>
 * 
 * @author Mathias Funk, 2020
 *
 */
public class DFDataset {

	// server, dataset, access token
	private final String protocol;
	private final String server;
	private final long dataset;
	private String access_token = "";

	// iot
	private String deviceId = "";
	private String activity = "";

	// entity / item access
	private String recordId = "";
	private String recordToken = "";

	// temp data for submission
	private Map<String, String> map = new HashMap<String, String>();

	DFDataset(String server, long dataset, String token) {
		this.protocol = server.startsWith("localhost") ? "http://" : "https://";
		this.server = server;
		this.dataset = dataset;
		this.access_token = token;
	}

	/**
	 * set the device for logging to an IoT dataset
	 * 
	 * @param deviceId
	 * @return
	 */
	public DFDataset device(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	/**
	 * set the activity for logging to an IoT dataset
	 * 
	 * @param activity
	 * @return
	 */
	public DFDataset activity(String activity) {
		this.activity = activity;
		return this;
	}

	/**
	 * set the item ID for accessing an Entity dataset item
	 * 
	 * @param id
	 * @return
	 */
	public DFDataset id(String id) {
		this.recordId = id;
		return this;
	}

	/**
	 * set the item token for accessing an Entity dataset item
	 * 
	 * @param token
	 * @return
	 */
	public DFDataset token(String token) {
		this.recordToken = token;
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, boolean value) {
		map.put(key, Boolean.toString(value));
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, int value) {
		map.put(key, Integer.toString(value));
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, long value) {
		map.put(key, Long.toString(value));
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, float value) {
		map.put(key, Float.toString(value));
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, double value) {
		map.put(key, Double.toString(value));
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, String value) {
		map.put(key, value);
		return this;
	}

	/**
	 * add a piece of data (key - value) for a sending to an IoT or Entity dataset
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DFDataset data(String key, Object value) {
		map.put(key, value.toString());
		return this;
	}

	/**
	 * log an entry to an IoT dataset
	 * 
	 */
	public synchronized void log() {
		String data = ("{" + map.entrySet().stream()
		        .map(e -> "\"" + e.getKey() + "\"" + ":\"" + String.valueOf(e.getValue()) + "\"")
		        .collect(Collectors.joining(", ")) + "}");

		try {
			URL url = new URL(protocol + server + "/datasets/ts/log/" + dataset + "/" + activity);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);

			byte[] out = data.getBytes(StandardCharsets.UTF_8);
			http.setFixedLengthStreamingMode(out.length);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("api_token", access_token);
			http.setRequestProperty("source_id", deviceId);
			http.setRequestProperty("device_id", deviceId);
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// clear input data
			map.clear();
		}
	}

	/**
	 * get an entry from an entity dataset
	 * 
	 * @return
	 */
	public synchronized Map<String, Object> get() {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			URL url = new URL(protocol + server + "/datasets/entity/" + dataset + "/item/");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");

			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("api_token", access_token);
			http.setRequestProperty("resource_id", recordId);
			http.setRequestProperty("token", recordToken);
			http.connect();
			InputStream inputStream = http.getInputStream();
			String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
			        .collect(Collectors.joining("\n"));

			Object obj = new JSONReader().read(text);
			if (obj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> rawMap = (Map<Object, Object>) obj;
				rawMap.entrySet().stream().forEach(e -> {
					result.put(e.getKey().toString(), e.getValue());
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * add an entry to an entity dataset
	 * 
	 */
	public synchronized void add() {
		String data = "{" + map.entrySet().stream()
		        .map(e -> "\"" + e.getKey() + "\"" + ":\"" + String.valueOf(e.getValue()) + "\"")
		        .collect(Collectors.joining(", ")) + "}";

		try {
			URL url = new URL(protocol + server + "/datasets/entity/" + dataset + "/item/");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);

			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			http.setFixedLengthStreamingMode(out.length);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("api_token", access_token);
			http.setRequestProperty("resource_id", recordId);
			http.setRequestProperty("token", recordToken);
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// clear input data
			map.clear();
		}
	}

	/**
	 * update an entry in an entity dataset
	 * 
	 */
	public synchronized void update() {
		String data = "{" + map.entrySet().stream()
		        .map(e -> "\"" + e.getKey() + "\"" + ":\"" + String.valueOf(e.getValue()) + "\"")
		        .collect(Collectors.joining(", ")) + "}";

		try {
			URL url = new URL(protocol + server + "/datasets/entity/" + dataset + "/item/");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("PUT");
			http.setDoOutput(true);

			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			http.setFixedLengthStreamingMode(out.length);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("api_token", access_token);
			http.setRequestProperty("resource_id", recordId);
			http.setRequestProperty("token", recordToken);
			http.connect();
			try (OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// clear input data
			map.clear();
		}
	}

	/**
	 * delete an entry from an entity dataset
	 * 
	 */
	public synchronized void delete() {
		try {
			URL url = new URL(protocol + server + "/datasets/entity/" + dataset + "/item/");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("DELETE");

			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("api_token", access_token);
			http.setRequestProperty("resource_id", recordId);
			http.setRequestProperty("token", recordToken);
			http.connect();
			http.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * JSONReader is part of the StringTree library (https://github.com/efficacy/stringtree)
	 * 
	 * Apache licence 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
	 *
	 */
	private static class JSONReader {

		protected static final Object OBJECT_END = new Object();
		protected static final Object ARRAY_END = new Object();
		protected static final Object COLON = new Object();
		protected static final Object COMMA = new Object();
		public static final int FIRST = 0;
		public static final int CURRENT = 1;
		public static final int NEXT = 2;

		protected static Map<Character, Character> escapes = new HashMap<Character, Character>();
		static {
			escapes.put(Character.valueOf('"'), Character.valueOf('"'));
			escapes.put(Character.valueOf('\\'), Character.valueOf('\\'));
			escapes.put(Character.valueOf('/'), Character.valueOf('/'));
			escapes.put(Character.valueOf('b'), Character.valueOf('\b'));
			escapes.put(Character.valueOf('f'), Character.valueOf('\f'));
			escapes.put(Character.valueOf('n'), Character.valueOf('\n'));
			escapes.put(Character.valueOf('r'), Character.valueOf('\r'));
			escapes.put(Character.valueOf('t'), Character.valueOf('\t'));
		}

		protected CharacterIterator it;
		protected char c;
		protected Object token;
		protected StringBuffer buf = new StringBuffer();

		public void reset() {
			it = null;
			c = 0;
			token = null;
			buf.setLength(0);
		}

		protected char next() {
			c = it.next();
			return c;
		}

		protected void skipWhiteSpace() {
			while (Character.isWhitespace(c)) {
				next();
			}
		}

		public Object read(CharacterIterator ci, int start) {
			reset();
			it = ci;
			switch (start) {
			case FIRST:
				c = it.first();
				break;
			case CURRENT:
				c = it.current();
				break;
			case NEXT:
				c = it.next();
				break;
			}
			return read();
		}

//		public Object read(CharacterIterator it) {
//			return read(it, NEXT);
//		}

		public Object read(String string) {
			return read(new StringCharacterIterator(string), FIRST);
		}

		protected Object read() {
			skipWhiteSpace();
			char ch = c;
			next();
			switch (ch) {
			case '"':
				token = string();
				break;
			case '[':
				token = array();
				break;
			case ']':
				token = ARRAY_END;
				break;
			case ',':
				token = COMMA;
				break;
			case '{':
				token = object();
				break;
			case '}':
				token = OBJECT_END;
				break;
			case ':':
				token = COLON;
				break;
			case 't':
				next();
				next();
				next(); // assumed r-u-e
				token = Boolean.TRUE;
				break;
			case 'f':
				next();
				next();
				next();
				next(); // assumed a-l-s-e
				token = Boolean.FALSE;
				break;
			case 'n':
				next();
				next();
				next(); // assumed u-l-l
				token = null;
				break;
			default:
				c = it.previous();
				if (Character.isDigit(c) || c == '-') {
					token = number();
				}
			}
			// System.out.println("token: " + token); // enable this line to see the token stream
			return token;
		}

		protected Object object() {
			Map<Object, Object> ret = new LinkedHashMap<Object, Object>();
			Object key = read();
			while (token != OBJECT_END) {
				read(); // should be a colon
				if (token != OBJECT_END) {
					ret.put(key, read());
					if (read() == COMMA) {
						key = read();
					}
				}
			}

			return ret;
		}

		protected Object array() {
			List<Object> ret = new ArrayList<Object>();
			Object value = read();
			while (token != ARRAY_END) {
				ret.add(value);
				if (read() == COMMA) {
					value = read();
				}
			}
			return ret;
		}

		protected Object number() {
			int length = 0;
			boolean isFloatingPoint = false;
			buf.setLength(0);

			if (c == '-') {
				add();
			}
			length += addDigits();
			if (c == '.') {
				add();
				length += addDigits();
				isFloatingPoint = true;
			}
			if (c == 'e' || c == 'E') {
				add();
				if (c == '+' || c == '-') {
					add();
				}
				addDigits();
				isFloatingPoint = true;
			}

			String s = buf.toString();
			return isFloatingPoint ? (length < 17) ? (Object) Double.valueOf(s) : new BigDecimal(s)
			        : (length < 19) ? (Object) Long.valueOf(s) : new BigInteger(s);
		}

		protected int addDigits() {
			int ret;
			for (ret = 0; Character.isDigit(c); ++ret) {
				add();
			}
			return ret;
		}

		protected Object string() {
			buf.setLength(0);
			while (c != '"') {
				if (c == '\\') {
					next();
					if (c == 'u') {
						add(unicode());
					} else {
						Object value = escapes.get(Character.valueOf(c));
						if (value != null) {
							add(((Character) value).charValue());
						}
					}
				} else {
					add();
				}
			}
			next();

			return buf.toString();
		}

		protected void add(char cc) {
			buf.append(cc);
			next();
		}

		protected void add() {
			add(c);
		}

		protected char unicode() {
			int value = 0;
			for (int i = 0; i < 4; ++i) {
				switch (next()) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					value = (value << 4) + c - '0';
					break;
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
					value = (value << 4) + (c - 'a') + 10;
					break;
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
					value = (value << 4) + (c - 'A') + 10;
					break;
				}
			}
			return (char) value;
		}
	}
}
