package com.ufl.cise.cnt5106;

import java.util.*;

import com.ufl.cise.conf.CommonProperties;
import com.ufl.cise.conf.RemotePeerInfo;

import edu.ufl.cise.cnt5106c.PeerManager.OptimisticUnchoker;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.log.LogHelper;

public class PeerManager implements Runnable{
	
	private final int prefNeighborsCount;
	private final int bitSize;
    private final int unchokingInterval;
    private final List<RemotePeerInfo> peers = new ArrayList<>();
    private final Collection<RemotePeerInfo> prefPeers = new HashSet<>();
    
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
    	List<RemotePeerInfo> peersIn = new ArrayList();
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
