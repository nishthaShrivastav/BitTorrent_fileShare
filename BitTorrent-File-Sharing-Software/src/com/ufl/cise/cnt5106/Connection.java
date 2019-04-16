package com.ufl.cise.cnt5106;

import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;

import com.ufl.cise.datastreams.DataIn;
import com.ufl.cise.datastreams.DataOut;
import com.ufl.cise.logsconstants.LoggerUtil;

public class Connection {
	
	DataOut dataOut;
	DataIn dataIn;
	Socket peerSocket;
	SharedData sharedData;
	double bytesDownloaded;
	String remotePeerId;
	boolean isPeerChoked;
	private PeerManager peerManager = PeerManager.getPeerManagerInstance();

	public Connection(Socket peerSocket) {

		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		dataOut = new DataOut(peerSocket, sharedData);
		dataIn= new DataIn(peerSocket,sharedData);
		setChannels(dataOut, dataIn);
		sharedData.setUploadHandshake(dataOut);
		sharedData.start();
	}
	
	public Connection(Socket peerSocket, String peerId) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		dataOut = new DataOut(peerSocket, peerId,sharedData);
		dataIn = new DataIn(peerSocket,peerId,sharedData);
		System.out.println("about to create up down threads, connection th "+Thread.currentThread().getName());
		setChannels(dataOut, dataIn);
		LoggerUtil.getLoggerInstance().logTcpConnectionTo(Peer.getInstance().getPeerInfo().getPeerId(), peerId);
		System.out.println("Sending handshake to "+peerId);
		sharedData.sendHandshake();
		sharedData.setUploadHandshake(dataOut);
		sharedData.start();
	}
	
	public double getBytesDownloaded() {
		return bytesDownloaded;
	}

	protected DataOut getUpload() {
		return dataOut;
	}

	public synchronized void addToBytesDownloaded(long value) {
		bytesDownloaded += value;
	}
	public boolean isPeerChoked() {
		return isPeerChoked;
	}
	
	private void setChannels(DataOut dataOut, DataIn download) {
		Thread dataOutThread = new Thread(dataOut);
		Thread dataInThread = new Thread(download);
		dataOutThread.start();
		dataInThread.start();
		
	}
	
	public void setPeerId(String val) {
		remotePeerId=val;
	}

	public synchronized void sendMessage(int messageLength, byte[] messageContent) {
		dataOut.addMessagetoQueue(messageLength, messageContent);
	}

	public void addAllConnections() {
		peerManager.addAllConnections(this);
		
	}

	public String getRemotePeerId() {
		return remotePeerId;
	}
	
	protected synchronized void addRequestedPiece(int pieceIndex) {
		SplitFile.getInstance().addRequestedPiece( pieceIndex,this);
	}

	public BitSet getPeerBitSet() {
		return sharedData.getPeerBitSet();
	}

	public void removeRequestedPiece(int pieceIndex) {
		SplitFile.getInstance().removeRequestedPiece(pieceIndex,this);
	}
	
	public void removeRequestedPieces(Connection connection) {
		SplitFile.getInstance().removeRequestedPieces(this);
	}

	public void addInterestedConnection() {
		peerManager.addInterestedConnection(remotePeerId, this);
		
	}

	public void addNotInterestedConnection() {
		peerManager.addNotInterestedConnection(remotePeerId, this);
	}

	public void sendHavetoAll(int pieceIndex) {
		peerManager.sendHavetoAll(pieceIndex);
		
	}
	public synchronized boolean hasFile() {
		return sharedData.hasFile();
	}
	public synchronized void setDownloadedbytes(int n) {
		bytesDownloaded = n;
	}

}
