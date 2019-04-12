package com.ufl.cise.cnt5106;
/*Get interested peers and provides all info about the peers choked and unchoked
 * Manages connections of peer with other peers
 * TO DO: add choking interval functions  
 * 
 */

import java.net.Socket;

import java.util.*;
import com.ufl.cise.conf.*;
import com.ufl.cise.messages.*;


public class PeerManager{
	
	private static PeerManager peerManager;
	private HashSet<Connection> connections;
	private HashSet<Connection> uninterested;
	private PriorityQueue<Connection> prefNeighbors;
	public HashSet<String> peersWithFullFile = new HashSet<String>();
	private int num_pref = Common.getNumberOfPreferredNeighbors();
	private int opt_unchoking = Common.getOptimisticUnchokingInterval();
	private int unchoking = Common.getUnchokingInterval();
	private HashSet<Connection> allConnections;
	private int num_peers= PeerInfoProperties.numberOfPeers();
	private splitFile splitFile;
	private PayloadProcess payloadProcess;
	
	private PeerManager() {

		uninterested = new HashSet<>();
		prefNeighbors = new PriorityQueue<>(num_pref + 1,
				(a, b) -> (int) a.getBytesDownloaded() - (int) b.getBytesDownloaded());
		payloadProcess = PayloadProcess.getInstance();
		splitFile =splitFile.getInstance();
		allConnections = new HashSet<>();
		//monitor();
	
	}
	
	private void monitor() {
		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (peersWithFullFile.size() == num_peers - 1 && splitFile.isCompleteFile()) {
					System.out.println("All peers have the file, stop program");
					System.exit(0);
				}
				if (prefNeighbors.size() > 1) {
					Connection conn = prefNeighbors.poll();
					conn.setDownloadedbytes(0);
					for (Connection connT : prefNeighbors) {
						connT.setDownloadedbytes(0);
					}
					payloadProcess.addMessage(new Object[] { conn, Message.MsgType.CHOKE, Integer.MIN_VALUE });
					LoggerUtil.getInstance().logChangePreferredNeighbors(getTime(), peerProcess.getPeerId(),prefNeighbors);
					 System.out.println("Choking:" + conn.getRemotePeerId());
				}
			}
			
		}, new Date(), unchoking * 1000);
		
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (Connection conn : allConnections) {
					if (!uninterested.contains(conn) && !prefNeighbors.contains(conn) && !conn.hasFile()) {
						payloadProcess.addMessage(new Object[] { conn, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
						prefNeighbors.add(conn);
						//LoggerUtil.getInstance().logOptimisticallyUnchokeNeighbor(getTime(), peerProcessMain.getId(),	conn.getRemotePeerId());
					}
				}
			}
		}, new Date(), opt_unchoking * 1000);
		
	}

	public static PeerManager getPeerManager() {

		if (peerManager == null) {
			peerManager = new PeerManager();
		}
		return peerManager;
	
		
	}
	protected synchronized void createConnection(Socket socket, String peerId) {
		new Connection(socket, peerId);
	}

	protected synchronized void createConnection(Socket socket) {
		new Connection(socket);
	}
	
	protected synchronized void tellAllNeighbors(int pieceIndex) {
		for (Connection conn : allConnections) {
			payloadProcess.addMessage(new Object[] { conn, Message.MsgType.HAVE, pieceIndex });
		}
	}
	public void addToFullFileList(String pid) {
		peersWithFullFile.add(pid);
	}
	
	public String getTime() {
		return Calendar.getInstance().getTime() + ": ";
	}

	public synchronized void addAllConnections(Connection connection) {
		allConnections.add(connection);
	}

	public synchronized void addNotInterestedConnection(String peerId, Connection connection) {
		uninterested.add(connection);
		prefNeighbors.remove(connection);
	}

	public synchronized void addInterestedConnection(String peerId, Connection connection) {
		if (prefNeighbors.size() <= num_pref && !prefNeighbors.contains(connection)) {
			connection.setDownloadedbytes(0);
			prefNeighbors.add(connection);
			payloadProcess.addMessage(new Object[] { connection, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
		}
		uninterested.remove(connection);
	}

	
	
}
