package com.ufl.cise.cnt5106;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.conf.Common;
import com.ufl.cise.messages.Message;
import com.ufl.cise.messages.Message.MsgType;

import com.ufl.cise.messages.MessageHandler;


public class SharedData implements Runnable{
	
	private LinkedBlockingQueue<byte[]> payloadQueue;
	private boolean isAlive;
	private Peer host = Peer.getInstance();
	private String remotePeerId;
	private splitFile splitFile;
	private BitSet peerBitset;
	private Connection connection;
	private volatile boolean uploadHandshake;
	private Upload upload;
	private boolean bitfieldSent;
	private boolean isHandshakeDownloaded;
	private MessageHandler messagehandler;
	private boolean peerHasFile;
	private PayloadProcess payloadProcess;
	
	
	public SharedData(Connection connection) {
		this.connection = connection;
		payloadQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		splitFile = splitFile.getInstance();
		payloadProcess= PayloadProcess.getInstance();
		peerBitset = new BitSet(Common.getNumberOfPieces());
	}
	
	public void setUpload(Upload val) {
		// put message in the queue to send handshake (broadcaster queue)
		upload = val;
		if (getUploadHandshake()) {
			Object[] msg =  { connection, Message.MsgType.HANDSHAKE, Integer.MIN_VALUE };
			payloadProcess.addMessage(msg);
		}
	}

	@Override
	public void run() {
		// take messaage from queue to process handshake
		while (isAlive) {
			try {
				byte[] p = payloadQueue.take();
				System.out.println("sharedData: get payload from queue and send for processing");
				processPayload(p);
			} catch (InterruptedException e) {
				System.out.println("Error in SharedData thread"+e);
			}
		}
	}
	
	public void addPayload(byte[] payload) {
		try {
			payloadQueue.put(payload);
		} catch (InterruptedException e) {
			System.out.println("Error in SharedAData addPayload"+e);
		}
		
	}
	//merge send and setupload later
	public synchronized void sendHandshake() {
		setUploadHandshake();
	}
	
	public synchronized void setUploadHandshake() {
		uploadHandshake = true;
	}
	

	public synchronized boolean getUploadHandshake() {
		return uploadHandshake; 
	}



