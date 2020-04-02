package com.rafael.med.ans;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.rafael.med.common.Datagram;
import com.rafael.med.common.LatencyMeasure;



public class ChannelData implements Datagram.Listener
{
	public enum SendType
	{
		BY_CLICK,PERIODIC;
	}

	public enum Type
	{
		RECEIVING_ONLY,SENDING_ONLY, RECEIVEING_AND_SENDING;
	}


	public static final class Statistic
	{

	}
	private static final Logger log = LogManager.getLogger();

	public String 				name;
	public String 				inCapacity;
	public ByteOrder 			byteOrder;
	public NetworkInterface 	networkInterface;
	public InetAddress 			localHost;
	public String 				localPort;
	public String 				multicast;
	public String 				remoteHost;
	public String 				remotePort;

	public String 				dataAmount;
	public String 				dataBytes;
	public SendType 			sendType;
	public String 				sendPeriodInms;

	private Datagram 			datagram;
	public InetSocketAddress	remoteAddress;
	private ByteBuffer 			outBuffer;
	private ScheduledExecutorService 			sender;

	public ChannelView 			view;

	public AtomicLong			txCount = new AtomicLong();
	public AtomicLong			rxCount = new AtomicLong();
	public AtomicLong 			httpAvr	= new AtomicLong();
	public AtomicLong 			httpSum	= new AtomicLong();
	private long sendPeriodInmsLong;
	public InetAddress multicastAddress;
	public Type type;
	
	

	private LatencyMeasure latencyMeasure;
	private long lastMessage = -1;

	private InetSocketAddress localAddress;

	public ChannelData(String name, String inCapacity, ByteOrder byteOrder, NetworkInterface networkInterface, InetAddress localHost, String localPort,Type type, String multicast, String remoteHost, String remotePort, String dataAmount, String dataBytes, SendType sendType, String sendPeriodInms, String httpUrl, String httpProxy,String userId)
	{

		this.name 				= name;
		this.inCapacity 		= inCapacity;
		this.byteOrder 			= byteOrder;
		this.networkInterface 	= networkInterface;
		this.localHost 			= localHost;
		this.localPort 			= localPort;
		this.type				= type;
		this.multicast 			= multicast;

		this.remoteHost 		= remoteHost;
		this.remotePort 		= remotePort;

		this.dataAmount 		= dataAmount;
		this.dataBytes 			= dataBytes;
		this.sendType 			= sendType;
		this.sendPeriodInms 	= sendPeriodInms;
		
		if(log.isDebugEnabled())
		{
			latencyMeasure = new LatencyMeasure(log, "Received", 10_000);
		}
	}


	public void init()
	{
		try
		{

			if(StringUtils.isNotBlank(localPort))
			{
				this.localAddress	= new InetSocketAddress(localHost, Integer.parseInt(localPort));
			}
			else
			{
				this.localAddress	= new InetSocketAddress(localHost, 0);
			}
			if(sendType == SendType.PERIODIC)
			{
				sendPeriodInmsLong = Long.parseLong(sendPeriodInms);
			}
			else
			{
				sendPeriodInmsLong = 0;
			}

			if(datagram != null)
			{
				datagram.close();
				datagram = null;
			}

			if(StringUtils.isNotBlank(multicast))
			{
				multicastAddress  	= InetAddress.getByName(multicast);
			}
			if(type == Type.SENDING_ONLY)
			{

				int remotePortInt = Integer.parseInt(remotePort);
				remoteAddress = new InetSocketAddress(remoteHost, remotePortInt);
				int dataAmountInt = Integer.parseInt(dataAmount);
				outBuffer = ByteBuffer.allocate(dataAmountInt).order(byteOrder);
				Random random = new Random();
				random.nextBytes(outBuffer.array());
				this.datagram					= new Datagram(0, byteOrder, networkInterface, localAddress, null,null);

			}
			else if(type == Type.RECEIVING_ONLY)
			{
				int inCapacityInt				= Integer.parseInt(inCapacity);
				this.datagram					= new Datagram(inCapacityInt, byteOrder, networkInterface, localAddress,null, multicastAddress);
			}
			else if(type == Type.RECEIVEING_AND_SENDING)
			{
				int remotePortInt = Integer.parseInt(remotePort);
				remoteAddress = new InetSocketAddress(remoteHost, remotePortInt);
				if(sendType == SendType.PERIODIC)
				{
					sendPeriodInmsLong = Long.parseLong(sendPeriodInms);
				}
				else
				{
					sendPeriodInmsLong = 0;
				}

				int dataAmountInt = Integer.parseInt(dataAmount);
				outBuffer = ByteBuffer.allocate(dataAmountInt).order(byteOrder);
				Random random = new Random();
				random.nextBytes(outBuffer.array());

				int inCapacityInt				= Integer.parseInt(inCapacity);
				this.datagram					= new Datagram(inCapacityInt, byteOrder, networkInterface, localAddress,null, multicastAddress);

			}

			this.datagram.setListener(this);




			if(StringUtils.isNotBlank(dataBytes) && outBuffer != null)
			{
				String[] pairs = dataBytes.split(",");
				for (String pair : pairs)
				{
					String[] split = pair.split(":");
					int index = Integer.parseInt(split[0]);
					byte value = Byte.parseByte(split[1]);
					outBuffer.put(index, value);
				}
			}

			this.datagram.open("receiver-" + name);

		}
		catch (Exception e)
		{
			log.debug("FAILED INIT CHANNEL - ", e);
		}
	}

