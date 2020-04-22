package com.rafael.med;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestSender
{
	private static final class TestDatagram
	{
		private static final Logger log = LogManager.getLogger();
	
	    public static final int CAPACITY = 64000;
	    public static final int PORT = 28028;
	    public static final ByteOrder BIG_ENDIAN = ByteOrder.LITTLE_ENDIAN;

	 

	    private AtomicBoolean isRunning;
	    private DatagramChannel channel;
	    private InetSocketAddress localAddress;
	    private InetSocketAddress remoteAddress;
	
	    private TestDatagram()
	    {
	        this.isRunning          = new AtomicBoolean(false);
	        try
	        {
	            for(Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();)
	            {
	                NetworkInterface localInterface = e.nextElement();
	                if(!localInterface.isVirtual() && !localInterface.isLoopback() && localInterface.isUp())
	                {
	                    for (InterfaceAddress interfaceAddress : localInterface.getInterfaceAddresses())
	                    {
	                    	
	                        InetAddress address =  interfaceAddress.getAddress();
	                        if(address instanceof Inet4Address)
	                        {
	                        	localAddress = new InetSocketAddress(address, PORT);
	                        	remoteAddress = new InetSocketAddress(address, Datagram.PORT);
	                        }
	                    }
	                }
	            }
	            if(localAddress == null)
	            {
	                throw new Exception("not found interface up ");
	            }
	           log.info("LOCAL ADDRESS = {}",localAddress);
	        }
	        catch(Exception e)
	        {
	            log.error("FAILED FOUND LOCAL ADDRESS - ",e);
	        }

	    }

	    public void open() throws Exception
	    {
	        if(isRunning.compareAndSet(false,true))
	        {
	            channel = DatagramChannel.open();
	            channel.configureBlocking(true);
	            channel.setOption(StandardSocketOptions.SO_REUSEADDR,true);
	            channel.bind(localAddress);
	   
	        }
	    }

	    public void close()
	    {
	        try
	        {
	            isRunning.set(false);
	            if(channel != null)
	            {
	                channel.close();
	            }
	        }
	        catch (Exception e)
	        {
	        	log.error("FAILED CLOSE - ",e);
	        }
	    }

	    public int send(ByteBuffer buffer) throws Exception
	    {
	        int sendBytes = 0;
	        if(isRunning.get())
	        {
	            if(buffer.position() > 0)
	            {
	                buffer.flip();
	            }
	            int limit = buffer.limit();

	            sendBytes = channel.send(buffer,remoteAddress);
	            if(sendBytes < limit)
	            {
	                throw new Exception("failed send all bytes - sendbytes = " + sendBytes + " bufferLimit = " + limit);
	            }
	        }
	        return sendBytes;
	    }
	    
	    
	    
	    
	    
	}
	
	private static void addParam(ByteBuffer buffer ,  int id, int type, int value)
	{
		buffer.putShort((short) id);
		buffer.put((byte) type);
		if(type == 2)
		{
			buffer.putInt(value);
		}
		else
		{
			buffer.putFloat(value);
		}
	}
	
	private static void addChart1(ByteBuffer buffer)
	{
		addParam(buffer, 1001, 2, 0);
		addParam(buffer, 1002, 2, 10_000);
		addParam(buffer, 1003, 2, -50);
		addParam(buffer, 1004, 2, 50);
		addParam(buffer, 1005, 2, 100);
		addParam(buffer, 1006, 2, 1);
	}
	
	private static void addChart2(ByteBuffer buffer)
	{
		addParam(buffer, 2001, 2, 0);
		addParam(buffer, 2002, 2, 10_000);
		addParam(buffer, 2003, 2, 0);
		addParam(buffer, 2004, 2, 10);
		addParam(buffer, 2005, 2, 100);
		addParam(buffer, 2006, 2, 1);
	}
	
	
	public static void main(String[] args) throws Exception 
	{
		long count = 0;
		for (int i = 0; i < 1000; i++)
		{
			boolean is = count%10 == 0;
			System.out.println("count = " + count + " " + is);
			count++;
		}
		
		
		TestDatagram testDatagram = new TestDatagram();
		testDatagram.open();
		
		ByteBuffer outBuffer = ByteBuffer.allocate(TestDatagram.CAPACITY).order(TestDatagram.BIG_ENDIAN);
		
		while(true)
		{
			try 
			{
				outBuffer.clear();
				outBuffer.putInt(1001);
				outBuffer.put((byte) 2);
				
				String serial = "8888888";
				outBuffer.put((byte) serial.length());
				outBuffer.put(serial.getBytes());
				
				
				byte paramsSize = 12 + 2 + 2;
				outBuffer.put((byte) paramsSize);
				
				addChart1(outBuffer);
				addChart2(outBuffer);
				
				addParam(outBuffer, 1007, 2, RandomUtils.nextInt(0, 50));
				addParam(outBuffer, 2007, 2, RandomUtils.nextInt(0, 10));
				
				addParam(outBuffer, 4, 2, RandomUtils.nextInt(1000, 2000));
				addParam(outBuffer, 0, 2, RandomUtils.nextInt(100, 200));
					
			//	addParam(outBuffer, 1007, 2, 20);
					
					
//					
//					<wf id="1000" name="blyayayayaya"/>
//				 	
//				 	<wf-range id="1001" wfId="1000" isAxisX="true" isMin="true"/>
//				 	<wf-range id="1002" wfId="1000" isAxisX="true" isMin="false"/>
//				 	<wf-range id="1003" wfId="1000" isAxisX="false" isMin="true"/>
//				 	<wf-range id="1004" wfId="1000" isAxisX="false" isMin="false"/>
//				 	
//				 	<wf-step id="1005" wfId="1000" isAxisX="true"/>
//				 	<wf-step id="1006" wfId="1000" isAxisX="false"/>
//				 	
//				 	<wf-value id="1007" wfId="1000" />
//				 	
//				 	<!-- chart 2 -->
//				 	
//				 	<wf id="2000" name="tatatatatata"/>
//				 	
//				 	<wf-range id="1001" wfId="2000" isAxisX="true" isMin="true"/>
//				 	<wf-range id="2002" wfId="2000" isAxisX="true" isMin="false"/>
//				 	<wf-range id="2003" wfId="2000" isAxisX="false" isMin="true"/>
//				 	<wf-range id="2004" wfId="2000" isAxisX="false" isMin="false"/>
//				 	
//				 	<wf-step id="2005" wfId="2000" isAxisX="true"/>
//				 	<wf-step id="2006" wfId="2000" isAxisX="false"/>
				
				
				
				
				outBuffer.flip();
				testDatagram.send(outBuffer);
				
				Thread.sleep(20);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
		
	}

}
