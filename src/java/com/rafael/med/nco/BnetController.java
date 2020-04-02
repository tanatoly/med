package com.rafael.med.nco;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.common.ByteBufferRing;
import com.rafael.med.common.Controller;
import com.rafael.med.common.Datagram;
import com.rafael.med.common.MessageHandler;
import com.rafael.med.common.Threads;
import com.rafael.med.common.Utilities;
import com.rafael.med.common.ByteBufferRing.IN;
import com.rafael.med.common.ByteBufferRing.WaitStrategyType;


public class BnetController extends Controller
{
	private static final Logger log = LogManager.getLogger();
	
	private Datagram datagram;

	private InetSocketAddress remoteAddress;
	private InetSocketAddress localAddress;

	

	protected BnetController(IN inRing, Configuration configuration, ByteOrder byteOrder, int maxBufferSize,String bnetLocalHost, String bnetLocalPort, String bnetRemoteHost, String bnetRemotePort, MessageHandler messageHandler, AtomicBoolean isRunning) throws Exception 
	{
		super(log, inRing, configuration, byteOrder, maxBufferSize, messageHandler, "bnet",isRunning);
		
		localAddress 	= new InetSocketAddress(bnetLocalHost, Integer.parseInt(bnetLocalPort));
		remoteAddress	= new InetSocketAddress(bnetRemoteHost, Integer.parseInt(bnetRemotePort));
		
		NetworkInterface networkInterface = Utilities.getNetworkInterfaceByAddress(bnetLocalHost);
		
		datagram = new Datagram(maxBufferSize, byteOrder, networkInterface, localAddress, remoteAddress, null);
		datagram.setListener(this);
	}

	@Override
	protected Object[] sendOutgoingMessage(ByteBuffer buffer, Object target, Object debugInfo) throws Exception
	{
		if(target == null)
		{
			target = remoteAddress;
		}
		int bytes = datagram.send(buffer, (InetSocketAddress) target);
		return new Object[]{localAddress,target,bytes};
	}

	@Override
	protected boolean validateIncomingMessage(ByteBuffer buffer, Object source) throws Exception
	{
		return true;
	}

	@Override
	protected ByteBufferRing.OUT createOutRing()
	{
		WaitStrategyType waitStrategyType = ByteBufferRing.waitStrategyByName(configuraion.getString("waitStrategy", WaitStrategyType.BLOCKING.name()));
		return new ByteBufferRing.OUT(16, maxBufferSize, byteOrder,false, Threads.newSingleThreadExecutor("bnet-out-ring"), false,waitStrategyType, this);
	}

	@Override
	protected void start0() throws Exception
	{
		datagram.open("bnet");
	}

	@Override
	protected void stop0() throws Exception
	{
		if(datagram != null)
		{
			datagram.close();
		}
	}
}
