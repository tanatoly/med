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
	
	private static int addParam(ByteBuffer buffer ,  int id, int type, double value)
	{
		buffer.putShort((short) id);
		buffer.put((byte) type);
		if(type == 2)
		{
			buffer.putInt((int) value);
		}
		else
		{
			buffer.putFloat((float) value);
		}
		return 1;
	}
	
	private static int addChart1(ByteBuffer buffer, int id, int stepX)
	{
		addParam(buffer, id + 1, 1, 0);
		addParam(buffer, id + 2, 1, 10_000);
		addParam(buffer, id + 3, 1, -1.0);
		addParam(buffer, id + 4, 1, 1.0);
		addParam(buffer, id + 5, 1, stepX);
		addParam(buffer, id + 6, 1, 0.1);
		return 6;
	}
	
//	private static int addChart2(ByteBuffer buffer)
//	{
//		addParam(buffer, 2001, 2, 0);
//		addParam(buffer, 2002, 2, 1000);
//		addParam(buffer, 2003, 2, 0);
//		addParam(buffer, 2004, 2, 100);
//		addParam(buffer, 2005, 2, 50);
//		addParam(buffer, 2006, 2, 1);
//		return 6;
//	}
//	
//	private static int addChart3(ByteBuffer buffer)
//	{
//		addParam(buffer, 3001, 2, 0);
//		addParam(buffer, 3002, 2, 1000);
//		addParam(buffer, 3003, 2, 0);
//		addParam(buffer, 3004, 2, 50);
//		addParam(buffer, 3005, 2, 50);
//		addParam(buffer, 3006, 2, 1);
//		return 6;
//	}
	
	
	
	public static void loop() throws Exception
	{
		TestDatagram testDatagram = new TestDatagram();
		testDatagram.open();
		
		ByteBuffer outBuffer = ByteBuffer.allocate(TestDatagram.CAPACITY).order(TestDatagram.BIG_ENDIAN);
		
		int count = 0;
		
		int angle = 0;
		
		int moudles = 10;
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
				
				
				
				//System.out.println(outBuffer);
				
				int paramsSize = 0;
				outBuffer.put((byte) 0);
				
				int stepX = 70;
				paramsSize += addChart1(outBuffer,1000,stepX);
				paramsSize += addChart1(outBuffer,2000,stepX);
				paramsSize += addChart1(outBuffer,3000,stepX);
				
				
				double random =  Math.sin(Math.toRadians(angle+=2));
				
				
				if(count%1000 == 0)
				{
					moudles = RandomUtils.nextInt(2, 20);
				}
				
				if(count%moudles == 0)
				{
					paramsSize +=addParam(outBuffer, 1007, 1, random);
				}
				if(count%moudles == 0)
				{
					paramsSize +=addParam(outBuffer, 2007, 1, random);
				}
				if(count%moudles == 0)
				{
					paramsSize +=addParam(outBuffer, 3007, 1, random);
				}
				if(count%50 == 0)
				{
					paramsSize +=addParam(outBuffer, 4, 2, RandomUtils.nextInt(1000, 2000));
					paramsSize +=addParam(outBuffer, 0, 2, RandomUtils.nextInt(100, 200));
				}
					
				//System.out.println("---- " + paramsSize);
				
				outBuffer.put(13, (byte) paramsSize);
				
				outBuffer.flip();
				testDatagram.send(outBuffer);
				
				Thread.sleep(10);
				count++;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception 
	{
		
		loop();
		
		
//		TestDatagram testDatagram = new TestDatagram();
//		testDatagram.open();
//		
//		ByteBuffer outBuffer = ByteBuffer.allocate(TestDatagram.CAPACITY).order(TestDatagram.BIG_ENDIAN);
//		
//	
//		
//		
//		while(true)
//		{
//			try 
//			{
//				outBuffer.clear();
//				outBuffer.putInt(1001);
//				outBuffer.put((byte) 2);
//				
//				String serial = "8888888";
//				outBuffer.put((byte) serial.length());
//				outBuffer.put(serial.getBytes());
//				
//				
//				byte paramsSize = 6 * 3 + 2 + 2 + 1;
//				outBuffer.put((byte) paramsSize);
//				
//				
//				
//				addChart1(outBuffer);
//				addChart2(outBuffer);
//				addChart3(outBuffer);
//				
//				addParam(outBuffer, 1007, 2, RandomUtils.nextInt(0, 50));
//				addParam(outBuffer, 2007, 2, RandomUtils.nextInt(0, 50));
//				addParam(outBuffer, 3007, 2, RandomUtils.nextInt(0, 50));
//				
//				addParam(outBuffer, 4, 2, RandomUtils.nextInt(1000, 2000));
//				addParam(outBuffer, 0, 2, RandomUtils.nextInt(100, 200));
//					
//				
//				
//				
//				
//				outBuffer.flip();
//				testDatagram.send(outBuffer);
//				
//				Thread.sleep(200);
//			} 
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//			}
//		}
		
		
	}

}
