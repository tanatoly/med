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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Datagram
{
	private static final Logger log = LogManager.getLogger();
	
    public static final String DATAGRAM = "DATAGRAM";
    public static final int CAPACITY = 64000;
    public static final int PORT = 27027;
    public static final ByteOrder BIG_ENDIAN = ByteOrder.LITTLE_ENDIAN;

    public interface Listener
    {
        public void onIncomingMessage(ByteBuffer buffer, InetSocketAddress source) throws Exception;
    }

    private ByteBuffer inBuffer;
    private AtomicBoolean isRunning;
    private DatagramChannel channel;
    private InetSocketAddress localAddress;
    private Listener listener;

    private ExecutorService receiverExecutor;

    public Datagram(Listener listener)
    {
        this.isRunning          = new AtomicBoolean(false);
        this.listener           = listener;
        this.inBuffer           = ByteBuffer.allocate(CAPACITY).order(BIG_ENDIAN);
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
           
            if(inBuffer != null)
            {
                receiverExecutor = Executors.newSingleThreadExecutor();
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
                                if(inBuffer.position() > 0)
                                {
                                    inBuffer.flip();
                                }
                                int limit = inBuffer.limit();
                                listener.onIncomingMessage(inBuffer,remoteAddress);
                                inBuffer.limit(limit);
                                inBuffer.position(limit);

                            }
                            catch (Exception e)
                            {
                            	log.error("FAILED HANDLE MESSAGE - ",e);
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
            isRunning.set(false);
            if(channel != null)
            {
                channel.close();
            }
            if(receiverExecutor != null)
            {
                receiverExecutor.shutdownNow();
            }
        }
        catch (Exception e)
        {
        	log.error("FAILED CLOSE - ",e);
        }
    }

    public int send(ByteBuffer buffer, InetSocketAddress target) throws Exception
    {
        int sendBytes = 0;
        if(isRunning.get())
        {
            if(buffer.position() > 0)
            {
                buffer.flip();
            }
            int limit = buffer.limit();

            sendBytes = channel.send(buffer,target);
            if(sendBytes < limit)
            {
                throw new Exception("failed send all bytes - sendbytes = " + sendBytes + " bufferLimit = " + limit);
            }
        }
        return sendBytes;
    }
}
