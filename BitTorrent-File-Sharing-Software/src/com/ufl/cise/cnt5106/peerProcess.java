package com.ufl.cise.cnt5106;

import java.io.*;
import java.util.*;
import com.ufl.cise.conf.*;
import com.ufl.cise.logsconstants.Constants;
import com.ufl.cise.messages.Handshake;

public class peerProcess {
	private static int peerId;

	public static void main(String[] args) throws Exception {

		if(args.length!=1) {
			throw new Exception("The number of arguments passed is  "+args.length+" but the required length is 1");
		}

		peerId = Integer.parseInt(args[0]);
		Reader peerReader = null;
		PeerInfo peerInfo = new PeerInfo();
		Collection<RemotePeerInfo> peersToConnect= new LinkedList<>();
		
		try {
			new LoadProperties();
			peerReader = new FileReader(Constants.SOURCE_FILE_PATH+Constants.PEER_CONFIG_FILE_NAME);
			peerInfo.read(peerReader);
			Handshake.set_Id(args[0]);
			if((PeerInfo.getPeer(peerId)).hasFile()) {
				SplitFile splitFile=SplitFile.getInstance();
				System.out.println("split file returned");
				splitFile.split();
			}
		
			Peer peer = Peer.getInstance();
			System.out.println("Peer "+peerId+" starting connections");
			peer.sendConnections();
			peer.listenforConnections();
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
