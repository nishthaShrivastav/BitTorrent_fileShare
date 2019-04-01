package com.ufl.cise.cnt5106;


import java.io.DataInputStream;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.conf.Common;


/*
 * file created to split the file into pieces 
 * run method yet to be written
 * function to get the requested piece to be written 
 * 
 * 
 * function written here to split the file into pieces using concurrent data structures
 * and data input output streams 
 */
public class splitFile extends Thread{
	
	public LinkedBlockingQueue<byte[]> fileQueue;
	private static splitFile split;
	private volatile static BitSet filePieces;
	private volatile HashMap<Connection, Integer> requestedPieces;
	private static ConcurrentHashMap<Integer, byte[]> file;
	//create a map for pieces and piece index
	//create map for each connection and their status? 
	
	
	private splitFile() {
		fileQueue = new LinkedBlockingQueue<>();
		requestedPieces = new HashMap<>();
	
	}
	
	public static synchronized splitFile getInstance() {
		if (split==null) {
			split = new splitFile();
			split.start();
		}
		return split;
	}
	
	public void split() {
		File filePtr = new File(Common.getFileName()); //add path to the file
		FileInputStream fis = null;
		DataInputStream dis = null;
		int fileSize = (int) Common.getFileSize();
		int numberOfPieces = Common.getNumberOfPieces();
		
		try {
			
			fis = new FileInputStream(filePtr);
			dis = new DataInputStream(fis);
			int pieceSize = Common.getPieceSize();
			
			int pieceIndex = 0;
				
				try {
					for (int i = 0; i < numberOfPieces; i++) {
						
						if(i==numberOfPieces-1) {
							pieceSize=fileSize % Common.getPieceSize();
						}
						else {
							
							pieceSize = Common.getPieceSize();
							
						}						
					
						byte[] piece = new byte[pieceSize];
						
						dis.readFully(piece);
						file.put(pieceIndex, piece);  
						filePieces.set(pieceIndex++);
					
				}//end for		
			
			
		}catch(IOException fileReadError) {
			System.out.println("Error while splitting file");
		}
			
	}
		catch (FileNotFoundException e) {
			System.out.println("Error reading Configuration file");
			}
		finally {
			try {
				fis.close();
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error while closing fileinputstream after reading file");
			}
		}
}// end split function
	
	public synchronized byte[] getPiece(int index) {
		return file.get(index);
	}
	
	public synchronized boolean isPieceAvailable(int index) {
		return filePieces.get(index);
	}
	
	public synchronized int getReceivedFileSize() {
		return filePieces.cardinality();
	}
	
	//we still have to add functions to write to file 
	//and getting the actual requested piece
}