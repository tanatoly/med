package com.rafael.med;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.Datagram.Listener;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class MedManager 
{
	private static final Logger log = LogManager.getLogger();
	
	
	public static final MedManager INSTANCE = new MedManager();
	
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private Lock readLock				= readWriteLock.readLock();
	private Lock writeLock				= readWriteLock.writeLock();
	
	
	private MainView mainView;
	private MedData data;


	private DetailsView detailsView;

	public void init(MainView mainView) throws Exception 
	{
		this.data 			= new MedData();
		this.mainView 		= mainView;
		mainView.buildView(data);
		this.detailsView 	= new DetailsView(mainView, mainView.center);
		
		Iterator<Bed> it = data.allBeds.values().iterator();
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent ae)
			{
				readLock.lock();
				try
				{
					for (RegularModule module : mainView.thinModules)
					{
						module.update();
					}
					detailsView.update();
					mainView.emergencyView.update();
//					log.debug("-------------------");
//					Bed next = it.next();
//					next.firstTime.set(System.currentTimeMillis());
//					mainView.emergencyView.addBed(next);
				}
				catch (Throwable e) 
				{
					log.error("FAILED PERIODIC ACTION - ",e);
				}
				finally
				{
					readLock.unlock();
				}
		    }
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		
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
	}
	
	
	public void showDetails(Bed bed)
	{
		if(bed != null)
		{
			detailsView.setBed(bed);
			detailsView.show();
		}
	}


	public void addToEmergency(Bed bed) 
	{
		mainView.emergencyView.addBed(bed);
	}
}
