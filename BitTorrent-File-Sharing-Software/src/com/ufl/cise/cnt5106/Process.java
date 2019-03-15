package com.ufl.cise.cnt5106;
/*
 * Implements runnable interface
 * TO do: run method
 * **/
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import com.ufl.cise.conf.RemotePeerInfo;


public class Process implements Runnable{
	private final int peerId;
	private final int port;
	private final String address;
	private final boolean hasFile;
	private final Properties prop;
	private final PeerManager peerManager;
	
	
	
	
	 public Process(int peerId,int port, String address,Collection<RemotePeerInfo> peerInfo,  boolean hasFile, Properties prop)           	{
		 this.peerId=peerId;
		 this.port=port;
		 this.address=address;
		 this.prop=prop;
		 this.hasFile=hasFile;
		 ArrayList<RemotePeerInfo> remotePeers = new ArrayList<>(peerInfo);
		 for(RemotePeerInfo pInfo: peerInfo) {
			 if(pInfo.getPeerId()==peerId) {
				 peerInfo.remove(pInfo);
				 break;
			 }
		 }
		 //put bitsize of file in place of 0
		 peerManager= new PeerManager(peerId,0,remotePeers,prop);
		//write peer manager
		 
	 }
	 
	 public void startProcess() {
		 if(hasFile) {
			 //split file
		 }
		 else {
			 //log peer does not have file
		 }
		 Thread t = new Thread(peerManager);
	        t.start();
	 }

	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			
		} catch (IOException e) {
			System.out.println(e);
		}
		
	}
	

}
