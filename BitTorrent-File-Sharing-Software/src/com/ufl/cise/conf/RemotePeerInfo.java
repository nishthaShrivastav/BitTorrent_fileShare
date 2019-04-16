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
	   
	    public String getPeerId() {
			return peerId;
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


		public boolean hasFile() {
			return hasFile;
		}

		public String getHostName() {
			return hostName;
		}

}
