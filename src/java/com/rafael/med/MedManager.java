package com.rafael.med;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.Datagram.Listener;
import com.rafael.med.common.Constants;
import com.rafael.med.common.ViewUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
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
	private AtomicBoolean isDepartmentsViewFilled = new AtomicBoolean(false);

	public void init(MainView mainView) throws Exception 
	{
		this.data 			= new MedData();
		this.mainView 		= mainView;
		mainView.buildView(data);
		this.detailsView 	= new DetailsView(mainView, mainView.center);
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent ae)
			{
				readLock.lock();
				try
				{
					if(isDepartmentsViewFilled.compareAndSet(false, true))
					{
						for (Department department : data.departments.values())
						{
							if(department != null)
							{
								for (Room room : department.rooms) 
								{
									if(room != null)
									{
										for (Bed bed : room.beds)
										{
											if(bed != null)
											{
												department.view.addBed(bed);
											}
										}
									}
								}
							}
						}
					}
					
					for (Department department : data.departments.values())
					{
						department.view.update();
					}

					detailsView.update();
					mainView.emergencyView.update();
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


	public void removeFromEmergency(Bed bed)
	{
		//mainView.emergencyView.removeBed(bed);
		
	}
}
