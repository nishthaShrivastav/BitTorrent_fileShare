package com.ufl.cise.cnt5106;

import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.messages.*;

public class PayloadProcess extends Thread {

	private LinkedBlockingQueue<Object[]> messageQueue;
	private MessageHandler messageHandler;
	private int pieceIndex;
	private Connection connection;
	private Message.MsgType msgType;
	private static PayloadProcess payloadProcess;


	private PayloadProcess() {
		messageQueue = new LinkedBlockingQueue<>();
		messageHandler = MessageHandler.getInstance();
		msgType = null;
		connection = null;
		pieceIndex = Integer.MIN_VALUE;
	}
	public static synchronized PayloadProcess getInstance() {
		if (payloadProcess == null) {
			payloadProcess = new PayloadProcess();
			payloadProcess.start();
		}
		return payloadProcess;
	}

	protected synchronized void addMessagetoQueue(Object[] input) {
		try {
			messageQueue.put(input);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("PayloadProcess run started");
			Object[] inputMessage = getMessage();
			connection = (Connection) inputMessage[0];
			msgType = (Message.MsgType) inputMessage[1];
			pieceIndex = (int) inputMessage[2];
			int messageLength = messageHandler.getMessageLength(msgType, pieceIndex);
			byte[] payload = messageHandler.getMessageContent(msgType, pieceIndex);
			connection.sendMessage(messageLength, payload);
			System.out.println("PayloadProcess: Sending " + msgType + " to peer ") ;

		}
	}
	private Object[] getMessage() {
		Object[] inputMessage = null;
		try {
			System.out.println("payloadprocess waiting on queue");
			inputMessage = messageQueue.take();
			System.out.println("Payloadprocess popped from queue");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return inputMessage;
	}
}
