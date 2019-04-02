package com.ufl.cise.cnt5106;

import java.util.BitSet;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.conf.Common;
import com.ufl.cise.messages.Mesaage;
import com.ufl.cise.messages.Mesaage.MsgType;
import com.ufl.cise.messages.MessageHandler;


public class SharedData implements Runnable{
	
	private LinkedBlockingQueue<byte[]> payloadQueue;
	private boolean isAlive;
	private String remotePeerId;
	private splitFile splitFile;
	private BitSet peerBitset;
	private Connection conn;
	private volatile boolean uploadHandshake;
	private Upload upload;
	private splitFile sharedFile;
	private boolean bitfieldSent;
	private boolean isHandshakeDownloaded;
	private MessageHandler messagehandler;
	private 	boolean peerHasFile;
	
	
	public SharedData(Connection connection) {
		conn = connection;
		payloadQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		splitFile = splitFile.getInstance();
		//broadcaster = BroadcastThread.getInstance();
		peerBitset = new BitSet(Common.getNumberOfPieces());
	}
	
	public synchronized void sendHandshake() {
		setUploadHandshake();
	}
	
	public synchronized void setUploadHandshake() {
		uploadHandshake = true;
	}
	
	public void setUpload(Upload value) {
		// put message in the queue to send handshake (broadcaster queue)
		upload = value;
		if (getUploadHandshake()) {
			//broadcaster.addMessage(new Object[] { conn, Message.Type.HANDSHAKE, Integer.MIN_VALUE });
		}
	}

	private boolean getUploadHandshake() {
		// TODO Auto-generated method stub
		return uploadHandshake; 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// take messaage from queue to process handshake
		while (isAlive) {
			try {
				byte[] p = payloadQueue.take();
				processPayload(p);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processPayload(byte[] p) {
		// TODO Auto-generated method stub
		Mesaage.MsgType messageType = getMessageType(p[0]);
		Mesaage.MsgType responseMessageType = null;
		int pieceIndex = Integer.MIN_VALUE;
		switch (messageType) {
		//write other types like choke unchoke interested etc
		case HANDSHAKE:
			remotePeerId = Handshake.get_Id(p);
			conn.setPeerId(remotePeerId);
			conn.addAllConnections();
			
			if (!getUploadHandshake()) {
				setUploadHandshake();
				
				//broadcaster.addMessage(new Object[] { conn, Message.Type.HANDSHAKE, Integer.MIN_VALUE });
		
			}
			if (sharedFile.hasAnyPieces()) {
				responseMessageType = Mesaage.MsgType.BITFIELD;
			}
			
			break;
		
		}
		
		//broadcaster if condition left
		
	}

	private MsgType getMessageType(byte b) {
		// TODO Auto-generated method stub
		 messagehandler = messagehandler.getInstance();
		if (!isHandshakeDownloaded()) {
			setHandshakeDownloaded();
			return Mesaage.MsgType.HANDSHAKE;
		}
		return messagehandler.getType(b);
	}

	public void addPayload(byte[] payload) {
		// TODO Auto-generated method stub
		try {
			payloadQueue.put(payload);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized BitSet getPeerBitSet() {
		return peerBitset;
	}

	public void updatePeerId(String peerId) {
		remotePeerId = peerId;
	}

	public synchronized String getRemotePeerId() {
		return remotePeerId;
	}

	public synchronized void setRemotePeerId(String remotePeerId) {
		this.remotePeerId = remotePeerId;
	}

	public synchronized void setBitfieldSent() {
		bitfieldSent = true;
	}
	
	private boolean isHandshakeDownloaded() {
	
		return isHandshakeDownloaded;
	}

	private void setHandshakeDownloaded() {
		isHandshakeDownloaded = true;
	}

	public String getTime() {
		return Calendar.getInstance().getTime() + ": ";
	}
	
	public boolean hasFile() {
	
		return peerHasFile;
	}

	//interested not interested along with complete switch case remaining

}
