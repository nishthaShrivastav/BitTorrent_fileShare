package com.ufl.cise.cnt5106;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;


// This program has basic handshake functions 
public class Handshake {
	private static String message = "";
	private static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ0000000000";
	

	public static synchronized String get_Remote_PId(byte[] a) {
		String id = "";
		
		int to = a.length;
		int from = to - 4;
		byte[] bytes = Arrays.copyOfRange(a, from, to);
		String string = new String(bytes, StandardCharsets.UTF_8);
		return string;
		
		
		
	}

/*
 * Functions to get Id,set Id, get message 
 * and verify the message have been written
 * below
 */
	public static synchronized byte[] message_get() {
		//byte[] hand_shake = new byte[32];
		return (message.getBytes());
		//Bb.get(hand_shake);
		//return hand_shake;
	}
	private static synchronized void init(String id) {
		message += HANDSHAKE_HEADER + id;
	}


	public static synchronized void set_Id(String id) {
		init(id);
	}
	public static synchronized String get_Id(byte[] message) {
		byte[] remote_PeerId = Arrays.copyOfRange(message, message.length - 4, message.length);
		return new String(remote_PeerId);
	}

	public static synchronized boolean verification(byte[] message, String peerId) {
		String receivedMsg = new String(message);
		return receivedMsg.indexOf(peerId) != -1 &&  receivedMsg.contains(HANDSHAKE_HEADER);
	}

	
}
