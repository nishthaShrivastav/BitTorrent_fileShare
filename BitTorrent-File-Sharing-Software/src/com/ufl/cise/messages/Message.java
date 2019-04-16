package com.ufl.cise.messages;

import java.nio.ByteBuffer;

public abstract class Message {
	
	protected ByteBuffer bytebuffer;
	protected byte msgNum;
	protected byte[] content;
	protected byte[] messagelength = new byte[4];
	protected byte[] messageContent;

	public static enum MsgType {
		CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
	}

	protected abstract byte[] getMessageContent();

	protected abstract int getMessageLength(); 
	
}
