package com.ufl.cise.conf;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemotePeerInfo{



		public String peerId;
	 	public int id;
	    public String peerPort;
	    public boolean hasFile;
	    private String hostName;
	    
	    public RemotePeerInfo(String P_id, String H_Name, String P_Port, boolean Has_File) {
	        
	        peerId = P_id;
	        hasFile = Has_File;
	        peerPort = P_Port;
	        hostName=H_Name;
	    }
	   
	    public RemotePeerInfo (int peerId) {
	        this (Integer.toString (peerId), "127.0.0.1", "0", false);
	    }

	    public String getPeerId() {
			return peerId;
		}

		public void setPeerId(String peerId) {
			this.peerId = peerId;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getPeerPort() {
			return Integer.parseInt(peerPort);
		}

		public void setPeerPort(String peerPort) {
			this.peerPort = peerPort;
		}

		public boolean hasFile() {
			return hasFile;
		}

		public void setHasFile(boolean hasFile) {
			this.hasFile = hasFile;
		}

		public String getHostName() {
			return hostName;
		}

		public void setHostName(String hostName) {
			this.hostName = hostName;
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
