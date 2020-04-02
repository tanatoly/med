package com.rafael.med.common;
/*
 *
 */


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




public class Datagram
{
	public interface Listener
	{
		public void onIncomingMessage(ByteBuffer message,InetSocketAddress source) throws Exception;
	}

	private static final Logger log 			= LogManager.getLogger();

	private ByteBuffer				inBuffer;


	private AtomicBoolean			isRunning;
	private DatagramChannel 		channel;
	private ExecutorService 		receiverExecutor;
	public InetSocketAddress 		localAddress;
	public InetSocketAddress 		remoteAddress;
	public NetworkInterface 		networkInterface;
	public InetAddress 				multicastAddress;
	private Listener 				listener;

	private MembershipKey membershipKey;




	public Datagram(int inCapacity, ByteOrder inByteOrder,NetworkInterface networkInterface,InetSocketAddress localAddress,InetSocketAddress remoteAddress, InetAddress multicastAddress)
	{
		this.isRunning 				= new AtomicBoolean(false);
		this.networkInterface		= networkInterface;
		this.localAddress			= localAddress;
		this.remoteAddress			= remoteAddress;
		this.multicastAddress		= multicastAddress;

		if(inCapacity > 0)
		{
			this.inBuffer   			= ByteBuffer.allocate(inCapacity).order(inByteOrder);
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
			channel = DatagramChannel.open(StandardProtocolFamily.INET);
			channel.configureBlocking(true);
			channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			channel.bind(localAddress);
			channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 255);

			if(multicastAddress != null && multicastAddress.isMulticastAddress())
			{
				channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
				channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
				membershipKey = channel.join(multicastAddress, networkInterface);
			}

			if(inBuffer != null)
			{
				receiverExecutor = Executors.newSingleThreadExecutor(new StandradThreadFactory(receiverThreadName + "-" + localAddress, true, Thread.MAX_PRIORITY));
				receiverExecutor.execute(new Runnable()
				{
					@Override
					public void run()
					{
						while(isRunning.get())
						{
							try
							{
								inBuffer.clear();
								InetSocketAddress remoteAddress = (InetSocketAddress) channel.receive(inBuffer);
								if (inBuffer.position() > 0)
								{
									inBuffer.flip();
								}

								int limit = inBuffer.limit();
								if(log.isTraceEnabled()){log.trace(toHexFormatString("RECEIVED", inBuffer));}
								listener.onIncomingMessage(inBuffer,remoteAddress);

								inBuffer.limit(limit);
								inBuffer.position(limit);// always go to state that all message read.
							}
							catch(Exception e)
							{
								log.debug("FAILED RECEIVE - ",e);
							}
						}
					}
				});
			}
		}
	}



	public void close()
	{
		try
		{
			if(membershipKey != null)
			{
				log.debug(" in close -> membershipKey = {}",membershipKey);
				membershipKey.drop();
				log.debug(" in close -> membershipKey = {} dropped",membershipKey);
			}

			isRunning.set(false);
			if(channel != null)
			{
				channel.close();
				log.debug(" in close -> channel = {} closed",channel);
			}
			if(receiverExecutor != null)
			{
				receiverExecutor.shutdownNow();
				log.debug(" in close -> receiverExecutor = {} stopped",receiverExecutor);
			}
		}
		catch (Throwable e)
		{
			log.debug("FAILED CLOSE - ",e);
		}
	}



	public int send(ByteBuffer buffer, InetSocketAddress target) throws Exception
	{
		int sendBytes = 0;
		if(isRunning.get())
		{
			if (buffer.position() > 0)
			{
				buffer.flip();
			}
			int limit = buffer.limit();

			if(target == null)
			{
				target = remoteAddress;
			}

			sendBytes =  channel.send(buffer,target);

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

	@Override
	public String toString()
	{
		return "Datagram [" + localAddress + " -> " + remoteAddress + "]";
	}
	

//**************************************** static methods **********************************************//

	public static String toSocketAddressToString(InetSocketAddress socketAddress)
	{
		StringBuilder builder = new StringBuilder();
		String hostString = socketAddress.getAddress().getHostAddress();
		builder.append(hostString);
		int port = socketAddress.getPort();
		if (port > 0)
		{
			builder.append(":").append(socketAddress.getPort());
		}
		return builder.toString();
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

	private static final class StandradThreadFactory implements ThreadFactory
	{
		protected final String 			namePrefix;
		protected final boolean 		isDaemon;
		protected final int 			priority;

		private StandradThreadFactory(String poolName, boolean isDaemon, int priority)
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
}
