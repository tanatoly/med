package com.rafael.med.common;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.Logger;

import com.lmax.disruptor.EventHandler;
import com.rafael.med.common.Datagram.Listener;



public abstract class Controller implements Listener,EventHandler<ByteBufferRing.ByteBufferCell>
{
	protected Logger log;
	protected ByteBufferRing.IN inRing;
	protected ByteBufferRing.OUT outRing;
	protected Configuration configuraion;
	protected ByteOrder byteOrder;
	protected int maxBufferSize;
	protected MessageHandler handler;

	private long validatedFailed		= 0;
	private long successCycleCounter 	= 0;
	private long failedCycleCounter 	= 0;
	private String debugName;
	private AtomicBoolean isRunning;

	protected Controller(Logger log, ByteBufferRing.IN inRing,Configuration configuraion, ByteOrder byteOrder, int maxBufferSize, MessageHandler handler, String debugName, AtomicBoolean isRunning)
	{
		this.log 				= log;
		this.inRing 			= inRing;
		this.configuraion		= configuraion;
		this.byteOrder			= byteOrder;
		this.maxBufferSize		= maxBufferSize;
		this.handler			= handler;
		this.outRing 			= createOutRing();
		this.debugName			= debugName;
		this.isRunning			= isRunning;
	}

	public void start() throws Exception
	{
		long begin = System.nanoTime();
		try
		{
			start0();
		}
		catch (Throwable e)
		{
			throw new Exception("FAILED START - ",e);
		}
		if(log.isTraceEnabled()){log.trace("start finished in {} ns",(System.nanoTime() - begin));}
	}

	public void stop()
	{
		long begin = System.nanoTime();
		try
		{
			stop0();
		}
		catch (Throwable e)
		{
			log.error("FAILED STOP - ",e);
		}
		if(log.isTraceEnabled()){log.trace("stop finished in {} ns",(System.nanoTime() - begin));}
	}


	@Override
	public void onIncomingMessage(ByteBuffer buffer, InetSocketAddress source) throws Exception
	{
		if(isRunning.get())
		{
			log.trace("got message buffer = {} , source = {} ",buffer,source);
			int currentLimit = buffer.limit();
			try
			{
				if(validateIncomingMessage(buffer, source))
				{
					inRing.push(buffer, source, getHandler());
				}
			}
			catch (Throwable e)
			{
				buffer.position(currentLimit);
				validatedFailed++;
				log.error("FAILED HANDLE INCOMING MESSAGE FROM SOURCE {} , validatedFailedCount = {} - ,",source,validatedFailed,e);
			}
		}
	}

	public void toOutgoingMessage(MessageBuilder messageBuilder,Object target,Object... params)
	{
		if(isRunning.get())
		{
			outRing.push(messageBuilder, target, params);
		}
	}


	@Override
	public void onEvent(ByteBufferRing.ByteBufferCell event, long sequence, boolean endOfBatch) throws Exception
	{
		ByteBuffer buffer 		= event.buffer;
		Object	target	  		= event.arg1;
		Object	debugInfo	  	= event.arg2;
		try
		{
			if(buffer.position() > 0)
			{
				buffer.flip();
			}
			Object[] params = sendOutgoingMessage(buffer,target, debugInfo);
			if(log.isDebugEnabled())
			{
				log.debug(String.format("SENT %1$8s TO %2$16s  %3$30s BYTES %4$10d", debugName, params[1] , debugInfo , params[2]));
			}
			log.trace("message has been sent  {} -> {}, bytes = {}.",params[0],params[1],params[2]);
			successCycleCounter++;
		}
		catch (Throwable e)
		{
			failedCycleCounter++;
			log.error("FAILED SENDING MESSAGE = {} TO TARGET = {} , successCycleCounter = {} , failedCycleCounter = {} ",buffer,target,successCycleCounter,failedCycleCounter,e);
		}
	}

	public  MessageHandler getHandler()
	{
		return handler;
	}


	protected abstract Object[] sendOutgoingMessage(ByteBuffer buffer,Object target, Object debugInfo) throws Exception;
	protected abstract boolean validateIncomingMessage(ByteBuffer buffer,Object source) throws Exception;
	protected abstract ByteBufferRing.OUT createOutRing();
	protected abstract void start0() throws Exception;
	protected abstract void stop0() throws Exception;

}


