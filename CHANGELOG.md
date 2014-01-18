2014-01-18
 
 * implemented service discovery mechanism for the oocsi server. new classes are in nl.tue.id.oocsi.server.discovery.
 * added an constructor to SocketSever to allow specify a special service name. by default it is "oocsiService".
 * changed init() in SocketServer: serverSocket is now created by the OOCSIServiceProvider; !!CHECK IT!!
 * changes in OOCSIServer: added a parameter "-service" for a different service name other than "oocsiService".
 * restructured the constructor of OOCSI. now allows "new OOCSI(this, "Name", "address:port");". 
 * added list() and list(service name) to OOCSI. it lists all servers (string array, in the format of "address:port") discovered in the network.
 * added DiscoveryChannelSender, DiscoveryChannelReceiver to the examples to demonstrate service discovery.
 * added OocsiServer to allow start a server in Processing.
 * recompiled oocsi.jar, now also include the oocsi-server in the jar. 
 