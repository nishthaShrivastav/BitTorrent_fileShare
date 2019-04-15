package com.ufl.cise.cnt5106;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Download implements Runnable {

	Socket socket;
	private SharedData sharedData;
	private boolean isAlive;
	private DataInputStream in;

	public Download(Socket socket, SharedData data) {
		init(socket, data);
	}

	private void init(Socket socket, SharedData data) {
		this.socket = socket;
		sharedData = data;
		isAlive = true;
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		System.out.println("Download run started");

		while (isAlive()) {
			System.out.println("is alive");
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
		receiveRawData(payload);

	}

	private int receiveMessageLength() {
		int len = Integer.MIN_VALUE;
		byte[] messageLength = new byte[4];
		try {
			receiveRawData(messageLength);
			len = ByteBuffer.wrap(messageLength).getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return len;

	}
	private void receiveRawData(byte[] messageLength) {
		try {
			in.readFully(messageLength);
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
