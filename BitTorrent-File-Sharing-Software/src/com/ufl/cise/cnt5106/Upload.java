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
		// TODO Auto-generated method stub
		uploadPayloadQueue = new LinkedBlockingQueue<>();
		uploadLengthQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		this.socket = socket;
		// sharedData = data;
		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isAlive) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
