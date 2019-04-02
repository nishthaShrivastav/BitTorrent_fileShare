package com.ufl.cise.cnt5106;

import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.messages.MessageHandler;
import com.ufl.cise.messages.Mesaage;




public class BroadcastThread extends Thread{
	private LinkedBlockingQueue<Object[]> queue;
	private MessageHandler messageHandler;
	private int piece_index;
	private static BroadcastThread broadcaster;
	private Connection Conn;
	private Mesaage mesaage;
	
	


private BroadcastThread() {
	queue = new LinkedBlockingQueue<>();
	messageHandler = MessageHandler.getInstance();
	mesaage = null;
	Conn = null;
	piece_index = Integer.MIN_VALUE;
}
protected static synchronized BroadcastThread getInstance() {
	if (broadcaster == null) {
		broadcaster = new BroadcastThread();
		broadcaster.start();
	}
	return broadcaster;
}

protected synchronized void addMessage(Object[] data) {
	try {
		queue.put(data);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

@Override
public void run() {
	while (true) {
		Object[] data = retrieveMessage();
		Conn = (Connection) data[0];
		mesaage = (Mesaage) data[1];
		piece_index = (int) data[2];
	/*	System.out.println(
				"Broadcaster: Building " + mesaage + piece_index + " to peer " + Conn.getRemotePeerId());
		int messageLength = messageHandler.getMessageLength(mesaage, piece_index);
		byte[] payload = messageHandler.getMessagePayload(mesaage, piece_index);
		Conn.sendMessage(messageLength, payload);
		System.out.println("Broadcaster: Sending " + mesaage + " to peer " + Conn.getRemotePeerId());
*/
	}
}
private Object[] retrieveMessage() {
	Object[] data = null;
	try {
		data = queue.take();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return data;
}
}
