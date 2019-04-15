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
		System.out.println("This connection");
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, sharedData);
		download= new Download(peerSocket,sharedData);
		createThreads(upload, download);
		sharedData.setUpload(upload);
		sharedData.start();
	}
	
	public Connection(Socket peerSocket, String peerId) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, peerId,sharedData);
		download = new Download(peerSocket,peerId,sharedData);
		System.out.println("about to create up down threads, connection th "+Thread.currentThread().getName());
		createThreads(upload, download);
		System.out.println("back to connection");
		LoggerUtil.getInstance().logTcpConnectionTo(Peer.getInstance().getPeerInfo().getPeerId(), peerId);
		System.out.println("Sending handshake to "+peerId);
		sharedData.sendHandshake();
		sharedData.setUpload(upload);
		sharedData.start();
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
		splitFile.getInstance().addRequestedPiece( pieceIndex,this);
	}

	public BitSet getPeerBitSet() {
		return sharedData.getPeerBitSet();
	}

	public void removeRequestedPiece(int pieceIndex) {
		splitFile.getInstance().removeRequestedPiece(pieceIndex,this);
	}
	
	public void removeRequestedPieces(Connection connection) {
		splitFile.getInstance().removeRequestedPieces(this);
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
