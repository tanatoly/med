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


public class BackboneController extends Controller
{
	private static final Logger log = LogManager.getLogger();
	
	private InetSocketAddress multicastAddress;
	private InetSocketAddress localAddress;
	private Datagram datagram;

	public BackboneController(IN inRing, Configuration configuration, ByteOrder byteOrder, int maxBufferSize, String backboneLocalHost, String backboneLocalPort, String backboneMulticast, MessageHandler messageHandler, AtomicBoolean isRunning) 
	{
		super(log, inRing, configuration, byteOrder, maxBufferSize, messageHandler, "backbone",isRunning);
		
		localAddress 		= new InetSocketAddress(backboneLocalHost, Integer.parseInt(backboneLocalPort));
		multicastAddress 	= new InetSocketAddress(backboneMulticast, Integer.parseInt(backboneLocalPort));
	
		NetworkInterface networkInterface = Utilities.getNetworkInterfaceByAddress(backboneLocalHost);
		
		datagram = new Datagram(maxBufferSize, byteOrder, networkInterface, localAddress, null, multicastAddress.getAddress());
		datagram.setListener(this);
		
	}

	@Override
	protected Object[] sendOutgoingMessage(ByteBuffer buffer, Object target, Object debugInfo) throws Exception
	{
		if(target == null)
		{
			target = multicastAddress;
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
		return new ByteBufferRing.OUT(1024, maxBufferSize, byteOrder,false, Threads.newSingleThreadExecutor("backbone-out-ring"), false,waitStrategyType, this);
	}

	@Override
	protected void start0() throws Exception
	{
		datagram.open("backbone");
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
