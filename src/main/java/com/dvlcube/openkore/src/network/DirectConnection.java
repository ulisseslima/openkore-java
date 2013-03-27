package com.dvlcube.openkore.src.network;

import java.lang.ref.WeakReference;
import java.net.Socket;

/**
 * 
 * MODULE DESCRIPTION: Connection handling <br>
 * <br>
 * The Network module handles connections to the Ragnarok Online server. <br>
 * This module only handles connection issues, and nothing else. It doesn't do <br>
 * anything with the actual data. Network data handling is performed by <br>
 * the @MODULE(Network::Receive) and Network::Receive::ServerTypeX classes. <br>
 * <br>
 * The submodule @MODULE(Network::Send) contains functions for sending all <br>
 * kinds of messages to the RO server. <br>
 * <br>
 * Please also read <a
 * href="http://wiki.openkore.com/index.php/Network_subsystem">the <br>
 * network subsystem overview.</a> <br>
 * <br>
 * This implementation establishes a direct connection to the RO server. <br>
 * Note that there are alternative implementations for this interface:
 * 
 * @MODULE(Network::XKore), <br>
 * @MODULE(Network::XKore2) and @MODULE(Network::XKoreProxy)
 * 
 * @since 25/01/2013
 * @author Ulisses Lima
 */
public class DirectConnection extends Network {
	public Socket remote_socket;
	public int version = 0;

	public WeakReference<?> wrapper;

	/**
	 * Network::DirectConnection->new([wrapper]) <br>
	 * wrapper: If this object is to be wrapped by another object which is
	 * interface-compatible <br>
	 * with the Network::DirectConnection class, then specify the wrapper object
	 * here. The message <br>
	 * sender will use this wrapper to send socket data. Internally, the
	 * reference to the wrapper <br>
	 * will be stored as a weak reference. <br>
	 * <br>
	 * Create a new Network::DirectConnection object. The connection is not yet
	 * established.
	 * 
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public DirectConnection() {
		remote_socket = new Socket();
	}

	public DirectConnection(WeakReference<?> wrapper) {
		remote_socket = new Socket();
		if (wrapper != null) {
			this.wrapper = wrapper;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		serverDisconnect();
	}

	/**
	 * boolean $net->serverAliveServer() <br>
	 * <br>
	 * Check whether the connection to the server is alive.
	 * 
	 * @return
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public boolean serverAlive() {
		return remote_socket != null && remote_socket.isConnected();
	}

	/**
	 * $net->serverDisconnect() <br>
	 * <br>
	 * Disconnect from the current Ragnarok Online server. <br>
	 * <br>
	 * This function is used internally by $net->checkConnection() and should
	 * not be used directly.
	 * 
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public void serverDisconnect() {
		if (serverAlive()) {
			// if()
		}
	}
}
