package com.ufl.cise.messages;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


// This program has basic handshake functions 
public class Handshake {
	
	private static String message = "";
	private static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ0000000000";

	public static synchronized byte[] message_get() {
		
		byte[] handshake = new byte[32];
		ByteBuffer Bb = ByteBuffer.wrap(message.getBytes());
		
		Bb.get(handshake);
		return handshake;
	}
	
	private static synchronized void initHandshake(String id) {
		message += HANDSHAKE_HEADER + id;
	}


	public static synchronized void set_Id(String id) {
		initHandshake(id);
	}
	
	public static synchronized String get_Id(byte[] message) {
		byte[] remote_PeerId = Arrays.copyOfRange(message, message.length - 4, message.length);
		return new String(remote_PeerId);
	}
	
}
