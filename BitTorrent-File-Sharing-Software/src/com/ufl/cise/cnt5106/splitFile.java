package com.ufl.cise.cnt5106;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.BitSet;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
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
	//create a map for pieces and piece index
	//create map for each connection andtheir status? 
	
	
	
	private splitFile() {
		fileQueue = new LinkedBlockingQueue<>();
	
	}
	
	public static synchronized splitFile getInstance() {
		if (split==null) {
			split = new splitFile();
			split.start();
		}
		return split;
	}
	
	//just to split the file
	//get properties from common properties cfg
	public void split() {
		int no_of_pieces=0;
		Properties CommonProperties = null;
		File filePtr = new File(CommonProperties.getProperty("FileName"));
		FileInputStream fis = null;
		DataInputStream dis = null;
		String fileSize = CommonProperties.getProperty("FileSize");
		int size=Integer.parseInt(fileSize);
		//number of pieces allowed get
		
		try {
			
			fis = new FileInputStream(filePtr);
			dis = new DataInputStream(fis);
			String pieceSize = CommonProperties.getProperty("PieceSize");
			int piecesize=Integer.parseInt(pieceSize);
			int pieceIndex = 0;
				
				try {
					for (int i = 0; i < no_of_pieces; i++) {
						
						if(i==no_of_pieces-1) {
							piecesize=size%piecesize;
						}
						else {
							String pieceSize1 = CommonProperties.getProperty("PieceSize");
							 piecesize=Integer.parseInt(pieceSize);
							
						}
						
					
						byte[] piece = new byte[piecesize];
						
						dis.readFully(piece);
						//file.put(pieceIndex, piece);  put in map the piece index and the piece array
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
		return null;
		//return from map created
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