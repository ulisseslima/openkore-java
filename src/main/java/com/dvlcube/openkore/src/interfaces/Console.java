package com.dvlcube.openkore.src.interfaces;

import com.dvlcube.openkore.src.Interface;

/**
 * Command line interface.
 * 
 * @since 17/01/2013
 * @author Ulisses Lima
 */
public class Console {
	private Interface sInterface;

	public Interface getsInterface() {
		return sInterface;
	}

	public void setsInterface(Interface sInterface) {
		this.sInterface = sInterface;
	}
}
