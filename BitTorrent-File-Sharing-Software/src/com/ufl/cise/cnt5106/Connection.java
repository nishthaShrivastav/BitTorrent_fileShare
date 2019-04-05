package com.ufl.cise.cnt5106;

import java.net.Socket;
import java.util.BitSet;

public class Connection {
	Upload upload;
	Download download;
	Socket peerSocket;
	SharedData sharedData;
	double bytesDownloaded;
	String remotePeerId;
	boolean IsPeerChoked;
	private PeerManager peerManager = PeerManager.getPeerManager();

	// download yet to be written 
	
	public Connection(Socket peerSocket) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, sharedData);
		download= new Download(peerSocket,sharedData);
		createThreads(upload, download);
		sharedData.setUpload(upload);
		sharedData.run();
	}

	private void createThreads(Upload upload, Download download) {
		// TODO Auto-generated method stub
		Thread uploadThread = new Thread(upload);
		Thread downloadThread = new Thread(download);
		uploadThread.start();
		downloadThread.start();
		
	}

	public Connection(Socket peerSocket, String peerId) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, peerId, sharedData);
		download= new Download(peerSocket,sharedData);
		createThreads(upload, download);
		//create Log
		sharedData.sendHandshake();
		sharedData.setUpload(upload);
		sharedData.run();
	}

	public void setPeerId(String remotePeerId) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void sendMessage(int msgLen, byte[] payload) {
		upload.addMessage(msgLen, payload);
	}

	public void addAllConnections() {
		// TODO Auto-generated method stub
		
	}

	public String getRemotePeerId() {
		// TODO Auto-generated method stub
		return remotePeerId;
	}

	public BitSet getPeerBitSet() {
		// TODO Auto-generated method stub
		return null;
	}

	
	//methods wrt connection manager not written 
}
