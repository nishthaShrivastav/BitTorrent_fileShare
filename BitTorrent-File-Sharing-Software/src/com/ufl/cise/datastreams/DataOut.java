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
			System.out.println("Created output data stream for "+client.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		
	
	@Override
	public void run() {
		while (isAlive) {
			System.out.println("DataOut run started");
			try {
				int messageLength = dataoutlengthqueue.take();
				outstream.writeInt(messageLength);
				outstream.flush();
				byte[] payload = dataoutpayloadqueue.take();
				outstream.write(payload);
				outstream.flush();
				System.out.println("Written to socket out stream of "+socket.getPort());
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
			System.out.println("payload[0] sending form upload thread"+payload[0]);
			dataoutpayloadqueue.put(payload);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
