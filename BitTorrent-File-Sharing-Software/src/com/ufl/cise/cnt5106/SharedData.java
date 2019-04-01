package com.ufl.cise.cnt5106;

import java.util.BitSet;
import java.util.concurrent.LinkedBlockingQueue;


public class SharedData implements Runnable{
	
	private LinkedBlockingQueue<byte[]> payloadQueue;
	private boolean isAlive;
	private splitFile splitFile;
	private BitSet peerBitset;
	private Connection conn;
	private volatile boolean uploadHandshake;
	
	
	public SharedData(Connection connection) {
		conn = connection;
		payloadQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		splitFile = splitFile.getInstance();
		//broadcaster = BroadcastThread.getInstance();
		//peerBitset = new BitSet(CommonProperties.getNumberOfPieces());
	}
	
	public synchronized void sendHandshake() {
		setUploadHandshake();
	}
	
	public synchronized void setUploadHandshake() {
		uploadHandshake = true;
	}
	
	public void setUpload(Upload value) {
		// put message in the queue to send handshake (broadcaster queue)
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// take messaage from queue to process handshake
	}

	public void addPayload(byte[] payload) {
		// TODO Auto-generated method stub
		
	}

}
