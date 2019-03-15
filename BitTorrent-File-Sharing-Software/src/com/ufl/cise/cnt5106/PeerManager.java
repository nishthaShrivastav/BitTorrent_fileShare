package com.ufl.cise.cnt5106;
/*Get interested peers and provides all info about the peers choked and unchoked
 * Manages connections of peer with other peers
 * TO DO: add choking interval functions  
 * 
 */

import java.util.*;

import com.ufl.cise.conf.CommonProperties;
import com.ufl.cise.conf.RemotePeerInfo;



public class PeerManager implements Runnable{
	
	private final int prefNeighborsCount;
	private final int bitSize;
    private final int unchokingInterval;
    private final List<RemotePeerInfo> peers = new ArrayList<RemotePeerInfo>();
    private final Collection<RemotePeerInfo> prefPeers = new HashSet<RemotePeerInfo>();
    
    PeerManager(int peerId, int bitsize, Collection<RemotePeerInfo> peerList, Properties conf) {
    	peers.addAll(peerList);
    	prefNeighborsCount = Integer.parseInt(
                conf.getProperty(CommonProperties.NumberOfPreferredNeighbors.toString()));
        unchokingInterval = Integer.parseInt(
                conf.getProperty(CommonProperties.UnchokingInterval.toString())) * 1000;
        this.bitSize = bitsize;

    	
    }

    synchronized RemotePeerInfo findPeer(int peerId) {
        for (RemotePeerInfo peer : peers) {
            if (peer.getPeerId() == peerId) {
                return peer;
            }
        }
        return null;
    }
    synchronized void setPeerInterested(int remotePeerId) {
        RemotePeerInfo peer = findPeer(remotePeerId);
        if (peer != null) {
            peer.set_Interested();
        }
    }

    public int getUnchokingInterval() {
    	return unchokingInterval;
    }
    
    public synchronized void setPeerUninterestPeer(int remotePeerId) {
    	 RemotePeerInfo peer = findPeer(remotePeerId);
         if (peer != null) {
             peer.set_NotInterested();;
         }
    	
    }
    public synchronized List<RemotePeerInfo> getInterestedPeers(){
    	List<RemotePeerInfo> peersIn = new ArrayList<RemotePeerInfo>();
    	for(RemotePeerInfo remotePeer : peersIn) {
    		if(remotePeer.isInterested()) {
    			peersIn.add(remotePeer);
    		}
    	}
    	return peersIn;
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
