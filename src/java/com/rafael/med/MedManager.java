package com.rafael.med;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rafael.med.Datagram.Listener;
import com.rafael.med.common.Utilities;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
	public MedData data;

	private static final Path excelPath = Paths.get("excel");

	private AtomicBoolean isDepartmentsViewFilled = new AtomicBoolean(false);
	private ByteBuffer excelBuffer = ByteBuffer.allocate(10 * 1024 * 1024);
	
	private Map<Device, FileChannel> files = new HashMap<>();

	public void init(MainView mainView) throws Exception 
	{
		this.data 			= new MedData();
		this.mainView 		= mainView;
		mainView.buildView(data);
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent ae)
			{
				if(isDepartmentsViewFilled.compareAndSet(false, true))
				{
					for (Department department : data.departments.values())
					{
						for (Room room : department.rooms) 
						{
							for (Bed bed : room.beds)
							{
								department.view.addBed(bed);
							}
						}
					}
				}
				updateCenterView(false);
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
		int excelPeriodInMinutes = data.excelPeriodInMinutes;
		log.info("EXPORT TO EXCEL EVERY {} MINUTES", excelPeriodInMinutes);
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run()
			{
				readLock.lock();
				try
				{
					long begin = System.nanoTime();
					toExcel();
					log.debug("export to excel  finshed during {} ms",  ((System.nanoTime() - begin) / 1_000_000) );
				}
				catch (Throwable e) 
				{
					log.error("FAILED EXCEL PERIODIC ACTION - ",e);
				}
				finally
				{
					readLock.unlock();
				}
			}
		}, excelPeriodInMinutes, excelPeriodInMinutes, TimeUnit.MINUTES);
		
		Files.createDirectories(excelPath);
	}
	
	public void updateCenterView(boolean isToFront) 
	{
		readLock.lock();
		try
		{			
			if(mainView.currentView != null)
			{
				mainView.currentView.update(isToFront);
//				log.trace("update view = " + mainView.currentView);
			}
			
//			for (Department department : data.departments.values())
//			{
//				department.view.update();
//			}
//
//			mainView.detailsView.update();
//			mainView.emergencyView.update();
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
	
	public void showDetails(Bed bed)
	{
		mainView.detailsView.setBed(bed);
		mainView.showDetailsView(bed.getFullName());
	}
	
	public void showSettingsForBed(Bed bed)
	{
		mainView.settingView.goToBed(bed);
		mainView.showDetailsView(bed.getFullName());
	}

	public void addBedToEmergency(Bed bed) 
	{
		mainView.emergencyView.addBed(bed);
	}
	
	public void toExcel() throws Exception
	{
		LocalDateTime now = LocalDateTime.now();
		
		
		for (Department department : data.departments.values())
		{
			for (Room room : department.rooms)
			{
				for (Bed bed : room.beds) 
				{
//					//temp -------------------------------DELETE 
//					if(bed.id == 1001)
//					{
//						Device prototype = data.allDevices.get(2);
//						Device device = new Device(prototype, "123456");
//						bed.devices.put("123456", device);
//						for (int i = 0; i < 100; i++) 
//						{
//							long t = System.currentTimeMillis();
//							device.timestamps.add(t);
//							for (Param param : device.params.values())
//							{
//								param.records.put(t, "string-" + i);
//							}
//							Thread.sleep(5);
//						}
//						
//						
//						
//						Device prototype1 = data.allDevices.get(100);
//						Device device1 = new Device(prototype1, "001200");
//						bed.devices.put("001200", device1);
//					}
//					else if(bed.id == 1002)
//					{
//						Device prototype = data.allDevices.get(2);
//						Device device = new Device(prototype, "777777");
//						bed.devices.put("777777", device);
//					}		
//					
//					// ---------------------------- DELETE
					
					for (Device device : bed.devices.values())
					{
						if(device != null)
						{
							FileChannel fileChannel = files.get(device);
							StringBuilder builder = new StringBuilder();
							if(fileChannel == null)
							{
								Path directories = Paths.get(excelPath.toString(), department.id, "חדר " + room.id, "מיטה -" + bed.number + "_" + bed.id);
								Files.createDirectories(directories);
								Path excelPath = Paths.get(directories.toString(), device.name +"-" + device.serial + "_" + now.format(Utilities.DATE_TIME_yyyy_MM_dd_HH_mm_ss) +".csv");
								fileChannel = FileChannel.open(excelPath,StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE,StandardOpenOption.DSYNC);
								files.put(device, fileChannel);
								
								builder.append("Timestamp,");
								
								for (Param param : device.params.values())
								{
									if(param != null)
									{
										builder.append(param.name).append(",");
									}
								}
								builder.deleteCharAt(builder.lastIndexOf(","));
								builder.append(StringUtils.LF);
							}
							
							
							for (Long timestamp : device.timestamps)
							{
								LocalDateTime ldt = Instant.ofEpochMilli(timestamp.longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
								String date  = ldt.format(Utilities.DATE_TIME_dd_MM_yyyy_HH_mm_ss_SSS);
								builder.append(date).append(",");
								for (Param param : device.params.values())
								{
									if(param != null)
									{
										Object value = param.records.get(timestamp);
										if(value != null)
										{
											builder.append(value);
										}
										builder.append(",");
										
									}
								}
								builder.deleteCharAt(builder.lastIndexOf(","));
								builder.append(StringUtils.LF);
							}
							
							
							excelBuffer.clear();
							byte[] bytes = builder.toString().getBytes();
							excelBuffer.put(bytes);
							excelBuffer.flip();
							
							fileChannel.write(excelBuffer, fileChannel.size());
							
							device.clearRecording();
							
						}
					}
				}
			}
		}
	}


	
}
