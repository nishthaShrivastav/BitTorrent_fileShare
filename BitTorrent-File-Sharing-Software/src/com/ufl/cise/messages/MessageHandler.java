package com.ufl.cise.messages;

import java.nio.ByteBuffer;

import com.ufl.cise.cnt5106.Handshake;
import com.ufl.cise.messages.Mesaage.MsgType;

/*
 * class to handle types of messages 
 * 
 * cases to understand the messages and get the actual payload. 
 * 
 */

public class MessageHandler {
	public static MessageHandler messageHandler;

	public static synchronized MessageHandler getInstance() {

		return messageHandler;

	}
	public synchronized MsgType getType(byte msgType) {
		switch (msgType) {
		case 0:
			return MsgType.CHOKE;
		case 1:
			return MsgType.UNCHOKE;
		case 2:
			return MsgType.INTERESTED;
		case 3:
			return MsgType.NOTINTERESTED;
		case 4:
			return MsgType.HAVE;
		case 5:
			return MsgType.BITFIELD;
		case 6:
			return MsgType.REQUEST;
		case 7:
			return MsgType.PIECE;
		}
		return null;
	}

	public synchronized int getMessageLength(MsgType messageType, int pieceIndex) {
		switch (messageType) {
		case CHOKE:
		case UNCHOKE:
		case INTERESTED:
		case NOTINTERESTED:
			return 1;
		
		case HAVE:
			return 5;
		case BITFIELD:
			// write bitfield case
			return 0;
		case REQUEST:
			return 5;
		case HANDSHAKE:
			return 32;
		case PIECE:
			//write piece case
			return 0;

		}
		return -1;
	}
	
	public synchronized byte[] getPayload(MsgType messageType, int pieceIndex) {
		byte[] payload = new byte[5];
		byte[] nopayload = new byte[1];
		switch(messageType) {
		case CHOKE:
		case UNCHOKE:
		case INTERESTED:
		case NOTINTERESTED:
			return nopayload;
		case HAVE:
			payload[0] = 4;
			byte[] pieceInd = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(pieceInd, 0, payload, 1, 4);
			break;
		case BITFIELD:
			//write payload case
		case REQUEST:
			payload[0] = 6;
			byte[] index = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(index, 0, payload, 1, 4);
			break;
		case HANDSHAKE:
			return Handshake.message_get();
		case PIECE:
			//get piece from file and return array 
			return payload;

		}
		return payload;
		
	}
}