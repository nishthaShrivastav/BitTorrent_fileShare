package com.ufl.cise.datastreams;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.ufl.cise.cnt5106.SharedData;

public class DataIn implements Runnable {
// Download as Data In 
	Socket insocket;
	private SharedData sharedData;
	private boolean isAlive;
	private DataInputStream instream;

	//init server client input streams
	public DataIn(Socket socket, SharedData data) {
		init(socket, data);
	}
	public DataIn(Socket socket, String peerId,SharedData data) {
		init(socket, data);
	}
	

	private void init(Socket socket, SharedData data) {
		this.insocket = socket;
		sharedData = data;
		isAlive = true;
		try {
			instream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		System.out.println("DataIn run started");

		while (isAlive()) {
			System.out.println("is alive");
			int messageLength = Integer.MIN_VALUE;
			messageLength = findMessageLength();
			if (!isAlive()) {
				continue;
			}
			byte[] payload = new byte[messageLength];
			takeinMessage(payload);
			System.out.println("Received message and adding payload to shareddata" +insocket.getPort());
			sharedData.addPayload(payload);

		}

	}

	private void takeinMessage(byte[] payload) {
		dataIn(payload);

	}

	private int findMessageLength() {
		int len = Integer.MIN_VALUE;
		byte[] messageLength = new byte[4];
		try {
			dataIn(messageLength);
			len = ByteBuffer.wrap(messageLength).getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return len;

	}
	private void dataIn(byte[] messageLength) {
		try {
			instream.readFully(messageLength);
		} catch (EOFException e) {
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized boolean isAlive() {
		return isAlive;
	}

}
