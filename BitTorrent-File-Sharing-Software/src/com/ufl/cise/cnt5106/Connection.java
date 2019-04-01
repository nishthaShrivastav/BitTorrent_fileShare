package com.ufl.cise.cnt5106;

import java.net.Socket;



public class Connection {
	Upload upload;
	Download download;
	Socket peerSocket;
	SharedData sharedData;
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

	
	//methods wrt connection manager not written 
}
