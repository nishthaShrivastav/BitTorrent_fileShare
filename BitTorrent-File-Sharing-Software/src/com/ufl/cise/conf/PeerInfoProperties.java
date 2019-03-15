package com.ufl.cise.conf;

/*
 * This class reads the PeerInfo.cfg file
 * */
import java.io.BufferedReader;


import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;


/*
 * class to read peer info config file
 * read from file split and stored 
 * the information of peers into a linked list vector
 * 
 * write function to get peerid as well 
 */
public class PeerInfoProperties {

	 public static final String CONFIG_FILE_NAME = "PeerInfo.cfg";
	  private final Collection<RemotePeerInfo> _peerInfoVector = new LinkedList<>();
	 
	 public void read (Reader reader) throws ParseException, IOException{
		 BufferedReader in = new BufferedReader(reader);
	        int i = 0;
	        for (String line; (line = in.readLine()) != null;) {
	            line = line.trim();
	            
	            String[] tokens = line.split("\\s+");
	            if (tokens.length != 4) {
	                throw new ParseException (line, i);
	            }
	            final boolean hasFile = (tokens[3].trim()=="1");
	            _peerInfoVector.add (new RemotePeerInfo(tokens[0].trim(), tokens[1].trim(),
	                    tokens[2].trim(), hasFile));
	            i++;
	        }
	           
	 }
	 
	 public Collection<RemotePeerInfo> getPeerInfo () {
	        return new LinkedList<>(_peerInfoVector);
	    }
}