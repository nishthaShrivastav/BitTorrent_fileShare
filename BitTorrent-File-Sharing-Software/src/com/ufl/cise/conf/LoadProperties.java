package com.ufl.cise.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ufl.cise.logsconstants.Constants;


public class LoadProperties {

	public LoadProperties(){
		load();
	}
	
	public void load() {
		Properties properties = new Properties();
		try {
			FileInputStream in = new FileInputStream(Constants.SOURCE_FILE_PATH+Constants.CONFIG_FILE_NAME);

			properties.load(in);
		} catch (IOException e) {
			System.out.println("Config file not found");
			e.printStackTrace();
		}

		Common.setFileSize(Long.parseLong(properties.get(Constants.FILESIZE).toString()));
		Common.setFileName(properties.getProperty(Constants.FILENAME).toString());
		Common.setNumberOfPreferredNeighbors(
				Integer.parseInt(properties.get(Constants.NUMBER_OF_PREFERRED_NEIGHBORS).toString()));
		Common.setOptimisticUnchokingInterval(
				Integer.parseInt(properties.get(Constants.OPTIMISTIC_UNCHOKING_INTERVAL).toString()));
		Common.setPieceSize(Integer.parseInt(properties.getProperty(Constants.PIECESIZE).toString()));
		Common.setUnchokingInterval(
				Integer.parseInt(properties.getProperty(Constants.UNCHOKING_INTERVAL).toString()));
		Common.setNumberOfPieces();

	}

}
