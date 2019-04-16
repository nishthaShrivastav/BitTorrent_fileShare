package com.ufl.cise.logsconstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.PriorityQueue;

import com.ufl.cise.cnt5106.Connection;
import com.ufl.cise.cnt5106.Peer;
import com.ufl.cise.logsconstants.LoggerUtil;

public class LoggerUtil {
	
	private static LoggerUtil logger;
	public static PrintWriter printWriter = null;

	private LoggerUtil() {
	try {
		System.out.println(Peer.getInstance().getPeerID());
		File logFile = new File(Constants.LOG_FILE_PATH + Peer.getInstance().getPeerID() + Constants.LOG_FILE_EXTENSION);
		logFile.getParentFile().mkdirs();
		logFile.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(logFile, false);
		printWriter = new PrintWriter(fileOutputStream, true);
		} catch (Exception e) {
			System.out.println("Error: Failed to create log file");
		}
	}

	public static synchronized LoggerUtil getLoggerInstance() {
		if (logger == null) {
			logger = new LoggerUtil();
		}
		return logger;
	}

	private void writeLogFile(String message) {
		synchronized (this) {
			printWriter.println(message);
		}
	}
	
	public void logTcpConnectionTo(String sourcePeer, String destinationPeer) {
		writeLogFile(getTime() + "Peer " + sourcePeer + " makes a connection to Peer " + destinationPeer + ".");
	}

	public String getTime() {
		return Calendar.getInstance().getTime() + ": ";
	}
	
	public void logTcpConnectionFrom(String sourcePeer, String destinationPeer) {
		writeLogFile(getTime() + "Peer " + sourcePeer + " is connected from Peer " + destinationPeer + ".");
	}
	
	public void logChangePreferredNeighbors(String timestamp, int i, PriorityQueue<Connection> prefferedPeers) {
		StringBuilder log = new StringBuilder();
		log.append(timestamp);
		log.append("Peer " + i + " has the preferred neighbors ");
		String prefix = "";
		Iterator<Connection> iterator = prefferedPeers.iterator();
		while (iterator.hasNext()) {
			log.append(prefix);
			prefix = ", ";
			log.append(iterator.next().getRemotePeerId());
		}
		writeLogFile(log.toString() + ".");
	}
	
	public void logOptimisticallyUnchokeNeighbor(String timestamp, int i, String unchokedNeighbor) {
		writeLogFile(timestamp + "Peer " + i + " has the optimistically unchoked neighbor " + unchokedNeighbor + ".");
	}
	
	public void logUnchokingNeighbor(String timestamp, int unchokedPeer, String peer) {
		writeLogFile(timestamp + "Peer " + unchokedPeer + " is unchoked by " + peer + ".");
	}
	
	public void logChokingNeighbor(String timestamp, int chokedPeer, String peer) {
		writeLogFile(timestamp + "Peer " + chokedPeer + " is choked by " + peer + ".");
	}
	
	public void logReceivedHaveMessage(String timestamp, int i, String sourcePeer, int pieceIndex) {
		writeLogFile(timestamp + "Peer " + i + " received the 'have' message from " + sourcePeer + " for the piece "+ pieceIndex + ".");
	}
	
	public void logReceivedInterestedMessage(String timestamp, int i, String sourcePeer) {
		writeLogFile(timestamp + "Peer " + i + " received the 'interested' message from " + sourcePeer + ".");
	}
	
	public void logReceivedNotInterestedMessage(String timestamp, int i, String sourcePeer) {
		writeLogFile(timestamp + "Peer " + i + " received the 'not interested' message from " + sourcePeer + ".");
	}
	
	public void logDownloadedPiece(String timestamp, int i, String sourcePeer, int pieceIndex, int numberOfPieces) {
		String message = timestamp + "Peer " + i + " has downloaded the piece " + pieceIndex + " from " + sourcePeer + ".";
		message += "Now the number of pieces it has is " + numberOfPieces;
		writeLogFile(message);
	}
	
	public void logFinishedDownloading(String timestamp, int i) {
		writeLogFile(timestamp + "Peer " + i + " has downloaded the complete file.");
	}
	
	public void logDebug(String message) {
		writeLogFile(message);
	}
}

