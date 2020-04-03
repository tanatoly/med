package com.rafael.med.spect;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.common.BitByteUtils;
import com.rafael.med.common.Datagram;
import com.rafael.med.common.Datagram.Listener;
import com.rafael.med.common.Utilities;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class SpectManager 
{
	private static final Logger log = LogManager.getLogger();

	public static final SpectManager INSTANCE = new SpectManager();
	
	private static final ByteOrder 			BYTE_ORDER 	= ByteOrder.LITTLE_ENDIAN;
	private static final int		 		IN_CAPACITY = 65500;
	private static final int		 		LOCAL_PORT 	= 14142;
	private static final String				MULTICAST 	= "228.10.10.12";
	
	
	
	private final List<SpectPane> spects 						= new ArrayList<>();
	private final Map<NodeOpcode, SpectPane> uniqueNodeOpcode 	= new HashMap<>();
	
	private final ScheduledService<Void> scheduledService;
	
	private long secondsCounter = 0;
	
	private Object mutex = new Object();
	
	private SpectManager()
	{
		scheduledService = new ScheduledService<Void>() 
		{
			@Override
			protected Task<Void> createTask() 
			{
				return new Task<Void>()
				{
					@Override
					protected Void call() throws Exception
					{	
						synchronized (mutex)
						{
							try
							{
								//System.out.println("rendering start - " + Instant.now());
								//long b = System.nanoTime();
								for (SpectPane spect : spects)
								{
									if(spect.isRunning.get() && secondsCounter%spect.refreshInSec.get() == 0)
									{
										List<SpectSegment> next = null;
										for (int i = spect.segments.size() - 1; i >= 0; i--) 
										{
											List<SpectSegment> current = spect.segments.get(i);
											if(next != null)
											{
												for (int j = 0; j < current.size(); j++) 
												{
													SpectSegment currSegment = current.get(j);
													SpectSegment nextSegment = next.get(j);
													nextSegment.setBackground(currSegment.getBackground());	
												}
												if(i == 0)
												{
													for (int j = 0; j < current.size(); j++)  
													{
														SpectSegment currSegment = current.get(j);
														currSegment.setSegmentValue();
													}
												}
											}
											next = current;
										}
									}
								}
								secondsCounter++;
								//System.out.println("rendering end - " + (System.nanoTime() - b)/ 1_000_000);
							}
							catch (Throwable e) 
							{
								//AppManager.INSTANCE.showError(AppSpect.class,log,"ON ACTION FAILED : ",e);
							}
							return null;
						}
					}
				};
			}
		};		
	}

	
	public void addSpect(SpectPane spect)
	{
		synchronized (mutex)
		{
			NodeOpcode unique = new NodeOpcode(spect.nodeId,spect.opcode);
			if(!uniqueNodeOpcode.containsKey(unique))
			{
				uniqueNodeOpcode.put(unique, spect);
				spects.add(spect);
			}
		}
	}


	public boolean isExists(int nodeId, int opcode) 
	{
		NodeOpcode unique = new NodeOpcode(nodeId,opcode);
		return uniqueNodeOpcode.containsKey(unique);
	}

	public void start(String localIp, NetworkInterface networkInterface)
	{
		try 
		{
			InetAddress localHost = null;
			if(StringUtils.isNotBlank(localIp))
			{
				localHost = InetAddress.getByName(localIp);
			}
			else
			{
				localHost = Utilities.getLocalHost();
			}
			
			
			if(networkInterface == null)
			{
				throw new IllegalStateException("NOT FOUND INTERFACE FOR LOCAL HOST = " + localHost);
			}
			
			InetAddress multicast = null;
		
			multicast = InetAddress.getByName(MULTICAST);
			Datagram receiver = new Datagram(IN_CAPACITY, BYTE_ORDER, networkInterface, new InetSocketAddress(localHost, LOCAL_PORT), null, multicast);
			receiver.setListener(new Listener() 
			{
				@Override
				public void onIncomingMessage(ByteBuffer message, InetSocketAddress source) throws Exception
				{					
					int nodeId = BitByteUtils.toUnsignedShort(message.getShort());
					int opcode = BitByteUtils.toUnsignedByte(message.get());
					
					boolean isMyOpcode = false;
					for (int i = 0; i < ManetType.values().length && !isMyOpcode; i++)
					{
						isMyOpcode = (ManetType.values()[i].opcode == opcode);
					}
					
					if(isMyOpcode)
					{
						NodeOpcode unique = new NodeOpcode(nodeId,opcode);
						SpectPane spectPane = uniqueNodeOpcode.get(unique);
						if(spectPane != null)
						{
							synchronized (mutex)
							{
								try
								{
									//System.out.println("message start");
									//long b = System.nanoTime();
									
									message.get();
									message.getLong();
									int numberOfChannels = message.getInt();
									
									for (int i = 0; i < numberOfChannels; i++) 
									{
										int ok 		= message.getShort();
										int fail	= message.getShort();
				
										SpectChannel spectChannel = spectPane.channels[i];
										spectChannel.setValue(ok, fail);
									}
									//System.out.println("message end - " + (System.nanoTime() - b)/ 1_000_000);
								}
								catch (Throwable e) 
								{
									//AppManager.INSTANCE.showError(AppSpect.class,log,"ON ACTION FAILED : ",e);
								}
							}
						}
					}
				}
			});
			receiver.open("receiver");
			System.out.println("RECEIVER OPENED ON HOST = " + localHost + ", PORT = " + LOCAL_PORT + ", MULTICAST = " + MULTICAST);
			
			scheduledService.setDelay(Duration.seconds(1));
			scheduledService.setPeriod(Duration.seconds(1));
			scheduledService.start();
		} 
		catch (Exception e) 
		{
			//AppManager.INSTANCE.showError(AppSpect.class,log,"ON ACTION FAILED : ",e);
		}
	}
}
