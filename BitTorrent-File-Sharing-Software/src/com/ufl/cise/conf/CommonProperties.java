package com.ufl.cise.conf;
/*
 * This class reads the common.cfg file 
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Properties;


/*
 * This program has been written to split the file
 * and check following properties:
 * Number of Preferred Neighbours
 * The unchoking interval
 * File name
 * File size
 * Piece Size
 */
public enum CommonProperties{


    NumberOfPreferredNeighbors,
    UnchokingInterval,
    OptimisticUnchokingInterval,
    FileName,
    FileSize,
    PieceSize;

    public static final String CONFIG_FILE_NAME = "Common.cfg";

    public static Properties read (Reader reader) throws Exception {

        final Properties conf = new Properties () {
            @Override
            public synchronized void load(Reader reader)
                    throws IOException {
                BufferedReader in = new BufferedReader(reader);
                int i = 0;
                
                
                for (String line; (line = in.readLine()) != null; i++) {
                    line = line.trim();
                    if ((line.length() <= 0) || (line.startsWith ("#"))) {
                        continue;
                    }
                    
                    String[] tokens = line.split("\\s+");
                    if (tokens.length != 2) {
                        throw new IOException (new ParseException (line, i));
                    }
                    setProperty(tokens[0].trim(), tokens[1].trim());
                }
            }
        };

        conf.load (reader);

        for (CommonProperties prop : CommonProperties.values()) {
            if (!conf.containsKey(prop.toString())) {
                throw new Exception ("config file does not contain property " + prop);
            }
        }

        return conf;
    }

}
