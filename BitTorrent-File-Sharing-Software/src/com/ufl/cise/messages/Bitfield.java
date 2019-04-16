package com.ufl.cise.messages;

import java.util.*;

import com.ufl.cise.cnt5106.SplitFile;
import com.ufl.cise.conf.Common;

public class Bitfield extends Message {

		private static Bitfield Bitfield;
		private SplitFile splitFile;

		private Bitfield() {
			getBitfieldContent();
		}
		
		private void getBitfieldContent() {
			msgNum= 5;
			int numPieces = Common.getNumberOfPieces(); 
			messageContent = new byte[numPieces+1];
			content = new byte[numPieces];
			splitFile = SplitFile.getInstance();
			messageContent[0] = msgNum;
			BitSet filePieces = SplitFile.getFilePieces();
			for (int i = 0; i < numPieces; i++) {
				if (filePieces.get(i)) {
					messageContent[i + 1] = 1;
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
			getBitfieldContent();
			return messageContent.length;
		}

		@Override
		public synchronized byte[] getMessageContent() {
			return messageContent;
		}

	}


