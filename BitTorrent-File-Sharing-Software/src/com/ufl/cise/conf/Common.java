package com.ufl.cise.conf;

public class Common {
	
	private static int numberOfPreferredNeighbors;
	private static int unchokingInterval;
	private static int optimisticUnchokingInterval;
	private static String fileName;
	private static long fileSize;
	private static int pieceSize;
	private static int numberOfPieces;
	
	
	public static int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}
	public static void setNumberOfPreferredNeighbors(int num) {
		numberOfPreferredNeighbors = num;
	}
	public static int getUnchokingInterval() {
		return unchokingInterval;
	}
	public static void setUnchokingInterval(int unchoking_Interval) {
		unchokingInterval = unchoking_Interval;
	}
	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}
	public static void setOptimisticUnchokingInterval(int optimistic_Unchoking) {
		optimisticUnchokingInterval = optimistic_Unchoking;
	}
	public static String getFileName() {
		return fileName;
	}
	public static void setFileName(String fName) {
		fileName = fName;
	}
	public static long getFileSize() {
		return fileSize;
	}
	public static void setFileSize(long size) {
		fileSize = size;
	}
	public static int getPieceSize() {
		return pieceSize;
	}
	public static void setPieceSize(int pSize) {
		pieceSize = pSize;
	}
	public static int getNumberOfPieces() {
		return numberOfPieces;
	}
	public static void setNumberOfPieces() {
		numberOfPieces = (int) (fileSize % pieceSize) == 0 ? (int) (fileSize / pieceSize)
				: (int) (fileSize / pieceSize) + 1;
	}
	public static void logProperties() {
		System.out.println("Common properties:-- number Of Preferred Neighbors: "+numberOfPreferredNeighbors+"unchokingInterval: "+unchokingInterval+"optimisticUnchokingInterval: "+optimisticUnchokingInterval+"fileName: "+fileName+"file size: "+fileSize+"peice size: "+pieceSize+"number of pieces: "+numberOfPieces);
		
	}
	
	

}
