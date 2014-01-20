2014-01-20
 * Implemented a unit test for service discovery;
 * added setPerformancePreferences and setReuseAddress in OOCSIServiceProvider, to match the configuration code in init() of SocketServer;
 * added configuration constants to ServiceConstants for setPerformancePreferences and setReuseAddress

2014-01-19

 * Detailed inline documentation for ServiceConstants
 * Refactor: responder => attendant

2014-01-18
 
 * implemented service discovery mechanism for the oocsi server. new classes are in nl.tue.id.oocsi.server.discovery.
 * added an constructor to SocketSever to allow specify a special service name. by default it is "oocsiService".
 * changed init() in SocketServer: serverSocket is now created by the OOCSIServiceProvider; !!CHECK IT!!
 * changes in OOCSIServer: added a parameter "-service" for a different service name other than "oocsiService".
 * restructured the constructor of OOCSI. now allows "new OOCSI(this, "Name", "address:port");". 
 * added list() and list(service name) to OOCSI. it lists all servers (string array, in the format of "address:port") discovered in the network.
 * added DiscoveryChannelSender, DiscoveryChannelReceiver to the examples to demonstrate service discovery.
 * added OocsiServer to show how to start a server in Processing.
 * recompiled oocsi.jar, now also include the oocsi-server in the jar. 
 