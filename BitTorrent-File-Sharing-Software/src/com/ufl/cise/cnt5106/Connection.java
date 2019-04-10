package com.ufl.cise.cnt5106;

import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;

public class Connection {
	Upload upload;
	Download download;
	Socket peerSocket;
	SharedData sharedData;
	double bytesDownloaded;
	String remotePeerId;
	boolean isPeerChoked;
	private PeerManager peerManager = PeerManager.getPeerManager();

	// download yet to be written 
	public double getBytesDownloaded() {
		return bytesDownloaded;
	}

	protected Upload getUpload() {
		return upload;
	}

	public synchronized void addToBytesDownloaded(long value) {
		bytesDownloaded += value;
	}
	public boolean isPeerChoked() {
		return isPeerChoked;
	}
	
	public Connection(Socket peerSocket) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, sharedData);
		download= new Download(peerSocket,sharedData);
		createThreads(upload, download);
		sharedData.setUpload(upload);
		sharedData.run();
	}
	
	public Connection(Socket peerSocket, String peerId) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, peerId, sharedData);
		download = new Download(peerSocket, peerId, sharedData);
		createThreads(upload, download);
		//LoggerUtil.getInstance().logTcpConnectionTo(Peer.getInstance().getNetwork().getPeerId(), peerId);
		sharedData.sendHandshake();
		sharedData.setUpload(upload);
		sharedData.run();
	}
	private void createThreads(Upload upload, Download download) {
		Thread uploadThread = new Thread(upload);
		Thread downloadThread = new Thread(download);
		uploadThread.start();
		downloadThread.start();
		
	}
	
	
	public void setPeerId(String val) {
		remotePeerId=val;
	}

	public synchronized void sendMessage(int msgLen, byte[] payload) {
		upload.addMessage(msgLen, payload);
	}

	public void addAllConnections() {
		peerManager.addAllConnections(this);
		
	}

	public String getRemotePeerId() {
		return remotePeerId;
	}

	public void close() {
		try {
			peerSocket.close();
		} catch (IOException e) {
			System.out.println("Exception in Connection close"+e);
		}
	}

	
	protected synchronized void addRequestedPiece(int pieceIndex) {
		splitFile.getInstance().addRequestedPiece(this, pieceIndex);
	}

	public BitSet getPeerBitSet() {
		return sharedData.getPeerBitSet();
	}

	public void removeRequestedPiece() {
		splitFile.getInstance().removeRequestedPiece(this);
	}

	public void addInterestedConnection() {
		peerManager.addInterestedConnection(remotePeerId, this);
		
	}

	public void addNotInterestedConnection() {
		peerManager.addNotInterestedConnection(remotePeerId, this);
	}

	public void tellAllNeighbors(int pieceIndex) {
		peerManager.tellAllNeighbors(pieceIndex);
		
	}
	public synchronized boolean hasFile() {
		return sharedData.hasFile();
	}
	public synchronized void setDownloadedbytes(int n) {
		bytesDownloaded = n;
	}


}
