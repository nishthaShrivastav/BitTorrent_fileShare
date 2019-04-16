package com.ufl.cise.conf;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;


public class PeerInfo {

	 private final static Collection<RemotePeerInfo> allPeers = new LinkedList<>();
	 
	 public void read (Reader reader) throws ParseException, IOException{
		 BufferedReader in = new BufferedReader(reader);
	        int i = 0;
	        for (String line; (line = in.readLine()) != null;) {
	            line = line.trim();
	            
	            String[] tokens = line.split("\\s+");
	            if (tokens.length != 4) {
	                throw new ParseException (line, i);
	            }
	            final boolean hasFile = (tokens[3].trim().equalsIgnoreCase("1"));
	            RemotePeerInfo pi=new RemotePeerInfo(tokens[0].trim(), tokens[1].trim(),
	                    tokens[2].trim(), hasFile);
	            pi.setId(i++);
	            allPeers.add (pi);
	        }
	           
	 }
	 
	 public static Collection<RemotePeerInfo> getPeerInfo () {
	        return new LinkedList<>(allPeers);
	    }
	 public static int numberOfPeers() {
		 return allPeers.size();
		 
	 }
	 public static RemotePeerInfo getPeer(int peerid) {
		 for(RemotePeerInfo pi : allPeers) {
			 try {
				 if(Integer.parseInt(pi.getPeerId())==peerid) {
					 return pi;
				 }
			 }
			 catch(NumberFormatException e) {
				 e.printStackTrace();
			 }
		 }
		 return null;
	 }
}