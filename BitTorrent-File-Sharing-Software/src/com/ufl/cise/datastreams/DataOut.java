package com.ufl.cise.datastreams;

import java.io.DataOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.cnt5106.SharedData;

public class DataOut implements Runnable {

	//Upload as DataOut
	
	private Socket socket;
	protected LinkedBlockingQueue<Integer> dataoutlengthqueue;
	protected LinkedBlockingQueue<byte[]> dataoutpayloadqueue;
	private boolean isAlive;
	private DataOutputStream outstream;

	//init client and server streams for data out from socket
	public DataOut(Socket socket, SharedData data) {
		init(socket, data);
	}
	public DataOut(Socket socket,String id, SharedData data) {
		init(socket, data);
	}

	private void init(Socket client, SharedData data) {
		dataoutpayloadqueue = new LinkedBlockingQueue<>();
		dataoutlengthqueue = new LinkedBlockingQueue<>();
		isAlive = true;
		this.socket = client;
		try {
			outstream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		
	
	@Override
	public void run() {
		while (isAlive) {
			try {
				int messageLength = dataoutlengthqueue.take();
				outstream.writeInt(messageLength);
				outstream.flush();
				byte[] payload = dataoutpayloadqueue.take();
				outstream.write(payload);
				outstream.flush();
			} catch (SocketException e) {
				isAlive = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void addMessagetoQueue(int len, byte[] payload) {
		try {
			dataoutlengthqueue.put(len);
			dataoutpayloadqueue.put(payload);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
