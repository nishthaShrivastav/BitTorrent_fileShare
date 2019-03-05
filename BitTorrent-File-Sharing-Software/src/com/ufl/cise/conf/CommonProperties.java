package com.ufl.cise.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Properties;

public class CommonProperties extends Properties {
	
	public static final String CONFIG_FILE_NAME = "Common.cfg";
	
	public static Properties read (Reader reader) throws Exception {
		CommonProperties conf = new CommonProperties();
		conf.load(reader);
		return conf;
	}
	
	@Override
	public synchronized void load(Reader reader) throws IOException {
		  BufferedReader in = new BufferedReader(reader);
          int i = 0;
          for (String line; (line = in.readLine()) != null; i++) {
              line = line.trim();
              String[] props = line.split("\\s+");
              if (props.length!=2) {
            	  throw new IOException(new ParseException (line, i));
              }
              setProperty(props[0].trim(), props[1].trim());
              
          }   
	}
}
