package com.ufl.cise.cnt5106;

import java.net.Socket;



public class Connection {
	Upload upload;
	Socket peerSocket;
	SharedData sharedData;
	// download yet to be written 
	
	public Connection(Socket peerSocket) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, sharedData);
		
		
		sharedData.setUpload(upload);
		sharedData.run();
	}

	public Connection(Socket peerSocket, String peerId) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, peerId, sharedData);
		
		
		
		sharedData.sendHandshake();
		sharedData.setUpload(upload);
		sharedData.run();
	}

}
