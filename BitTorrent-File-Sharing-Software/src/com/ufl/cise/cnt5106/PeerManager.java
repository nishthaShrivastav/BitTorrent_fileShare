package com.ufl.cise.cnt5106;
/*Get interested peers and provides all info about the peers choked and unchoked
 * Manages connections of peer with other peers
 * TO DO: add choking interval functions  
 * 
 */

import java.net.Socket;
import java.util.*;
import com.ufl.cise.conf.*;

public class PeerManager{
	
	private static PeerManager peerManager;
	private HashSet<Connection> connections;
	private HashSet<Connection> uninterested;
	private PriorityQueue<Connection> prefNeighbors;
	public HashSet<String> peersWithFullFile = new HashSet<String>();
	private int num_pref = Common.getNumberOfPreferredNeighbors();
	private int opt_unchoking = Common.getOptimisticUnchokingInterval();
	private int unchoking = Common.getUnchokingInterval();
	
	private int num_peers= PeerInfoProperties.numberOfPeers();
	private splitFile file;
//	private BroadcastThread broadcaster;

	
	private PeerManager() {

		uninterested = new HashSet<>();
//		prefNeighbors = new PriorityQueue<>(num_pref + 1,
//				(a, b) -> (int) a.getBytesDownloaded() - (int) b.getBytesDownloaded());
//		broadcaster = BroadcastThread.getInstance();
//		sharedFile = SharedFile.getInstance();
//		allConnections = new HashSet<>();
//		monitor();
	
	}
	
	public static PeerManager getPeerManager() {

		if (peerManager == null) {
			peerManager = new PeerManager();
		}
		return peerManager;
	
		
	}
	public void createConnection(Socket peerSocket) {
		
		// TODO Auto-generated method stub
		
	}
	
	public void addToFullFileList(String pid) {
		peersWithFullFile.add(pid);
	}

	
	
}
