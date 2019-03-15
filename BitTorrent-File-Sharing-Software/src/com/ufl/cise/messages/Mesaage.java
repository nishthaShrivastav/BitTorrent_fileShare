package com.ufl.cise.messages;

import java.nio.ByteBuffer;

/*
 * class to identify message types
 */
public class Mesaage {
	
	protected ByteBuffer bytebuffer;
	protected byte msgType;
	protected byte[] content;
	protected byte[] length = new byte[4];
	protected byte[] payload;

	public static enum MsgType {
		CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
	}
}
