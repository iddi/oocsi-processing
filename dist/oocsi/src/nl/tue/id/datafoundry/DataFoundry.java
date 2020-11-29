package nl.tue.id.datafoundry;

/**
 * Java client API for Data Foundry
 * 
 * This mini-library allows to access IoT and Entity datasets on a Data Foundry server. That is, to log IoT data and to
 * access items of an Entity dataset: get, add, update, and delete.
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
public class DataFoundry {

	private String server;

	/**
	 * create a data foundry connection
	 * 
	 * @param server
	 */
	public DataFoundry(String server) {
		this.server = server;
	}

	/**
	 * create access to a dataset with a given <code>id</code> and HTTP access <code>token</code>
	 * 
	 * @param id
	 * @param token
	 * @return
	 */
	public DFDataset dataset(long id, String token) {
		return new DFDataset(server, id, token);
	}

}
