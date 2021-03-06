package com.ufl.cise.cnt5106;

import java.io.IOException;
import java.net.*;
import java.util.*;

import com.ufl.cise.conf.*;

public class Peer {
	
	private static Peer client = new Peer();
	private RemotePeerInfo myPeerInfo;
	private PeerManager peerManager; 
	public static boolean allPeersReceivedFiles = false;
	public int peerID;
	
	private Peer() {
		myPeerInfo = PeerInfo.getPeer(peerProcess.getPeerId());
		peerManager = PeerManager.getPeerManagerInstance();
		peerID=peerProcess.getPeerId();
	}
	
	public static Peer getInstance() {
		return client;
	}
	
	public RemotePeerInfo getPeerInfo() {
		return myPeerInfo;
	}

	public void setPeerInfo(RemotePeerInfo peerInfo) {
		this.myPeerInfo = peerInfo;
	}
	
	public int getPeerID() {
		return peerID;
	}
	
	public void listenforConnections() throws IOException {

		ServerSocket socket = null;
		try {
			socket = new ServerSocket(myPeerInfo.getPeerPort());
			
			//continue while all peers are yet to receive the file
			while (false == allPeersReceivedFiles) {
				Socket clientSocket = socket.accept();
				peerManager.createConnection(clientSocket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	public void sendConnections() {
		Collection<RemotePeerInfo> peers=PeerInfo.getPeerInfo();
		for(RemotePeerInfo pi : peers) {
			if(pi.getId()<myPeerInfo.getId()) {
				new Thread() {
					@Override
					public void run() {
						sendConnectionRequest(pi);
					}
				}.start();
		}
		
	}
	
}

	public void sendConnectionRequest(RemotePeerInfo pi) {
		int peerPort = pi.getPeerPort();
		String peerHost= pi.getHostName();
		try {
			Socket socket = new Socket(peerHost, peerPort);
			peerManager.createConnection(socket,pi.getPeerId());
			Thread.sleep(300);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
