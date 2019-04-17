package com.ufl.cise.cnt5106;

import java.net.Socket;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.ufl.cise.conf.*;
import com.ufl.cise.logsconstants.LoggerUtil;
import com.ufl.cise.messages.*;


public class PeerManager{


	private static PeerManager peerManager;
	private SplitFile splitFile;
	private PayloadProcess payloadProcess;
	PeerInfo peerInfo = new PeerInfo();
	
	private HashSet<Connection> uninterested;
	private HashSet<Connection> interested;
	private PriorityQueue<Connection> prefNeighbors;
	public HashSet<String> peersWithFullFile = new HashSet<String>();
	private List<Connection> allConnections;
	
	private int numPrefNeighbors = Common.getNumberOfPreferredNeighbors();
	private int unchokingInterval = Common.getUnchokingInterval();
	private int optUnchokingInterval = Common.getOptimisticUnchokingInterval();
	private int numberOfPeers = peerInfo.numberOfPeers();
	

	private PeerManager() {

		uninterested = new HashSet<>();
		prefNeighbors = new PriorityQueue<>(numPrefNeighbors + 1,(a, b) -> (int) a.getBytesDownloaded() - (int) b.getBytesDownloaded());
		payloadProcess = PayloadProcess.getInstance();
		splitFile =splitFile.getInstance();
		allConnections = new ArrayList<Connection>();
		interested = new HashSet<>();
		chokeUnchokePeers();

	}

	public static synchronized PeerManager getPeerManagerInstance() {

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

	protected synchronized void sendHavetoAll(int pieceIndex) {
		for (Connection conn : allConnections) {
			payloadProcess.addMessagetoQueue(new Object[] { conn, Message.MsgType.HAVE, pieceIndex });
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
		interested.remove(connection);
		System.out.println("Removing from interested and pref "+connection.remotePeerId);
	}

	public synchronized void addInterestedConnection(String peerId, Connection connection) {
		if (prefNeighbors.size() < numPrefNeighbors && !prefNeighbors.contains(connection)) {
			connection.setDownloadedbytes(0);
			prefNeighbors.add(connection);
			System.out.println("Added to pref neighbors" +connection.remotePeerId);
			payloadProcess.addMessagetoQueue(new Object[] { connection, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
		}
		interested.add(connection);
		uninterested.remove(connection);
		System.out.println("added to interested connections "+connection.remotePeerId);
	}

	public synchronized PriorityQueue<Connection> getpreferredneighbours(){
		return prefNeighbors;

	}
	public void chokeUnchokePeers()
	{
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("start opt unchoking after a sleep");
				if(peersWithFullFile.size()==numberOfPeers-1 && splitFile.isCompleteFile()) {
					System.out.println("All peers have files");
					System.exit(0);
				}
				if(null!=allConnections && null!= interested && null!=prefNeighbors && !allConnections.isEmpty() && interested.size()>prefNeighbors.size()) {
					Collections.shuffle(allConnections);
					for (Connection conn : allConnections) {
						System.out.println("shuffled connections and iterating, interested empty, prefN empty"+interested.isEmpty()+" "+prefNeighbors.isEmpty());
						System.out.println("interested.contains(conn) "+conn.remotePeerId +interested.contains(conn));
						System.out.println("prefNeighbors contains conn "+prefNeighbors.contains(conn));
						System.out.println("has file"+conn.hasFile());
						if (interested.contains(conn) && !prefNeighbors.contains(conn) ) {
							System.out.println("opt Unchoke neighbor "+conn.remotePeerId);
							payloadProcess.addMessagetoQueue(new Object[] { conn, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
							prefNeighbors.add(conn);
							System.out.println("Optimistic unchoke done for peer "+conn.remotePeerId);
							LoggerUtil.getLoggerInstance().logOptimisticallyUnchokeNeighbor(getTime(), peerProcess.getPeerId(),conn.getRemotePeerId());
							break;
						}
					}
				}
			}
		}, new Date(), optUnchokingInterval*1000);


		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(peersWithFullFile.size()==numberOfPeers-1 && splitFile.isCompleteFile()) {
					System.out.println("All peers have files");
					System.exit(0);
				}
				System.out.println("start choking after a sleep");
				List<Connection> interestedPeers = new ArrayList<Connection>(interested);
				System.out.println("Interested size "+interestedPeers.size());
				System.out.println("pref neighbors size"+prefNeighbors.size());
				if(!prefNeighbors.isEmpty() && interestedPeers.size()>prefNeighbors.size()) {
					System.out.println("started choking/unchoking");
					if(splitFile.isCompleteFile()) {
						System.out.println("peer has file, shuffle");
						Collections.shuffle(interestedPeers);

					}
					else {
						System.out.println("peer sorting files");
						Collections.sort(interestedPeers,
								(a, b) -> (int) b.getBytesDownloaded() - (int) a.getBytesDownloaded());
						//tempPref is used to decide which neighbors to choke after this 

					}
					List<Connection> oldPref = new ArrayList(prefNeighbors);
					//update preferred neighbors
					prefNeighbors.clear();
					for(int i=0;i<Math.min(numPrefNeighbors, interestedPeers.size());i++) {
						prefNeighbors.add(interestedPeers.get(i));
					}
					List<Connection> newPref = new ArrayList<Connection>(prefNeighbors);
					//temp storage for the new pref neighbors
					
					Collection<Connection> toChoke = CollectionUtils.subtract(oldPref, newPref);
					Collection<Connection> toUnchoke = CollectionUtils.subtract(newPref, oldPref);

					System.out.println("Setting all down bytes to 0");
					for(Connection conn:allConnections) {
						conn.setDownloadedbytes(0);
					}
					for(Connection conn : toChoke) {
						payloadProcess.addMessagetoQueue(new Object[] { conn, Message.MsgType.CHOKE, Integer.MIN_VALUE });
						System.out.println("Choking:" + conn.getRemotePeerId());
					}
					LoggerUtil.getLoggerInstance().logChangePreferredNeighbors(getTime(), peerProcess.getPeerId(), toUnchoke);
					for(Connection conn: toUnchoke) {
						payloadProcess.addMessagetoQueue(new Object[] { conn, Message.MsgType.UNCHOKE, Integer.MIN_VALUE });
						System.out.println("Unchoke new pref neighbors"+conn.remotePeerId);
						LoggerUtil.getLoggerInstance().logUnchokingNeighbor(getTime(), Integer.parseInt(conn.getRemotePeerId()),String.valueOf(peerProcess.getPeerId()));
					}

				}

			}
		}, new Date(), unchokingInterval*1000);
	}

}
