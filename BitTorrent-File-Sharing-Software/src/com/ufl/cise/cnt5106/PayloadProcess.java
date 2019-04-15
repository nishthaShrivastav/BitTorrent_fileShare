package com.ufl.cise.cnt5106;

import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.messages.*;

public class PayloadProcess extends Thread {

	private LinkedBlockingQueue<Object[]> queue;
	private MessageHandler messageHandler;
	private int piece_index;
	private Connection connection;
	private Message.MsgType msgType;
	private static PayloadProcess payloadProcess;


	private PayloadProcess() {
		queue = new LinkedBlockingQueue<>();
		messageHandler = MessageHandler.getInstance();
		msgType = null;
		connection = null;
		piece_index = Integer.MIN_VALUE;
	}
	public static synchronized PayloadProcess getInstance() {
		if (payloadProcess == null) {
			payloadProcess = new PayloadProcess();
			payloadProcess.start();
		}
		return payloadProcess;
	}

	protected synchronized void addMessage(Object[] input) {
		try {
			queue.put(input);
		} catch (InterruptedException e) {
			System.out.println("Exception in addMessage PayloadProcess"+e);
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("PayloadProcess run started");
			Object[] input = retrieveMessage();
			connection = (Connection) input[0];
			msgType = (Message.MsgType) input[1];
			piece_index = (int) input[2];
			/*	System.out.println(
				"Broadcaster: Building " + message + piece_index + " to peer " + connection.getRemotePeerId())*/
			int messageLength = messageHandler.getMessageLength(msgType, piece_index);
			byte[] payload = messageHandler.getPayload(msgType, piece_index);
			connection.sendMessage(messageLength, payload);
			System.out.println("PayloadProcess: Sending " + msgType + " to peer ") ;

		}
	}
	private Object[] retrieveMessage() {
		Object[] input = null;
		try {
			System.out.println("payloadprocess waiting on queue");
			input = queue.take();
			System.out.println("Payloadprocess popped from queue");
		} catch (InterruptedException e) {
			System.out.println("Exception in retreiveMessage PayloadProcess"+e);
		}
		return input;
	}
}
