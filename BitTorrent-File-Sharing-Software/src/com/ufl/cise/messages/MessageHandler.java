package com.ufl.cise.messages;

import java.nio.ByteBuffer;

import com.ufl.cise.cnt5106.Bitfield;
import com.ufl.cise.cnt5106.Handshake;
import com.ufl.cise.cnt5106.splitFile;
import com.ufl.cise.messages.Message.MsgType;

/*
 * class to handle types of messages 
 * 
 * cases to understand the messages and get the actual payload. 
 * 
 */

public class MessageHandler {
	public static MessageHandler messageHandler;
	private splitFile splitFie;
	
	
	private MessageHandler() {
		splitFile.getInstance();
	}
	public static synchronized MessageHandler getInstance() {
		if(messageHandler==null) {
			messageHandler=new MessageHandler();
		}

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
		case REQUEST:
		case HAVE:
			return 5;
		case BITFIELD:
			Bitfield bitfield = Bitfield.getInstance();
			return bitfield.getMessageLength();
		case HANDSHAKE:
			return 32;
		case PIECE:
			if(splitFile.getPiece(pieceIndex) !=null) {
				int payloadLength = 5 + splitFile.getPiece(pieceIndex).length;
				return payloadLength;
			}
		}
		return -1;
	}
	
	public synchronized byte[] getPayload(MsgType messageType, int pieceIndex) {
		byte[] payload = new byte[5];
		switch(messageType) {
		case CHOKE:
			return new byte[] { 0 };
		case UNCHOKE:
			return new byte[] { 1 };
		case INTERESTED:
			return new byte[] { 2 };
		case NOTINTERESTED:
			return new byte[] { 3 };
		case HAVE:
			payload[0] = 4;
			byte[] pieceInd = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(pieceInd, 0, payload, 1, 4);
			break;
		case BITFIELD:
			Bitfield bitfield = Bitfield.getInstance();
			payload = bitfield.getPayload();
			break;
		case REQUEST:
			payload[0] = 6;
			byte[] index = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(index, 0, payload, 1, 4);
			break;
		case HANDSHAKE:
			return Handshake.message_get();
		case PIECE:
			byte[] piece = splitFile.getPiece(pieceIndex);
			int pieceSize = piece.length;
			int totalLength = 5 + pieceSize;
			payload = new byte[totalLength];
			payload[0] = 7;
			byte[] data = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(data, 0, payload, 1, 4);
			System.arraycopy(piece, 0, payload, 5, pieceSize);
			break;
		}
		return payload;
		
	}
}