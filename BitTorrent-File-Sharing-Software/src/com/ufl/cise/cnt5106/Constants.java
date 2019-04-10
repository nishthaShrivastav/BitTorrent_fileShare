package com.ufl.cise.cnt5106;

import java.io.File;

public class Constants {
	
	public static final String CONFIG_FILE_NAME = "Common.cfg";
	public static final String PEER_CONFIG_FILE_NAME = "PeerInfo.cfg";
	public static final String NUMBER_OF_PREFERRED_NEIGHBORS = "NumberOfPreferredNeighbors";
	public static final String UNCHOKING_INTERVAL = "UnchokingInterval";
	public static final String CREATED_FILE_PATH = System.getProperty("user.dir") + File.separatorChar
			+ "project"+File.separatorChar+"peer_";
	public static final String SOURCE_FILE_PATH =System.getProperty("user.dir") + File.separatorChar+"src"+File.separatorChar;;
	public static final String OPTIMISTIC_UNCHOKING_INTERVAL = "OptimisticUnchokingInterval";
	public static final String FILENAME = "FileName";
	public static final String FILESIZE = "FileSize";
	public static final String PIECESIZE = "PieceSize";
	public static final String PEER_LOG_FILE_PATH = null;
	public static final String PEER_LOG_FILE_EXTENSION = null;
	
	//System.getProperty("user.dir") fetches the directory or path of the workspace for the current project

}
