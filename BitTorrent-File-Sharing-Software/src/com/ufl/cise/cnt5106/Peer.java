package com.ufl.cise.cnt5106;

import java.io.IOException;
import java.net.*;
import java.util.*;
import com.ufl.cise.conf.*;
/*
 * Peer consists of RemotePeerInfo , peerManager
 * */


public class Peer {
	private static Peer client = new Peer();
	private RemotePeerInfo myPeerInfo;
	private PeerManager peerManager; 
	public static boolean allPeersReceivedFiles = false;
	
	private Peer() {
		myPeerInfo = PeerInfoProperties.getPeer(peerProcess.getPeerId());
		peerManager = PeerManager.getPeerManager();
		
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
	
	public void connectToPeers() throws IOException {

		ServerSocket socket = null;
		try {
			socket = new ServerSocket(myPeerInfo.getPeerPort());
			//continue while all peers are yet to receive the file
			while (false == allPeersReceivedFiles) {
				Socket clientSocket = socket.accept();
				peerManager.createConnection(clientSocket);
			}
		} catch (Exception e) {
			System.out.println("Exception in connnectToPeers"+e);
		} finally {
			socket.close();
		}
	}

	public void TCPConnections() {
		Collection<RemotePeerInfo> peers=PeerInfoProperties.getPeerInfo();
		for(RemotePeerInfo pi : peers) {
			if(pi.getId()<myPeerInfo.getId()) {
				new Thread() {
					@Override
					public void run() {
						createPeerConnection(pi);
					}
				}.start();
		}
		
	}
	
}

	private void createPeerConnection(RemotePeerInfo pi) {
		
		int peerPort = pi.getPeerPort();
		String peerHost= pi.getHostName();
		try {
			Socket socket = new Socket(peerHost, peerPort);
			peerManager.createConnection(socket);
			Thread.sleep(300);
		}
		catch(Exception e) {
			System.out.println("Exception in createPeerConnection"+e);
		}
	}

	
	

}
