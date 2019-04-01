package com.ufl.cise.cnt5106;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * write run method and server socket upload methods
 */

public class Upload implements Runnable {

	
	
	private Socket socket;
	protected LinkedBlockingQueue<Integer> uploadLengthQueue;
	protected LinkedBlockingQueue<byte[]> uploadPayloadQueue;
	private boolean isAlive;
	private DataOutputStream out;



	public Upload(Socket socket, SharedData data) {
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
		
	}

}