	private void processPayload(byte[] p) {
		System.out.println("payload[0]= "+p[0]);
		MsgType msgType = getMessageType(p[0]);
		MsgType responseMsgType = null;
		int pieceIndex = Integer.MIN_VALUE;
		System.out.println("Shareddata processPayload message received:"+msgType);
		switch (msgType) {
		case CHOKE:
			LoggerUtil.getInstance().logChokingNeighbor(getTime(), peerProcess.getPeerId(), connection.getRemotePeerId());
			connection.removeRequestedPiece();
			responseMsgType = null;
			break;
		case UNCHOKE:
			// respond with request
			LoggerUtil.getInstance().logUnchokingNeighbor(getTime(), peerProcess.getPeerId(), connection.getRemotePeerId());
			responseMsgType = MsgType.REQUEST;
			pieceIndex = splitFile.getRequestPieceIndex(connection);
			break;
		case INTERESTED:
			// add to interested connections
			LoggerUtil.getInstance().logReceivedInterestedMessage(getTime(), peerProcess.getPeerId(),connection.getRemotePeerId());
			connection.addInterestedConnection();
			responseMsgType = null;
			break;
		case NOTINTERESTED:
			// add to not interested connections
			LoggerUtil.getInstance().logReceivedNotInterestedMessage(getTime(), peerProcess.getPeerId(),
					connection.getRemotePeerId());
			connection.addNotInterestedConnection();
			responseMsgType = null;
			break;
		case HAVE:
			// update peer bitset
			// send interested/not interested
			pieceIndex = ByteBuffer.wrap(p, 1, 4).getInt();
			LoggerUtil.getInstance().logReceivedHaveMessage(getTime(), peerProcess.getPeerId(), connection.getRemotePeerId(),
					pieceIndex);
			updatePeerBitset(pieceIndex);
			if(isInterested()) {
				responseMsgType= MsgType.INTERESTED;
			}
			else {
				responseMsgType=MsgType.NOTINTERESTED;
			}
			
			break;
		case BITFIELD:
			// update peer bitset
			// send interested/not interested
			setPeerBitset(p);
			if(isInterested()) {
				responseMsgType= MsgType.INTERESTED;
			}
			else {
				responseMsgType=MsgType.NOTINTERESTED;
			}
			break;
		case REQUEST:
			// send requested piece
			responseMsgType = MsgType.PIECE;
			byte[] content = new byte[4];
			System.arraycopy(p, 1, content, 0, 4);
			pieceIndex = ByteBuffer.wrap(content).getInt();
			if (pieceIndex == Integer.MIN_VALUE) {
				System.out.println("received file");
				responseMsgType = null;
			}
			break;
		case PIECE:
			/*
			 * update own bitset & file . Send have to all neighbors & notinterested to
			 * neighbors with same bitset. Respond with request update bytesDownloaded pi =
			 * pieceIndex
			 */
			pieceIndex = ByteBuffer.wrap(p, 1, 4).getInt();
			connection.addToBytesDownloaded(p.length);
			splitFile.setPiece(Arrays.copyOfRange(p, 1, p.length));
			LoggerUtil.getInstance().logDownloadedPiece(getTime(), peerProcess.getPeerId(), connection.getRemotePeerId(),
				pieceIndex, splitFile.getReceivedFileSize());
			responseMsgType = MsgType.REQUEST;
			connection.tellAllNeighbors(pieceIndex);
			pieceIndex = splitFile.getRequestPieceIndex(connection);
			if (pieceIndex == Integer.MIN_VALUE) {
				LoggerUtil.getInstance().logFinishedDownloading(getTime(), peerProcess.getPeerId());
				splitFile.writeToFile(peerProcess.getPeerId());
				msgType = null;
				isAlive = false;
				responseMsgType = null;
				// conn.close();
			}
			break;
		case HANDSHAKE:
			remotePeerId = Handshake.get_Id(p);
			connection.setPeerId(remotePeerId);
			connection.addAllConnections();
			if (!getUploadHandshake()) {
				setUploadHandshake();
				LoggerUtil.getInstance().logTcpConnectionFrom(host.getPeerInfo().getPeerId(), remotePeerId);
				payloadProcess.addMessage(new Object[] { connection, MsgType.HANDSHAKE, Integer.MIN_VALUE });
			}
			if (splitFile.hasAnyPieces()) {
				responseMsgType = MsgType.BITFIELD;
			}
			break;
		}
		if (null != responseMsgType) {
			
			payloadProcess.addMessage(new Object[] { connection, responseMsgType, pieceIndex });
		}
		
	}

	private MsgType getMessageType(byte b) {
		 messagehandler = messagehandler.getInstance();
		if (!isHandshakeDownloaded) {
			isHandshakeDownloaded=true;
			return Message.MsgType.HANDSHAKE;
		}
		return messagehandler.getType(b);
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


	public String getTime() {
		return Calendar.getInstance().getTime() + ": ";
	}
	
	public boolean hasFile() {
	
		return peerHasFile;
	}
	
	public synchronized void setPeerBitset(byte[] payload) {
		for (int i = 1; i < payload.length; i++) {
			if (payload[i] == 1) {
				peerBitset.set(i - 1);
			}
		}
		//if the peerbitset values are 1 for all pieces it means all pieces are received
		if (peerBitset.cardinality() == Common.getNumberOfPieces()) {
			peerHasFile = true;
			PeerManager.getPeerManager().addToFullFileList(remotePeerId);
		}
	}
	
	//updatePeerBitset and setPeerbitset has partial code same
	public synchronized void updatePeerBitset(int index) {
		peerBitset.set(index);
		if (peerBitset.cardinality() == Common.getNumberOfPieces()) {
			PeerManager.getPeerManager().addToFullFileList(remotePeerId);
			peerHasFile = true;
		}
	}
	
	private boolean isInterested() {
		for (int i = 0; i < Common.getNumberOfPieces(); i++) {
			if (peerBitset.get(i) && !splitFile.isPieceAvailable(i)) {
				return true;
			}
		}
		return false;
	}


}
