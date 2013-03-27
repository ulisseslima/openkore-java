package com.dvlcube.openkore.src.network;

import static com.dvlcube.openkore.Openkore.DEBUG;
import static com.dvlcube.openkore.adaptations.Perl.bool;
import static com.dvlcube.openkore.adaptations.Perl.defined;
import static com.dvlcube.openkore.adaptations.Perl.join;
import static com.dvlcube.openkore.adaptations.Perl.length;
import static com.dvlcube.openkore.adaptations.Perl.uc;
import static com.dvlcube.openkore.adaptations.Perl.unpack;

import com.dvlcube.openkore.adaptations.Perl.$;
import com.dvlcube.openkore.adaptations.PerlList;
import com.dvlcube.openkore.adaptations.PerlMap;
import com.dvlcube.openkore.src.Globals;

/**
 * MODULE DESCRIPTION: Conversion of byte stream to descrete messages. <br>
 * <br>
 * As explained by the <a
 * href="http://wiki.openkore.com/index.php/Network_subsystem"> <br>
 * network subsystem overview</a>, the Ragnarok Online protocol uses TCP, which
 * means <br>
 * that all server messages are received as a byte stream. <br>
 * This class is specialized in extracting discrete RO server or client messages
 * from a byte <br>
 * stream.
 * 
 * @since 25/01/2013
 * @author Ulisses Lima
 */
public class MessageTokenizer {

	public enum Message {
		ACCOUNT_ID, KNOWN_MESSAGE, UNKNOWN_MESSAGE;
	}

	public PerlList<Byte> buffer = new PerlList<>();

	private volatile Boolean nextMessageMightBeAccountID;

	public PerlMap rpackets;

	/**
	 * Network::MessageTokenizer->new(Hash* rpackets) <br>
	 * rpackets: A reference to a hash containing the packet length database. <br>
	 * Required: defined($rpackets) <br>
	 * <br>
	 * Create a new Network::MessageTokenizer object.
	 * 
	 * @param rpackets
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public MessageTokenizer(PerlMap rpackets) {
		if (DEBUG) {
			assert defined(rpackets);
		}
		this.rpackets = rpackets;
	}

	/**
	 * void $Network_MessageTokenizer->add(Bytes data) <br>
	 * Requires: defined($data) <br>
	 * <br>
	 * Add raw data to this tokenizer's buffer.
	 * 
	 * @param data
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public void add(Byte[] data) {
		if (DEBUG) {
			assert defined(data);
		}
		buffer.addAll(data);
	}

	/**
	 * void $Network_MessageTokenizer->clear([int size]) <br>
	 * Requires: size >= 0 <br>
	 * <br>
	 * Clear the internal buffer. If $size is given, only the first $size <br>
	 * bytes are removed.
	 * 
	 * @param size
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public void clear(Integer size) {
		if (defined(size)) {
			buffer.remove(0, size);
		} else {
			buffer.clear();
		}
	}

	/**
	 * Bytes $Network->MessageTokenizer->getBuffer() <br>
	 * Ensures: defined(result) <br>
	 * <br>
	 * Get the internal buffer.
	 * 
	 * @return
	 * @since 26/01/2013
	 * @author Ulisses Lima
	 */
	public PerlList<Byte> getBuffer() {
		return buffer;
	}

	/**
	 * String Network::MessageTokenizer::getMessageID(Bytes message) <br>
	 * Requires: length($message) >= 2 <br>
	 * <br>
	 * Extract the message ID (also known as the "packet switch") from the given
	 * message.
	 * 
	 * @param message
	 * @return
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public String getMessageID(Byte[] message) {
		return uc(join("", unpack("@1H2 @0H2", message)));
	}

	public String getMessageID(PerlList<Byte> message) {
		return getMessageID(message.array());
	}

	/**
	 * void $Network_MessageTokenizer->nextMessageMightBeAccountID() <br>
	 * <br>
	 * Tell this tokenizer that the next message might be the account ID.
	 * 
	 * @since 25/01/2013
	 * @author Ulisses Lima
	 */
	public void nextMessageMightBeAccountID() {
		nextMessageMightBeAccountID = true;
	}

	/**
	 * Bytes $Network_MessageTokenizer->readNext(int* type) <br>
	 * <br>
	 * Read the next full message from the buffer, if there is one. <br>
	 * If not, undef will be returned. <br>
	 * <br>
	 * The message's type will be returned via the type parameter. <br>
	 * It will be one of: <br>
	 * `l <br>
	 * - KNOWN_MESSAGE - This is a known message, i.e. we know its length. <br>
	 * - UNKNOWN_MESSAGE - This is an unknown message, i.e. we don't know its
	 * length. <br>
	 * - ACCOUNT_ID - This is an account ID. <br>
	 * `l`
	 * 
	 * @param type
	 * @return
	 * @since 26/01/2013
	 * @author Ulisses Lima
	 */
	public PerlList<Byte> readNext(Message type) {
		if (length(buffer) < 2) {
			return null;
		}

		String $switch = getMessageID(buffer);
		int size = rpackets.get($switch).i();
		PerlList<Byte> result = null;

		boolean $nextMessageMightBeAccountID = bool(nextMessageMightBeAccountID);
		nextMessageMightBeAccountID = null;

		if ($nextMessageMightBeAccountID) {
			if (length(buffer) >= 4) {
				result = buffer.sub(0, 4);
				if (unpack("V1", result) == unpack("V1", Globals.accountID)) {
					buffer.remove(0, 4);
					type = Message.ACCOUNT_ID;
				} else {
					// Account ID is "hidden" in a packet (0283 is one of them)
					return readNext(type);
				}
			} else {
				nextMessageMightBeAccountID = $nextMessageMightBeAccountID;
			}
		} else if (size > 1) {
			// Static length message.
			if (length(buffer) >= size) {
				result = buffer.sub(0, size);
				buffer.remove(0, size);
				type = Message.KNOWN_MESSAGE;
			}
		} else if (defined(size) && size == 0 // old Kore convention
				|| size == -1 // packet extractor v3
		) {
			// Variable length message.
			if (length(buffer) >= 4) {
				size = $.i(unpack("v", buffer.sub(2, 2)));
				if (length(buffer) >= size) {
					result = buffer.remove(0, size);
					type = Message.KNOWN_MESSAGE;
				}
			}
		} else {
			result = buffer.values();
			buffer.clear();
			type = Message.UNKNOWN_MESSAGE;
		}
		return result;
	}
}