	public void send()
	{

		if(outBuffer != null && remoteAddress != null)
		{
			try
			{
				datagram.send(outBuffer, remoteAddress);
				txCount.incrementAndGet();
			}
			catch (Exception e)
			{
				log.debug("FAILED SEND - ", e);
			}

		}
	}


	@Override
	public void onIncomingMessage(ByteBuffer message, InetSocketAddress source) throws Exception
	{
		if(log.isDebugEnabled())
		{
			long now = System.nanoTime();
			if(lastMessage > 0 )
			{
				long delta = now - lastMessage;
				latencyMeasure.measureDelta(delta);
			}
			lastMessage = now;
		}

		rxCount.incrementAndGet();
	}


	public void startSend()
	{
		if(sender != null)
		{
			sender.shutdownNow();
			sender = null;
		}

		sender = Executors.newSingleThreadScheduledExecutor();

		sender.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				send();
			}
		}, sendPeriodInmsLong, sendPeriodInmsLong, TimeUnit.MILLISECONDS);

	}


	public void stopSend()
	{
		if(sender != null)
		{
			sender.shutdown();
			sender = null;
		}
	}

	public void destroy()
	{
		if(sender != null)
		{
			sender.shutdownNow();
		}
		if(datagram != null)
		{
			datagram.close();
		}
	}


	public Element toXmlElement(Document document,Element channelElement)
	{
		channelElement.setAttribute("name", name);
		channelElement.setAttribute("networkInterface", networkInterface.getName());
		channelElement.setAttribute("localhost", localHost.getHostAddress());
		channelElement.setAttribute("localport", localPort);
		channelElement.setAttribute("byteorder", byteOrder.toString());
		channelElement.setAttribute("type", type.name().toLowerCase());
		channelElement.setAttribute("incapacity", inCapacity);
		channelElement.setAttribute("remotehost", remoteHost);
		channelElement.setAttribute("remotePort", remotePort);
		channelElement.setAttribute("multicast", multicast);
		if(sendType != null)
		{
			channelElement.setAttribute("sendType", sendType.name().toLowerCase());
		}
		else
		{
			channelElement.setAttribute("sendType", "");
		}

		channelElement.setAttribute("sendPeriodInms", sendPeriodInms);
		channelElement.setAttribute("dataAmount", dataAmount);
		channelElement.setAttribute("dataBytes", dataBytes);
		return channelElement;
	}

	public static ChannelData fromXmlElement(Element channelElement) throws Exception
	{

		String name 							= channelElement.getAttribute("name");
		NetworkInterface networkInterface 		= NetworkInterface.getByName(channelElement.getAttribute("networkInterface"));
		InetAddress localHost 					= InetAddress.getByName(channelElement.getAttribute("localhost"));
		String localPort 						= channelElement.getAttribute("localport");
		ByteOrder byteOrder 					= (channelElement.getAttribute("byteorder").equals(ByteOrder.BIG_ENDIAN.toString()) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		Type type 								= Type.valueOf(channelElement.getAttribute("type").toUpperCase());
		String inCapacity 						= channelElement.getAttribute("incapacity");
		String remoteHost 						= channelElement.getAttribute("remotehost");
		String remotePort 						= channelElement.getAttribute("remotePort");
		String multicast 						= channelElement.getAttribute("multicast");
		String attribute = channelElement.getAttribute("sendType");
		SendType sendType = null;
		if(StringUtils.isBlank(attribute))
		{
			sendType = null;
		}
		else
		{
			sendType 						= SendType.valueOf(attribute.toUpperCase());
		}
		String sendPeriodInms 					= channelElement.getAttribute("sendPeriodInms");
		String dataAmount 						= channelElement.getAttribute("dataAmount");
		String dataBytes 						= channelElement.getAttribute("dataBytes");
		
		String httpUrl 						= channelElement.getAttribute("httpUrl");
		String httpProxy 					= channelElement.getAttribute("httpProxy");
		
		String userId 					= channelElement.getAttribute("userId");
		
		return new ChannelData(name, inCapacity, byteOrder, networkInterface, localHost, localPort, type, multicast, remoteHost, remotePort, dataAmount, dataBytes, sendType, sendPeriodInms, httpUrl, httpProxy,userId);
	}
}
