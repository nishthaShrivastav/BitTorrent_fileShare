package com.ufl.cise.cnt5106;
import java.io.*;
import java.util.*;

import com.ufl.cise.conf.*;

/*
 * main function to start program
 * 
 * peer id has argument 
 * read common config file
 * read peer config file
 * 
 * identify peers to communicate with and 
 * create process to communicate  
 * 
 */
public class peerProcess {

	public static void main(String[] args) throws Exception {
		
		if(args.length!=1) {
			throw new Exception("The numbe rof arguments passed is  "+args.length+" but the required length is 1");
			
		}
		final int peerId = Integer.parseInt(args[0]);
    	String address = "localhost";
        int port = 6008;
	    boolean hasFile = false;
		Reader commonReader = null;
		Reader peerReader = null;
		Properties common =null;
		PeerInfoProperties peerInfo = new PeerInfoProperties();
		Collection<RemotePeerInfo> peersToConnect= new LinkedList<>();
		
		try {
			commonReader = new FileReader(CommonProperties.CONFIG_FILE_NAME);
			common= CommonProperties.read(commonReader);
			peerReader = new FileReader(PeerInfoProperties.CONFIG_FILE_NAME);
			peerInfo.read(peerReader);
			for( RemotePeerInfo peer : peerInfo.getPeerInfo()) {
				if(peer.getPeerId()==peerId) {
					address= peer.getPeer_Address();
					port = peer.getPort();
					hasFile= peer.has_File();
					
					break;
				}
				peersToConnect.add(peer);
			}
			
		}
		catch(Exception e){
			//log exceptions
		}
		finally {
			try {
				commonReader.close();
			}
			catch(Exception e) {}
			try {
				peerReader.close();
			}
			catch(Exception e) {}
			
		}
		Process proc = new Process(peerId, port, address, peerInfo.getPeerInfo(), hasFile, common);
        proc.startProcess();
        Thread t = new Thread (proc);
        t.start();
        //connect to peers
	}

}
