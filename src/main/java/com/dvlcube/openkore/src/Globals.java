package com.dvlcube.openkore.src;

import com.dvlcube.openkore.adaptations.PerlMap;
import com.dvlcube.openkore.src.network.DirectConnection;
import com.dvlcube.openkore.src.network.MessageTokenizer;
import com.dvlcube.openkore.src.network.Network;

public class Globals {
	public static volatile boolean $quit;
	public static Byte[] accountID;

	public static final PerlMap config = new PerlMap();
	public static MessageTokenizer incomingMessages;
	public static final Network net = new DirectConnection();
}
