package com.rafael.med.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ClientSocket
{	
	public static final class StandradThreadFactory implements ThreadFactory
	{
		protected final String 			namePrefix;
		protected final boolean 		isDaemon;
		protected final int 			priority;

		public StandradThreadFactory(String poolName, boolean isDaemon, int priority)
		{
			this.isDaemon 	= isDaemon;
			this.namePrefix = poolName;
			this.priority 	= priority;
		}

		@Override
		public Thread newThread(Runnable runnable)
		{
			Thread thread = new Thread(runnable);
			thread.setPriority(priority);
			thread.setName(namePrefix);
			thread.setDaemon(isDaemon);
			return thread;
		}
	}
	
	public enum ConnectionStatus
	{
		CONNECTED,NOT_CONNECTED;
	}
	
	public interface Listener
	{
		public void onIncomingMessage(ByteBuffer message,InetSocketAddress source) throws Exception;
		public void onConnectionStatusChanged(ConnectionStatus connectionStatus);
	}
	
	private static final class StreamParseParams
	{
		public byte[] startSync;
		public int headerLength;
		public int lengthOffset;
		public int fieldLength;
		public boolean isHeaderIncludeInLength;
		public int messageLength;
		public StreamParseMethod method;
		
		public StreamParseParams(byte[] startSync, int headerLength, int lengthOffset, int fieldLength, boolean isHeaderIncludeInLength, int messageLength, StreamParseMethod streamParseMethod)
		{
			this.startSync = startSync;
			this.headerLength = headerLength;
			this.lengthOffset = lengthOffset;
			this.fieldLength = fieldLength;
			this.isHeaderIncludeInLength = isHeaderIncludeInLength;
			this.messageLength = messageLength;
			this.method = streamParseMethod;
		}
	}
	
	
	public enum StreamParseMethod
	{
		HEADER
		{
			@Override
			public void parseStream(Logger log, ByteBuffer buffer, StreamParseParams params, Listener listener, InetSocketAddress streamSource)
			{
				int lastPosition = buffer.position();
				while (lastPosition >= params.headerLength) // has enough bytes for parse
				{
					boolean isSyncFound = true; // for stream without sync aka TCP
					if (params.startSync != null && params.startSync.length > 0)
					{
						buffer.flip();
						isSyncFound = isSyncFoundAndDiscardGarbageBefore(buffer, params.startSync);
						lastPosition = buffer.position();
					}
					
					if (isSyncFound && lastPosition >= params.headerLength)
					{
						buffer.flip();
						int messageLength = getMessageLength(buffer, params.fieldLength, params.lengthOffset);
						if (!params.isHeaderIncludeInLength)
						{
							messageLength = messageLength + params.headerLength;
						}
						
						if (messageLength < params.headerLength)
						{
							throw new IllegalStateException("message length = " + messageLength + "  < header length = " + params.headerLength);
						}
						
						lastPosition = cutAndCompact(log, buffer, messageLength, lastPosition, listener, streamSource);
						if (lastPosition == -1)
						{
							return;
						}
					}
				}
			}
		},
		
		FIX_LENGTH
		{
			@Override
			public void parseStream(Logger log, ByteBuffer buffer, StreamParseParams params, Listener listener, InetSocketAddress streamSource)
			{
				int lastPosition = buffer.position();
				while (lastPosition >= params.messageLength) // has enough bytes for parse
				{
					boolean isSyncFound = true; // for stream without sync aka TCP
					if (params.startSync != null && params.startSync.length > 0)
					{
						buffer.flip();
						isSyncFound = isSyncFoundAndDiscardGarbageBefore(buffer, params.startSync);
						lastPosition = buffer.position();
					}
					
					if (isSyncFound && lastPosition >= params.messageLength)
					{
						buffer.flip();
						lastPosition = cutAndCompact(log, buffer, params.messageLength, lastPosition, listener, streamSource);
						if (lastPosition == -1)
						{
							return;
						}
					}
				}
			}
		};
		public abstract void parseStream(Logger log, ByteBuffer buffer, StreamParseParams streamParseParams, Listener listener, InetSocketAddress streamSource);
		
		
		
		private static int getMessageLength(ByteBuffer buffer, int fieldLength, int lengthOffset)
		{
			long frameLength;
			switch (fieldLength)
			{
			case 1:
				frameLength = buffer.get(lengthOffset) & 0xFF;
				break;
			case 2:
				frameLength = buffer.getShort(lengthOffset) & 0xFFFF;
				break;
			case 4:
				frameLength = buffer.getInt(lengthOffset) & 0xFFFFFFFFL;
				break;
			default:
				throw new IllegalArgumentException("unsupported lengthFieldLength: " + fieldLength + " (expected:1, 2, 4)");
			}
			return (int) frameLength;
		}
		
		private static boolean isSyncFoundAndDiscardGarbageBefore(ByteBuffer buffer, byte[] startSync)
		{
			boolean isSyncFound = false;
			while (!isSyncFound && buffer.remaining() >= startSync.length)
			{
				byte current = buffer.get();
				if (current == startSync[0])
				{
					isSyncFound = true;
					for (int i = 1; i < startSync.length && isSyncFound; i++)
					{
						int checkPosition = buffer.position() + (i - 1);
						if (checkPosition < buffer.limit())
						{
							current = buffer.get(checkPosition);
							isSyncFound = current == startSync[i];
						}
					}
				}
			}
			if (buffer.position() > 0)
			{
				buffer.position(buffer.position() - 1);
				buffer.compact();
			}
			return isSyncFound;
		}
		
		private static int cutAndCompact(Logger log, ByteBuffer buffer, int messageLength, int lastPosition, Listener listener, InetSocketAddress streamSource)
		{
			if (log.isTraceEnabled())
			{
				log.trace("cutAndCompact : lastPosition = {}, messageLength = {} ,buffer = {}", lastPosition, messageLength, buffer);
			}
			if (lastPosition >= messageLength)
			{
				buffer.limit(messageLength);
				handleReceivedMessage(buffer, listener, streamSource);
				buffer.position(messageLength);
				buffer.limit(lastPosition);
				buffer.compact();
				return buffer.position();
			}
			else
			{
				buffer.position(lastPosition);
				buffer.limit(buffer.capacity());
				return -1;
			}
		}
		
		private static void handleReceivedMessage(ByteBuffer buffer, Listener listener, InetSocketAddress source)
		{
			try
			{
				if (buffer.position() > 0)
				{
					buffer.flip();
				}	
				int limit = buffer.limit();

				if(log.isTraceEnabled()){log.trace(toHexFormatString("RECEIVED", buffer));}
				listener.onIncomingMessage(buffer,source);

				buffer.position(limit);// always go to state that all message read.
			}
			catch (Throwable e)
			{
				log.error("FAILED RECEIVE : buffer = {} ,source = {} - ",buffer,source,e);
			}
		}
	}
	
	private static final Logger log 			= LogManager.getLogger();

	private AtomicBoolean		isRunning;
	private ByteBuffer 			inBuffer;
	
	private boolean 			previousIsConnected;
	private boolean 			currentIsConnected;
	private long 				reconnectDelay;

	private InetSocketAddress 	localAddress;
	private InetSocketAddress 	remoteAddress;
	
	private SocketChannel 		clientChannel;
	private ExecutorService 	clientThread;

	private Listener 			listener;
	private StreamParseParams	streamParseParams;
	
	
	public ClientSocket(int inCapacity, ByteOrder inByteOrder,InetSocketAddress localAddress, InetSocketAddress remoteAddress, long reconnectDelay, byte[] startSync, int messageLength,int headerLength, int lengthOffset, int fieldLength,boolean isHeaderIncludeInLength, StreamParseMethod streamParseMethod)
	{
		this.isRunning 				= new AtomicBoolean(false);
		
		this.localAddress 			= localAddress;
		this.remoteAddress 			= remoteAddress;
		
		this.reconnectDelay 		= reconnectDelay;
		this.previousIsConnected 	= false;
		this.currentIsConnected  	= false;
		
		this.streamParseParams		= new StreamParseParams(startSync, headerLength, lengthOffset, fieldLength, isHeaderIncludeInLength, messageLength, streamParseMethod);
		
		if(inCapacity > 0)
		{
			this.inBuffer   		= ByteBuffer.allocate(inCapacity).order(inByteOrder);
		}
	}
	
	public void setListener(Listener listener)
	{
		this.listener = listener;
	}

	
	public void open(String receiverThreadName) throws Exception
	{
		if(isRunning.compareAndSet(false, true))
		{
		
			clientThread = Executors.newSingleThreadExecutor(new StandradThreadFactory(receiverThreadName + "-" + localAddress, false, Thread.MAX_PRIORITY));
			clientThread.execute(new Runnable()
			{
				@Override
				public void run()
				{
					while (isRunning.get())
					{
						previousIsConnected = currentIsConnected;
						if(clientChannel == null)
						{
							try
							{
								clientChannel = SocketChannel.open();
								clientChannel.bind(localAddress);
								clientChannel.configureBlocking(true);
								clientChannel.connect(remoteAddress);
								currentIsConnected = clientChannel.isConnected();
							}
							catch (IOException e)
							{
								log.error("CLIENT CHANNEL ON ADDRESS {} FAILED IN START OPERATION - {}", localAddress, oneLineException(e).toUpperCase());
								closeAndWait();
								currentIsConnected = false;
							}
						}
						else
						{
							currentIsConnected = clientChannel.isConnected();
							if(currentIsConnected)
							{
								try
								{
									read();
								}
								catch (Exception e)
								{
									log.warn("CLIENT CHANNEL ON ADDRESS {} FAILED IN READ OPERATION - ",localAddress,oneLineException(e));
									closeAndWait();
									currentIsConnected = false;
								}
							}
							else
							{
								log.warn("CLIENT CHANNEL ON ADDRESS {} NOT CONNECTED YET",localAddress);
								closeAndWait();
							}	
						}
						if(previousIsConnected != currentIsConnected)
						{
							ConnectionStatus connectionStatus = null;
							try
							{
								connectionStatus = currentIsConnected ? ConnectionStatus.CONNECTED : ConnectionStatus.NOT_CONNECTED;
								listener.onConnectionStatusChanged(connectionStatus);
							}
							catch (Throwable e)
							{
								log.error("FAILED CONNECTION STATUS EVENT : connectionStatus = {} ",connectionStatus,e);
							}
						}
					}
				}
			
			});
		}
	}

	private void read() throws Exception
	{
		System.out.println("----- before read");
		int result = clientChannel.read(inBuffer);
		System.out.println("----- after read");
		if(result == -1)
		{
			throw new Exception("end of stream");
		}
		if(log.isTraceEnabled()){log.trace("read bytes = {} inBuffer = {}",result,inBuffer);}

		streamParseParams.method.parseStream(log, inBuffer, streamParseParams, listener, remoteAddress);
	}
	
	private void closeAndWait()
	{
		if(clientChannel != null)
		{
			try
			{
				clientChannel.close();
			}
			catch (IOException e)
			{
				log.error("failed close client channel on address {} - {}", localAddress, oneLineException(e).toUpperCase());
			}
			clientChannel = null;
		}
		try{Thread.sleep(reconnectDelay);}catch (InterruptedException e1){}
		log.info("CLIENT CHANNEL ON ADDRESS {} RECONNECT TO {}", localAddress,remoteAddress);
	}
	
	public boolean isConnected()
	{
		return clientChannel != null && clientChannel.isConnected();
	}
	
	public int send(ByteBuffer buffer) throws Exception
	{
		int sendBytes = 0;
		if(isRunning.get() && clientChannel != null)
		{
			if (buffer.position() > 0)
			{
				buffer.flip();
			}
			int limit = buffer.limit();
			sendBytes = clientChannel.write(buffer);
			
			if(sendBytes < limit)
			{
				throw new Exception("failed send all bytes - sendBytes = " + sendBytes + " bufferLimit = " + limit);
			}
			if(log.isTraceEnabled())
			{
				int oldPosition = buffer.position();
				int oldLimit = buffer.limit();

				buffer.position(sendBytes);

				log.trace(toHexFormatString("SEND",(ByteBuffer) buffer.flip()));
				buffer.position(oldPosition);
				buffer.limit(oldLimit);
			}
		}
		return sendBytes;
	}
	
	public void close() throws Exception
	{
		isRunning.set(false);
		if(clientChannel != null)
		{
			clientChannel.close();
			clientChannel = null;
		}
		if(clientThread != null)
		{
			clientThread.shutdownNow();
		}
		listener.onConnectionStatusChanged(ConnectionStatus.NOT_CONNECTED);
	}
	
	private static final String NEWLINE = String.format("%n");
	private static final String[] BYTE2HEX = new String[256];
	private static final String[] HEXPADDING = new String[16];
	private static final String[] BYTEPADDING = new String[16];
	private static final char[] BYTE2CHAR = new char[256];

	static
	{
		int i;

		// Generate the lookup table for byte-to-hex-dump conversion
		for (i = 0; i < 10; i++)
		{
			StringBuilder buf = new StringBuilder(3);
			buf.append(" 0");
			buf.append(i);
			BYTE2HEX[i] = buf.toString();
		}
		for (; i < 16; i++)
		{
			StringBuilder buf = new StringBuilder(3);
			buf.append(" 0");
			buf.append((char) ('a' + i - 10));
			BYTE2HEX[i] = buf.toString();
		}
		for (; i < BYTE2HEX.length; i++)
		{
			StringBuilder buf = new StringBuilder(3);
			buf.append(' ');
			buf.append(Integer.toHexString(i));
			BYTE2HEX[i] = buf.toString();
		}

		// Generate the lookup table for hex dump paddings
		for (i = 0; i < HEXPADDING.length; i++)
		{
			int padding = HEXPADDING.length - i;
			StringBuilder buf = new StringBuilder(padding * 3);
			for (int j = 0; j < padding; j++)
			{
				buf.append("   ");
			}
			HEXPADDING[i] = buf.toString();
		}

		// Generate the lookup table for byte dump paddings
		for (i = 0; i < BYTEPADDING.length; i++)
		{
			int padding = BYTEPADDING.length - i;
			StringBuilder buf = new StringBuilder(padding);
			for (int j = 0; j < padding; j++)
			{
				buf.append(' ');
			}
			BYTEPADDING[i] = buf.toString();
		}

		// Generate the lookup table for byte-to-char conversion
		for (i = 0; i < BYTE2CHAR.length; i++)
		{
			if (i <= 0x1f || i >= 0x7f)
			{
				BYTE2CHAR[i] = '.';
			}
			else
			{
				BYTE2CHAR[i] = (char) i;
			}
		}
	}


	public static String toHexFormatString(String eventName, ByteBuffer buffer)
	{
		int length = buffer.remaining();
		int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
		StringBuilder dump = new StringBuilder(rows * 80 + eventName.length() + 16);

		dump.append(eventName).append(" (").append(length).append('B').append(')');
		dump.append(NEWLINE + "         +-------------------------------------------------+" + NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + NEWLINE + "+--------+-------------------------------------------------+----------------+");

		final int startIndex = buffer.position();
		final int endIndex = buffer.limit();

		int i;
		for (i = startIndex; i < endIndex; i++)
		{
			int relIdx = i - startIndex;
			int relIdxMod16 = relIdx & 15;
			if (relIdxMod16 == 0)
			{
				dump.append(NEWLINE);
				dump.append(Long.toHexString(relIdx & 0xFFFFFFFFL | 0x100000000L));
				dump.setCharAt(dump.length() - 9, '|');
				dump.append('|');
			}
			dump.append(BYTE2HEX[(short) (buffer.get(i) & 0xFF)]);
			if (relIdxMod16 == 15)
			{
				dump.append(" |");
				for (int j = i - 15; j <= i; j++)
				{
					dump.append(BYTE2CHAR[(short) (buffer.get(j) & 0xFF)]);
				}
				dump.append('|');
			}
		}
		if ((i - startIndex & 15) != 0)
		{
			int remainder = length & 15;
			dump.append(HEXPADDING[remainder]).append(" |");
			for (int j = i - remainder; j < i; j++)
			{
				dump.append(BYTE2CHAR[(short) (buffer.get(j) & 0xFF)]);
			}
			dump.append(BYTEPADDING[remainder]).append('|');
		}

		dump.append(NEWLINE + "+--------+-------------------------------------------------+----------------+");
		return dump.toString();
	}
	
	public static String oneLineException(Throwable throwable)
	{
		if (throwable != null)
		{
			StringBuilder builder = new StringBuilder(throwable.getClass().getCanonicalName());
			builder.append(" : ").append(throwable.getMessage());
			return builder.toString();
		}
		return "no throwable";
	}
}
