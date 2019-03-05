package com.ufl.cise.cnt5106;

import java.io.*;
import java.util.*;

import com.ufl.cise.conf.CommonProperties;

public class peerProcess {

	public static void main(String[] args) throws Exception {
		
		if(args.length!=1) {
			throw new Exception("The numbe rof arguments passed is  "+args.length+" but the required length is 1");
			
		}
		final int peerId = Integer.parseInt(args[0]);
		Reader commonReader = null;
		Properties common =null;
		try {
			commonReader = new FileReader(CommonProperties.CONFIG_FILE_NAME);
			common= CommonProperties.read(commonReader);
			
			
		}
		catch(Exception e){
			
		}
		
		

	}

}
