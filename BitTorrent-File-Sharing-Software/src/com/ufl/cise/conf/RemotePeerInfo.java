package com.ufl.cise.conf;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;



public class RemotePeerInfo{

	 public String peerId;
	    public String peerPort;
	    public String peerAdd;
	    public Integer bytes_Downloaded_From;
	    public boolean Has_File;
	    private AtomicBoolean interested  ;
	    public BitSet Received_parts;
	    
	    public RemotePeerInfo(String P_address, String P_ID, String P_Port, boolean hasFile) {
	        
	        peerAdd = P_address;
	        peerId = P_ID;
	        Has_File = hasFile;
	        peerPort = P_Port;
	        Received_parts = new BitSet();
	        interested = new AtomicBoolean (false);
	        bytes_Downloaded_From = new Integer (0);
	        
	        
	    }
	    public RemotePeerInfo (int peerId) {
	        this (Integer.toString (peerId), "127.0.0.1", "0", false);
	    }

	    public String getPeer_Address() {
	        return peerAdd;
	    }

	    public boolean has_File() {
	        return Has_File;
	    }
	    public boolean isInterested() {
	        return interested.get();
	    }
	    public void set_Interested() {
	        interested.set (true);
	    }
	    public void set_NotIterested() {
	        interested.set (false);
	    }
	    public int getPort() {
	        return Integer.parseInt(peerPort);
	    }
	    public int getPeerId() {
	        return Integer.parseInt(peerId);
	    }

	    


	    
	    

	    
	    @Override
	    public boolean equals (Object obj) {
	    	if (obj instanceof RemotePeerInfo) {
	            return (((RemotePeerInfo) obj).peerId.equals (peerId));
	        }
	        if (obj == null) {
	            return false;
	        }
	        
	        return false;
	    }



}
