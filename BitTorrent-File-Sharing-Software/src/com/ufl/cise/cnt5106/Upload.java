package com.ufl.cise.cnt5106;

import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class Upload implements Runnable {

	
	
	private Socket socket;
	protected LinkedBlockingQueue<Integer> uploadLengthQueue;
	protected LinkedBlockingQueue<byte[]> uploadPayloadQueue;
	private boolean isAlive;
	private DataOutputStream out;



	public Upload(Socket socket, SharedData data) {
		init(socket, data);
	}
	
	public Upload(Socket socket, String id, SharedData data) {
		init(socket, data);
	}
	private void init(Socket client, SharedData data) {
		uploadPayloadQueue = new LinkedBlockingQueue<>();
		uploadLengthQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		this.socket = socket;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			System.out.println("Created output data stream for "+client.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		
	
	@Override
	public void run() {
		while (isAlive) {
			System.out.println("Upoad run started");
			try {
				int messageLength = uploadLengthQueue.take();
				out.writeInt(messageLength);
				out.flush();
				byte[] payload = uploadPayloadQueue.take();
				out.write(payload);
				out.flush();
			} catch (SocketException e) {
				isAlive = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}// end run 
	
	public void addMessage(int length, byte[] payload) {
		try {
			uploadLengthQueue.put(length);
			uploadPayloadQueue.put(payload);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
