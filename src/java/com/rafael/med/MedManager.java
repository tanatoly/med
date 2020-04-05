package com.rafael.med;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.Datagram.Listener;
import com.rafael.med.MedData.Bed;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class MedManager 
{
	private static final Logger log = LogManager.getLogger();
	
	
	public static final MedManager INSTANCE = new MedManager();
	
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private Lock readLock				= readWriteLock.readLock();
	private Lock writeLock				= readWriteLock.writeLock();
	
	private ScheduledService<Void> scheduledService;
	
	private MainView mainView;
	private MedData data;


	private DetailsView detailsView;
	private final List<AlarmModule> alarmModules = new ArrayList<>();

	public void init(MainView mainView) throws Exception 
	{
		this.data 			= new MedData();
		this.mainView 		= mainView;
		mainView.buildView(data);
		this.detailsView 	= new DetailsView(mainView, mainView.center);
		
		
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
						readLock.lock();
						try
						{
							for (ThinModule module : mainView.thinModules)
							{
								module.onTimeClick();
							}
							detailsView.onTimeClick();
						}
						catch (Throwable e) 
						{
							log.error("FAILED PERIODIC ACTION - ",e);
						}
						finally
						{
							readLock.unlock();
						}
						return null;
					}
				};
			}
		};		
		
		
		Datagram receiver = new Datagram(new Listener()
		{
			@Override
			public void onIncomingMessage(ByteBuffer buffer, InetSocketAddress source)
			{
				//log.debug("RECEIVED MESSAGE FROM {} , BUFFER = {}", source, buffer);
				int bedId = 0;
				
				writeLock.lock();
				try 
				{
					bedId = buffer.getInt();
					data.handleMessage(buffer, bedId);
				} 
				catch (Exception e)
				{
					log.error("FAILED HANDLE MESSAGE FROM SOURCE = {} AND BED_ID = {}",source, bedId, e);
				}
				finally
				{
					writeLock.unlock();
				}
			}
		});
		receiver.open();
		scheduledService.setDelay(Duration.seconds(1));
		scheduledService.setPeriod(Duration.seconds(2));
		scheduledService.start();
	}
	
	
	public void showDetails(Bed bed)
	{
		if(bed != null)
		{
			detailsView.setBed(bed);
			detailsView.show();
		}
	}


	public void addAlarmBed(Bed bed) 
	{
		if(!data.emergencyBeds.contains(bed))
		{
			data.emergencyBeds.add(0, bed);
		}
		
		
	}
}
