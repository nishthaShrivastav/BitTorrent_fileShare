package com.ufl.cise.cnt5106;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.PriorityQueue;

import com.ufl.cise.cnt5106.Constants;
import com.ufl.cise.cnt5106.Peer;

import com.ufl.cise.cnt5106.Connection;

import com.ufl.cise.cnt5106.LoggerUtil;

public class LoggerUtil {
private static LoggerUtil customLogger;
public static PrintWriter printWriter = null;
private LoggerUtil() {
	try {
		System.out.println(Peer.getInstance().getPeerID());

		File file = new File(Constants.LOG_FILE_PATH + Peer.getInstance().getPeerID() + Constants.LOG_FILE_EXTENSION);
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file, false);
		printWriter = new PrintWriter(fileOutputStream, true);
	} catch (Exception e) {
		System.out.println("Error: Failed to create log file");
	}
}

public static synchronized LoggerUtil getInstance() {
	if (customLogger == null) {
		customLogger = new LoggerUtil();
	}
	return customLogger;
}

private void writeToFile(String message) {
	synchronized (this) {
		printWriter.println(message);
	}
}
public void logTcpConnectionTo(String peerFrom, String peerTo) {
	writeToFile(getTime() + "Peer " + peerFrom + " makes a connection to Peer " + peerTo + ".");
}

public String getTime() {
	return Calendar.getInstance().getTime() + ": ";
}
public void logTcpConnectionFrom(String peerFrom, String peerTo) {
	writeToFile(getTime() + "Peer " + peerFrom + " is connected from Peer " + peerTo + ".");
}
public void logChangePreferredNeighbors(String timestamp, int i, PriorityQueue<Connection> peers) {
	StringBuilder log = new StringBuilder();
	log.append(timestamp);
	log.append("Peer " + i + " has the preferred neighbors ");
	String prefix = "";
	Iterator<Connection> iter = peers.iterator();
	while (iter.hasNext()) {
		log.append(prefix);
		prefix = ", ";
		log.append(iter.next().getRemotePeerId());
	}
	writeToFile(log.toString() + ".");
}
public void logOptimisticallyUnchokeNeighbor(String timestamp, String source, String unchokedNeighbor) {
	writeToFile(
			timestamp + "Peer " + source + " has the optimistically unchoked neighbor " + unchokedNeighbor + ".");
}
public void logUnchokingNeighbor(String timestamp, int i, String peerId2) {
	writeToFile(timestamp + "Peer " + i + " is unchoked by " + peerId2 + ".");
}
public void logChokingNeighbor(String timestamp, int i, String peerId2) {
	writeToFile(timestamp + "Peer " + i + " is choked by " + peerId2 + ".");
}
public void logReceivedHaveMessage(String timestamp, int i, String from, int pieceIndex) {
	writeToFile(timestamp + "Peer " + i + " received the 'have' message from " + from + " for the piece "
			+ pieceIndex + ".");
}
public void logReceivedInterestedMessage(String timestamp, int i, String from) {
	writeToFile(timestamp + "Peer " + i + " received the 'interested' message from " + from + ".");
}
public void logReceivedNotInterestedMessage(String timestamp, int i, String from) {
	writeToFile(timestamp + "Peer " + i + " received the 'not interested' message from " + from + ".");
}
public void logDownloadedPiece(String timestamp, int i, String from, int pieceIndex, int numberOfPieces) {
	String message = timestamp + "Peer " + i + " has downloaded the piece " + pieceIndex + " from " + from + ".";
	message += "Now the number of pieces it has is " + numberOfPieces;
	writeToFile(message);

}
public void logFinishedDownloading(String timestamp, int i) {
	writeToFile(timestamp + "Peer " + i + " has downloaded the complete file.");
}
public void logDebug(String message) {
	writeToFile(message);
}
}

