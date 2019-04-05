package com.ufl.cise.cnt5106;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Download implements Runnable {
	
	Socket socket;
	private SharedData sharedData;
	private boolean isAlive;
	private DataInputStream in;
	
	
	public Download(Socket socket, SharedData data) {
		init(socket, data);
	}
	
	public Download(Socket socket, String id, SharedData data) {
		init(socket, data);
	}

	private void init(Socket socket, SharedData data) {
		// TODO Auto-generated method stub
		this.socket = socket;
		sharedData = data;
		isAlive = true;
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		recvmsg();
		
	}

	private void recvmsg() {
		// TODO Auto-generated method stub
		while (isAlive()) {
		
			int messageLength = Integer.MIN_VALUE;
			messageLength = receiveMessageLength();
			if (!isAlive()) {
				continue;
			}
			byte[] payload = new byte[messageLength];
			receiveMessagePayload(payload);
			sharedData.addPayload(payload);
			
		}
		
	}

	private void receiveMessagePayload(byte[] payload) {
		// TODO Auto-generated method stub
		
	}

	private int receiveMessageLength() {
		// TODO Auto-generated method stub
		
		return 0;
	}
	private synchronized boolean isAlive() {
		// TODO Auto-generated method stub
		return isAlive;
	}

}
