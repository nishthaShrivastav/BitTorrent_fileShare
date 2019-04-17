package com.ufl.cise.cnt5106;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.conf.Common;
import com.ufl.cise.logsconstants.Constants;
import com.ufl.cise.logsconstants.LoggerUtil;

/*
 * file created to split the file into pieces 
 * run method yet to be written
 * function to get the requested piece to be written 
 * 
 * 
 * function written here to split the file into pieces using concurrent data structures
 * and data input output streams 
 */
public class SplitFile extends Thread{
	
	public LinkedBlockingQueue<byte[]> fileQueue;
	private static SplitFile splitfile;
	private volatile static BitSet filePieceswithPeer;
	private volatile HashMap<Integer, Connection> requestedPieces;
	private static ConcurrentHashMap<Integer, byte[]> fileMap;
	private static FileChannel fileOut;
	private boolean writing =false;
	
	private SplitFile() {
		fileQueue = new LinkedBlockingQueue<>();
		requestedPieces = new HashMap<>();
	
	}
	
	public synchronized static SplitFile getInstance() {
		if (splitfile==null) {
			splitfile = new SplitFile();
			splitfile.start();
		}
		return splitfile;
	}
	
	//to be executed only once 
	static {
		
		fileMap = new ConcurrentHashMap<Integer, byte[]>();
		filePieceswithPeer = new BitSet(Common.getNumberOfPieces());
		try {
			
			File outputFile = new File(Constants.CREATED_FILE_PATH + peerProcess.getPeerId()+ File.separatorChar + Common.getFileName());
			outputFile.getParentFile().mkdirs(); // Will create parent directories if not exists
			outputFile.createNewFile();
			fileOut = FileChannel.open(outputFile.toPath(), StandardOpenOption.WRITE);
			
		} catch (IOException e) {
			System.out.println("Failed to create new file while receiving the file from host peer");
			e.printStackTrace();
		}
	}
	
	//to split file
	public void split() {
		
		File filePtr = new File(Constants.SOURCE_FILE_PATH+Common.getFileName()); //add path to the file
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		int fileSize = (int) Common.getFileSize();
		int numberOfPieces = Common.getNumberOfPieces();
		
		try {
			
			fileInputStream = new FileInputStream(filePtr);
			dataInputStream = new DataInputStream(fileInputStream);
			int pieceSize = Common.getPieceSize();
			int pieceIndex = 0;
				
				try {
					
					for (int i = 0; i < numberOfPieces; i++) {
						
						if(i==numberOfPieces-1) {
							pieceSize=fileSize % Common.getPieceSize();
						}
						
						else 
							pieceSize = Common.getPieceSize();					
							byte[] piece = new byte[pieceSize];
						
							dataInputStream.readFully(piece);
							fileMap.put(pieceIndex, piece);  
							filePieceswithPeer.set(pieceIndex++);
					}	//end for
					
				}catch(IOException e) {
					
			System.out.println("Error while splitting file");
			e.printStackTrace();
		}
	}catch (FileNotFoundException e) {
		
			System.out.println("Error reading Configuration file");
			}finally {
				
						try {
								fileInputStream.close();
								dataInputStream.close();
							} catch (IOException e) {
								
									e.printStackTrace();
									System.out.println("Error while closing fileinputstream after reading file");
							}
					}
	}// end split function
	
	public synchronized static byte[] getPiece(int index) {
		return fileMap.get(index);
	}
	
	public synchronized boolean isPieceAvailable(int index) {
		return filePieceswithPeer.get(index);
	}
	
	public synchronized int getReceivedFileSize() {
		return filePieceswithPeer.cardinality();
	}
	
	public synchronized boolean isCompleteFile() {
		return filePieceswithPeer.cardinality() == Common.getNumberOfPieces();
	}


	public synchronized boolean hasAnyPieces() {
		return filePieceswithPeer.nextSetBit(0) != -1;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				byte[] payload = fileQueue.take();
				int pieceIndex = ByteBuffer.wrap(payload, 0, 4).getInt();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	public synchronized void setPiece(byte[] payload) {
		
		filePieceswithPeer.set(ByteBuffer.wrap(payload, 0, 4).getInt());
		fileMap.put(ByteBuffer.wrap(payload, 0, 4).getInt(), Arrays.copyOfRange(payload, 4, payload.length));

		try {
			fileQueue.put(payload);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(isCompleteFile() && !writing) {
			writing=true;
			LoggerUtil.getLoggerInstance().logFinishedDownloading(Calendar.getInstance().getTime() + ": ", peerProcess.getPeerId());
			writeToFile(peerProcess.getPeerId());
		}
	}
	public synchronized void writeToFile(int peerId) {
		System.out.println("Final fileMap"+ fileMap);
		for (int i = 0; i < fileMap.size(); i++) {
				try {
					ByteBuffer Bb = ByteBuffer.wrap(fileMap.get(i));
					fileOut.write(Bb);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
	
	public synchronized void addRequestedPiece(int pieceIndex, Connection connection ) {
		requestedPieces.put(pieceIndex,connection);

	}

	public synchronized void removeRequestedPiece(int pieceindex, Connection connection) {
		
		requestedPieces.remove(pieceindex);
	}
	
	
	public BitSet getFilePieces() {
		return filePieceswithPeer;
	}

	//check removing extra bit fields
	public synchronized int getRequestPieceIndex(Connection conn) {
		
		if (isCompleteFile()) {
			System.out.println("File received");
			return Integer.MIN_VALUE;
		}
		BitSet peerBitset = conn.getPeerBitSet();
		int numberOfPieces = Common.getNumberOfPieces();
		BitSet peerClone = (BitSet) peerBitset.clone();
		BitSet myClone = (BitSet) filePieceswithPeer.clone();
		peerClone.andNot(myClone);
		if (peerClone.cardinality() == 0) {
			return Integer.MIN_VALUE;
		}
		myClone.flip(0, numberOfPieces);
		myClone.and(peerClone);
		int[] missingPieces = myClone.stream().toArray();
		int askpieceindex=new Random().nextInt(missingPieces.length);
		while(requestedPieces.containsKey(askpieceindex)) {
			askpieceindex=new Random().nextInt(missingPieces.length);
		}
			return missingPieces[askpieceindex];
		
	}

	//concurrent modification exception
	public synchronized void removeRequestedPieces(Connection connection) {
		for (Iterator<Connection> iterator = requestedPieces.values().iterator(); iterator.hasNext(); ) {
			Connection conn = iterator.next();
			if(connection.remotePeerId==conn.remotePeerId) {
				iterator.remove();
			}
		}
		
	}
	

}