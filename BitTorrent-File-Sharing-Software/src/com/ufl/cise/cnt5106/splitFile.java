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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
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
	private volatile HashMap<Integer, Connection> requestedPieces;
	private static ConcurrentHashMap<Integer, byte[]> file;
	private static FileChannel writeFileChannel;
	
	private splitFile() {
		fileQueue = new LinkedBlockingQueue<>();
		requestedPieces = new HashMap<>();
	
	}
	
	public synchronized static splitFile getInstance() {
		if (split==null) {
			split = new splitFile();
			split.start();
		}
		return split;
	}
	
	static {
		file = new ConcurrentHashMap<Integer, byte[]>();
		filePieces = new BitSet(Common.getNumberOfPieces());
		try {
			File createdFile = new File(Constants.CREATED_FILE_PATH + peerProcess.getPeerId()
			+ File.separatorChar + Common.getFileName());
			createdFile.getParentFile().mkdirs(); // Will create parent directories if not exists
			createdFile.createNewFile();
			writeFileChannel = FileChannel.open(createdFile.toPath(), StandardOpenOption.WRITE);
		} catch (IOException e) {
			System.out.println("Failed to create new file while receiving the file from host peer");
			e.printStackTrace();
		}
	}
	public void split() {
		File filePtr = new File(Constants.SOURCE_FILE_PATH+Common.getFileName()); //add path to the file
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
						else 
							pieceSize = Common.getPieceSize();					
						byte[] piece = new byte[pieceSize];
						
						dis.readFully(piece);
						file.put(pieceIndex, piece);  
						filePieces.set(pieceIndex++);
				}//end for		
			}catch(IOException fileReadError) {
			System.out.println("Error while splitting file");
			fileReadError.printStackTrace();
		}
	}
		catch (FileNotFoundException e) {
			System.out.println("Error reading Configuration file");
			}finally {
			try {
				fis.close();
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error while closing fileinputstream after reading file");
			}
		}
}// end split function
	
	public synchronized static byte[] getPiece(int index) {
		return file.get(index);
	}
	
	public synchronized boolean isPieceAvailable(int index) {
		return filePieces.get(index);
	}
	
	public synchronized int getReceivedFileSize() {
		return filePieces.cardinality();
	}
	
	public synchronized boolean isCompleteFile() {
		return filePieces.cardinality() == Common.getNumberOfPieces();
	}


	public boolean hasAnyPieces() {
		return filePieces.nextSetBit(0) != -1;
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
		filePieces.set(ByteBuffer.wrap(payload, 0, 4).getInt());
		file.put(ByteBuffer.wrap(payload, 0, 4).getInt(), Arrays.copyOfRange(payload, 4, payload.length));
		try {
			fileQueue.put(payload);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public synchronized void writeToFile(int peerId) {
			for (int i = 0; i < file.size(); i++) {
				try {
					ByteBuffer Bb = ByteBuffer.wrap(file.get(i));
					writeFileChannel.write(Bb);
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
	
	
	public static BitSet getFilePieces() {
		return filePieces;
	}


	public synchronized int getRequestPieceIndex(Connection conn) {
		if (isCompleteFile()) {
			System.out.println("File received");
			return Integer.MIN_VALUE;
		}
		BitSet peerBitset = conn.getPeerBitSet();
		int numberOfPieces = Common.getNumberOfPieces();
		BitSet peerClone = (BitSet) peerBitset.clone();
		BitSet myClone = (BitSet) filePieces.clone();
		peerClone.andNot(myClone);
		if (peerClone.cardinality() == 0) {
			return Integer.MIN_VALUE;
		}
		myClone.flip(0, numberOfPieces);
		myClone.and(peerClone);
		System.out.println(peerClone + " " + myClone);
		int[] missingPieces = myClone.stream().toArray();
		int askpieceindex=new Random().nextInt(missingPieces.length);
		while(requestedPieces.containsKey(askpieceindex)) {
			askpieceindex=new Random().nextInt(missingPieces.length);
		}
			return missingPieces[askpieceindex];
		
	}

	public synchronized void removeRequestedPieces(Connection connection) {
		Iterator it = requestedPieces.entrySet().iterator();
		 while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        if(pair.getValue()==connection)
		        	requestedPieces.remove(pair.getKey());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		
	}
	

}