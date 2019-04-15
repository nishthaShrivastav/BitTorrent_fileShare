package com.ufl.cise.cnt5106;

import java.util.*;
import com.ufl.cise.conf.Common;
import com.ufl.cise.messages.Message;

public class Bitfield extends Message {

		private static Bitfield Bitfield;
		private splitFile spFile;

		private Bitfield() {
			getBitfield();
		}
		
		private void getBitfield() {
			msgNum= 5;
			int numPieces = Common.getNumberOfPieces(); 
			payload = new byte[numPieces+1];
			content = new byte[numPieces];
			spFile = splitFile.getInstance();
			payload[0] = msgNum;
			BitSet filePieces = splitFile.getFilePieces();
			for (int i = 0; i < numPieces; i++) {
				if (filePieces.get(i)) {
					payload[i + 1] = 1;
				}
			}
		
		}

		public synchronized static Bitfield getInstance() {
			if (Bitfield == null) {
				Bitfield = new Bitfield();
			}
			return Bitfield;
		}

		@Override
		public synchronized int getMessageLength() {
			getBitfield();
			return payload.length;
		}

		@Override
		public synchronized byte[] getPayload() {
			return payload;
		}

	}


