package com.ufl.cise.messages;

import java.nio.ByteBuffer;

/*
 * class to identify message types
 */
public abstract class Message {
	
	protected ByteBuffer bytebuffer;
	protected byte msgNum;
	protected byte[] content;
	protected byte[] length = new byte[4];
	protected byte[] payload;

	public static enum MsgType {
		CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
	}

	protected abstract byte[] getPayload();

	protected abstract int getMessageLength(); 
	
}
