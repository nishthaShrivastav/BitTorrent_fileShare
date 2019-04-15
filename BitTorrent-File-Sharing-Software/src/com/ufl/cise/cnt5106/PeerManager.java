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
import org.apache.commons.collections4.*;

public class PeerManager implements Runnable{
	
	class OptimisticUnchoker extends Thread {
		
		 @Override
	        public void run() {
             try {
                 Thread.sleep(optUnchokingInterval);
             } catch (InterruptedException ex) {
             }

				Collections.shuffle(allConnections);
				for (Connection conn : allConnections) {
					if (interested.contains(conn) && !prefNeighbors.contains(conn) && !conn.hasFile()) {
						payloadProcess.addMessage(new Object[] { conn, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
						prefNeighbors.add(conn);
						System.out.println("Optimistic unchoke peer "+conn.remotePeerId);
						LoggerUtil.getInstance().logOptimisticallyUnchokeNeighbor(getTime(), peerProcess.getPeerId(),conn.getRemotePeerId());
					}
				}
			
             
		 }
	}
	private int optUnchokingInterval = Common.getOptimisticUnchokingInterval();
	private static PeerManager peerManager;
	private HashSet<Connection> uninterested;
	private HashSet<Connection> interested;
	private PriorityQueue<Connection> prefNeighbors;
	public HashSet<String> peersWithFullFile = new HashSet<String>();
	private int numPrefNeighbors = Common.getNumberOfPreferredNeighbors();
	private OptimisticUnchoker optUnchoker;
	private int unchokingInterval = Common.getUnchokingInterval();
	private List<Connection> allConnections;
	private splitFile splitFile;
	private PayloadProcess payloadProcess;
	PeerInfoProperties peerInfo = new PeerInfoProperties();

	private PeerManager() {

		uninterested = new HashSet<>();
		prefNeighbors = new PriorityQueue<>(numPrefNeighbors + 1,
				(a, b) -> (int) a.getBytesDownloaded() - (int) b.getBytesDownloaded());
		payloadProcess = PayloadProcess.getInstance();
		System.out.println("clock started at "+System.currentTimeMillis());
		splitFile =splitFile.getInstance();
		allConnections = new ArrayList<Connection>();
		interested = new HashSet<>();
		
	}
	
	@Override
	public void run() {
		optUnchoker.start();
		try {
			Thread.sleep(unchokingInterval);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		List<Connection> interestedPeers = new ArrayList<Connection>(interested);
		RemotePeerInfo peer = peerInfo.getPeer(peerProcess.getPeerId());
		if(peer!=null && peer.hasFile) {
			Collections.shuffle(interestedPeers);
			
		}
		else {
		Collections.sort(interestedPeers,
				(a, b) -> (int) b.getBytesDownloaded() - (int) a.getBytesDownloaded());
		//tempPref is used to decide which neighbors to choke after this 
	
		}
		PriorityQueue<Connection> tempPref = new PriorityQueue<Connection>();
		tempPref.addAll(prefNeighbors);
		//update preferred neighbors
		prefNeighbors.clear();
		prefNeighbors.addAll(interestedPeers.subList(0, Math.min(numPrefNeighbors, interestedPeers.size())));
		Collection<Connection> chokeNeighbors=CollectionUtils.subtract(tempPref, prefNeighbors);
		Collection<Connection> unchokeNeighbors=CollectionUtils.subtract(prefNeighbors,tempPref);

		for(Connection conn : chokeNeighbors) {
			payloadProcess.addMessage(new Object[] { conn, Message.MsgType.CHOKE, Integer.MIN_VALUE });
			LoggerUtil.getInstance().logChangePreferredNeighbors(getTime(), peerProcess.getPeerId(),prefNeighbors);
			System.out.println("Choking:" + conn.getRemotePeerId());

		}
		for(Connection conn: unchokeNeighbors) {
			payloadProcess.addMessage(new Object[] { conn, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
			System.out.println("Unchoke new pref neighbors"+conn.remotePeerId);
			LoggerUtil.getInstance().logUnchokingNeighbor(getTime(), conn.getRemotePeerId(), peerProcess.getPeerId());
		}

	}
	public static PeerManager getPeerManager() {

		if (peerManager == null) {
			peerManager = new PeerManager();
			peerManager.run();
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
		if (prefNeighbors.size() <= numPrefNeighbors && !prefNeighbors.contains(connection)) {
			connection.setDownloadedbytes(0);
			prefNeighbors.add(connection);
			System.out.println("Added to pref neighbors");
			payloadProcess.addMessage(new Object[] { connection, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
		}
		interested.add(connection);
		uninterested.remove(connection);
	}

	public synchronized PriorityQueue<Connection> getpreferredneighbours(){
		return prefNeighbors;
		
	}

}
