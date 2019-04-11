package com.ufl.cise.cnt5106;
import java.io.*;
import java.util.*;

import com.ufl.cise.conf.*;

/*
 * main function to start program
 * 
 * peer id has argument 
 * read common config file
 * read peer config file
 * 
 * identify peers to communicate with and 
 * create process to communicate  
 * 
 */
public class peerProcess {
	private static int peerId;

	public static void main(String[] args) throws Exception {

		if(args.length!=1) {
			throw new Exception("The number of arguments passed is  "+args.length+" but the required length is 1");
		}

		peerId = Integer.parseInt(args[0]);
		Reader peerReader = null;
		PeerInfoProperties peerInfo = new PeerInfoProperties();
		Collection<RemotePeerInfo> peersToConnect= new LinkedList<>();
		
		try {
			new LoadProperties();
			peerReader = new FileReader(Constants.SOURCE_FILE_PATH+Constants.PEER_CONFIG_FILE_NAME);
			peerInfo.read(peerReader);
			Handshake.set_Id(args[0]);
			if((PeerInfoProperties.getPeer(peerId)).hasFile()) {
				splitFile sp=splitFile.getInstance();
				sp.split();
			}
			Peer peer = Peer.getInstance();
			System.out.println("Peer "+peerId+" starting connections");
			peer.TCPConnections();
			peer.connectToPeers();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally {
			try {
				peerReader.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

		}
	}
	public static int getPeerId() {
		return peerId;
	}

}
