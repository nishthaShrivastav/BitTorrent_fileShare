package com.ufl.cise.cnt5106;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ufl.cise.conf.Common;
import com.ufl.cise.datastreams.DataOut;
import com.ufl.cise.logsconstants.LoggerUtil;
import com.ufl.cise.messages.Handshake;
import com.ufl.cise.messages.Message;
import com.ufl.cise.messages.Message.MsgType;
import com.ufl.cise.messages.MessageHandler;


public class SharedData extends Thread{
	
	private LinkedBlockingQueue<byte[]> payloadQueue;
	private boolean isAlive;
	private Peer host = Peer.getInstance();
	private String remotePeerId;
	private SplitFile splitFile;
	private BitSet peerBitset;
	private Connection connection;
	private volatile boolean uploadHandshake;
	private DataOut upload;
	private volatile boolean bitfieldSent;
	private volatile boolean isHandshakeDownloaded;
	private MessageHandler messagehandler;
	private boolean peerHasFile;
	private PayloadProcess payloadProcess;
	private PeerManager peermgr;
	
	
	public SharedData(Connection connection) {
		this.connection = connection;
		payloadQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		splitFile = splitFile.getInstance();
		payloadProcess= PayloadProcess.getInstance();
		peerBitset = new BitSet(Common.getNumberOfPieces());
		peermgr = PeerManager.getPeerManagerInstance();
		peerHasFile=splitFile.isCompleteFile();
	}
	
	public void setUploadHandshake(DataOut val) {
		// put message in the queue to send handshake (broadcaster queue)
		upload = val;
		if (getUploadHandshake()) {
			Object[] msg =  { connection, Message.MsgType.HANDSHAKE, Integer.MIN_VALUE };
			payloadProcess.addMessagetoQueue(msg);
		}
	}

	@Override
	public void run() {
		// take messaage from queue to process handshake
		while (isAlive) {
			try {
				byte[] p = payloadQueue.take();
				processPayload(p);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void addPayload(byte[] payload) {
		try {
			payloadQueue.put(payload);
		} catch (InterruptedException e) {
		}
		
	}
	
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
		MsgType msgType = getMessageType(p[0]);
		MsgType responseMsgType = null;
		int pieceIndex = Integer.MIN_VALUE;
		switch (msgType) {
		case CHOKE:
			LoggerUtil.getLoggerInstance().logChokingNeighbor(getTime(),peerProcess.getPeerId(),connection.getRemotePeerId());
			connection.removeRequestedPieces(connection);
			responseMsgType = null;
			break;
		case UNCHOKE:
			// respond with request
			LoggerUtil.getLoggerInstance().logUnchokingNeighbor(getTime(), peerProcess.getPeerId(),connection.getRemotePeerId());
			responseMsgType = MsgType.REQUEST;
			pieceIndex = splitFile.getRequestPieceIndex(connection);
			if(pieceIndex!=Integer.MIN_VALUE)
				connection.addRequestedPiece(pieceIndex);
			break;
		case INTERESTED:
			// add to interested connections
			LoggerUtil.getLoggerInstance().logReceivedInterestedMessage(getTime(), peerProcess.getPeerId(),connection.getRemotePeerId());
			connection.addInterestedConnection();
			responseMsgType = null;
			break;
		case NOTINTERESTED:
			// add to not interested connections
			LoggerUtil.getLoggerInstance().logReceivedNotInterestedMessage(getTime(), peerProcess.getPeerId(),
			connection.getRemotePeerId());
			connection.addNotInterestedConnection();
			responseMsgType = null;
			break;
		case HAVE:
			// update peer bitset
			// send interested/not interested
			pieceIndex = ByteBuffer.wrap(p, 1, 4).getInt();
			LoggerUtil.getLoggerInstance().logReceivedHaveMessage(getTime(), peerProcess.getPeerId(), connection.getRemotePeerId(),
					pieceIndex);
			updateRemotePeerBitset(pieceIndex);
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
			setRemotePeerBitset(p);
			if(isInterested()) {
				responseMsgType= MsgType.INTERESTED;
			}
			else {
				responseMsgType=MsgType.NOTINTERESTED;
			}
			break;
		case REQUEST:
			// send requested piece
			PriorityQueue<Connection> queue=peermgr.getpreferredneighbours();
			if(queue==null || queue.isEmpty()|| (!queue.isEmpty() && queue.contains(connection))) {
				responseMsgType = MsgType.PIECE;
				byte[] content = new byte[4];
				System.arraycopy(p, 1, content, 0, 4);
				pieceIndex = ByteBuffer.wrap(content).getInt();
				if (pieceIndex == Integer.MIN_VALUE) {
					responseMsgType = null;
				}
			}
			else {
				responseMsgType=null;
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
			LoggerUtil.getLoggerInstance().logDownloadedPiece(getTime(), peerProcess.getPeerId(), connection.getRemotePeerId(),
					pieceIndex, splitFile.getReceivedFileSize());
			responseMsgType = MsgType.REQUEST;
			connection.sendHavetoAll(pieceIndex);
			connection.removeRequestedPiece(pieceIndex);
			pieceIndex = splitFile.getRequestPieceIndex(connection);
			if(pieceIndex!=Integer.MIN_VALUE) {
				connection.addRequestedPiece(pieceIndex);
			}
				
			else {
				responseMsgType = MsgType.NOTINTERESTED;
			}
			break;
		case HANDSHAKE:
			remotePeerId = Handshake.get_Id(p);
			connection.setPeerId(remotePeerId);
			connection.addAllConnections();
			if (!getUploadHandshake()) {
				setUploadHandshake();
				LoggerUtil.getLoggerInstance().logTcpConnectionFrom(host.getPeerInfo().getPeerId(), remotePeerId);
				payloadProcess.addMessagetoQueue(new Object[] { connection, MsgType.HANDSHAKE, Integer.MIN_VALUE });
			}
			if (splitFile.hasAnyPieces()) {
				responseMsgType = MsgType.BITFIELD;
			}
			break;
		}
		if (null != responseMsgType) {

			payloadProcess.addMessagetoQueue(new Object[] { connection, responseMsgType, pieceIndex });
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
	
	public synchronized void setRemotePeerBitset(byte[] remoteBitSet) {
		for (int i = 1; i < remoteBitSet.length; i++) {
			if (remoteBitSet[i] == 1) {
				peerBitset.set(i - 1);
			}
		}
		//if the peerbitset values are 1 for all pieces it means all pieces are received
		if (peerBitset.cardinality() == Common.getNumberOfPieces()) {
			peerHasFile = true;
			PeerManager.getPeerManagerInstance().addToFullFileList(remotePeerId);
		}
	}
	
	//updatePeerBitset and setPeerbitset has partial code same
	public synchronized void updateRemotePeerBitset(int newPiecevalue) {
		peerBitset.set(newPiecevalue);
		if (peerBitset.cardinality() == Common.getNumberOfPieces()) {
			PeerManager.getPeerManagerInstance().addToFullFileList(remotePeerId);
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
